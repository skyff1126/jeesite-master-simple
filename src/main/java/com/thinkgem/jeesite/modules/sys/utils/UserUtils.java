/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.utils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.security.Digests;
import com.thinkgem.jeesite.common.utils.*;
import com.thinkgem.jeesite.modules.log.entity.LoginLog;
import com.thinkgem.jeesite.modules.log.entity.OperationLog;
import com.thinkgem.jeesite.modules.log.service.LoginLogService;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.log.service.OperationLogService;
import com.thinkgem.jeesite.modules.sys.exception.AuthenException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.dao.MenuDao;
import com.thinkgem.jeesite.modules.sys.dao.OfficeDao;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import org.ietf.ldap.LDAPConnection;
import org.springframework.ldap.core.AuthenticationErrorCallback;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-12-05
 */
public class UserUtils {

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final String MD5_ALGORITHM = "MD5";
	public static final int HASH_INTERATIONS = 1024;

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static LdapTemplate ldapTemplate = SpringContextHolder.getBean(LdapTemplate.class);
	private static OperationLogService operationLogService = SpringContextHolder.getBean(OperationLogService.class);
	private static LoginLogService loginLogService = SpringContextHolder.getBean(LoginLogService.class);

	private static ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";
	
	public static final String CACHE_AUTH_INFO = "authInfo";
	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = userDao.get(id);
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_AUTH_INFO);
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}
	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		Principal principal = getPrincipal();
		if (principal!=null){
			User user = get(principal.getId());
			if (user != null){
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = roleDao.findAllList(new Role());
			}else{
				Role role = new Role();
				role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "u"));
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = menuDao.findAllList(new Menu());
			}else{
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = menuDao.findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取当前用户授权的区域
	 * @return
	 */
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
			areaList = areaDao.findAllList(new Area());
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}
	
	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = officeDao.findAllList(new Office());
			}else{
				Office office = new Office();
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = officeDao.findAllList(new Office());
		}
		return officeList;
	}
	
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null){
				return principal;
			}
//			subject.logout();
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
//		getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}
	
//	public static Map<String, Object> getCacheMap(){
//		Principal principal = getPrincipal();
//		if(principal!=null){
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}

	/**
	 * 根据账号获取用户
	 *
	 * @param account 账号（登录名 或 手机号 或 邮箱）
	 * @return
     */
	public static User findUserByAccount(String account) {
		return userDao.findByAccount(account);
	}

	/**
	 * 验证密码
	 *
	 * @param plainPassword 明文密码
	 * @param password      密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String passwordEncrptedType = PropertyUtils.getProperty("password.encrypted.type");
		if (passwordEncrptedType.equals(MD5_ALGORITHM)) {
			return password.equals(MD5Util.MD5(plainPassword));
		} else {
			byte[] salt = com.thinkgem.jeesite.common.utils.Encodes.decodeHex(password.substring(0, 16));
			byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
			return password.equals(com.thinkgem.jeesite.common.utils.Encodes.encodeHex(salt) + com.thinkgem.jeesite.common.utils.Encodes.encodeHex(hashPassword));
		}
	}

	/**
	 * 验证密码
	 *
	 * @param plainPassword 明文密码
	 * @param user     用户
	 * @return 验证成功返回true
	 */
	public static HResult validatePassword(String plainPassword, User user) {
		HResult hResult = new HResult();
		hResult.setResult(true);
		if (user == null) {
			hResult.setResult(false);
			hResult.setValue("未知账号");
		}
		user.setRoleList(roleDao.findList(new Role(user)));
		String delFlag = user.getDelFlag();
		if ("1".equals(delFlag)) {
			hResult.setResult(false);
			hResult.setValue("账号已经被删除");
		}
        /*if (User.STATUS_FREEZE.equals(user.getStatus())) {
            hResult.setResult(false);
            hResult.setValue("账号已经被禁用");
        }*/
		if (User.USERTYPE_DB.equals(user.getUserType())) {
			if (!validatePassword(plainPassword, user.getPassword())) {
				hResult.setResult(false);
				hResult.setValue("账号认证失败");
			}
		} else if (User.USERTYPE_AD.equals(user.getUserType())) {
			/*String host = PropertyUtils.getProperty("ad.host");
			Integer port = Integer.parseInt(PropertyUtils.getProperty("ad.port"));
			String domain = PropertyUtils.getProperty("ad.domain");

			LDAPConnection lc = new LDAPConnection();
			try {
				lc.connect(host, port);
				String loginName = user.getLoginName();
				lc.bind(com.novell.ldap.LDAPConnection.LDAP_V3, loginName.endsWith(domain) ? loginName : loginName + domain, plainPassword.getBytes());
			} catch (org.ietf.ldap.LDAPException e) {
				System.out.println("Error: " + e.toString());
				e.printStackTrace();
				hResult.setResult(false);
				hResult.setValue("账号认证失败");
			} finally {
				try {
					if (lc.isConnected()) {
						lc.disconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
			// 下面这种方式需要在spring-context-ldap.xml中配置
			AndFilter filter = new AndFilter();
			ldapTemplate.setIgnorePartialResultException(true);
			filter.and(new EqualsFilter("sAMAccountName", user.getLoginName()));
			final Exception[] exception = {null};
			boolean flag = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), plainPassword, new AuthenticationErrorCallback() {
				@Override
				public void execute(Exception e) {
					exception[0] = e;
				}
			});
			if (!flag) {
				hResult.setResult(false);
				hResult.setValue("账号认证失败");
			}
		} else if (User.USERTYPE_CAS.equals(user.getUserType())) {
			String server = Global.getConfig("cas.integrate.login.rest");
			String ticketGrantingTicket = CasServiceUtil.getTicketGrantingTicket(server, user.getLoginName(), plainPassword);
			if (ticketGrantingTicket == null) {
				hResult.setResult(false);
				hResult.setValue("账号认证失败");
			}
		} else {
			hResult.setResult(false);
			hResult.setValue("未知账号");
		}
		return hResult;
	}

	public static void addLoginLog(String type, String clientType) {
		User currentUser = UserUtils.getUser();
		LoginLog loginLog = new LoginLog();
		loginLog.setOfficeId(currentUser.getOffice().getId());
		loginLog.setOfficeName(currentUser.getOffice().getName());
		loginLog.setCompanyId(currentUser.getCompany().getId());
		loginLog.setCompanyName(currentUser.getCompany().getName());
		loginLog.setUserName(currentUser.getName());
		loginLog.setLoginName(currentUser.getLoginName());
		loginLog.setType(type);
		loginLog.setClientType(clientType);
		loginLog.setCreateDate(new Date());
		scheduledThreadPool.execute(new AsynAddLog(loginLogService, loginLog));
	}

	public static void addOperationLog(String menuName, String moduleName, String operation) {
		User currentUser = UserUtils.getUser();
		OperationLog operationLog = new OperationLog();
		operationLog.setOfficeId(currentUser.getOffice().getId());
		operationLog.setOfficeName(currentUser.getOffice().getName());
		operationLog.setCompanyId(currentUser.getCompany().getId());
		operationLog.setCompanyName(currentUser.getCompany().getName());
		operationLog.setUserName(currentUser.getName());
		operationLog.setLoginName(currentUser.getLoginName());
		operationLog.setMenuName(menuName);
		operationLog.setModuleName(moduleName);
		operationLog.setOperation(operation);
		operationLog.setCreateDate(new Date());
		scheduledThreadPool.execute(new AsynAddLog(operationLogService, operationLog));
	}
	
}
