package com.thinkgem.jeesite.modules.conf.util;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.conf.entity.SysConf;
import com.thinkgem.jeesite.modules.conf.service.SysConfService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Leon.wang on 2016/12/13.
 */
public final class SysConfUtil {

    protected Logger logger = LoggerFactory.getLogger(SysConfUtil.class);

    private static SysConfUtil instance = new SysConfUtil();

    public final Properties properties = new Properties();

    public Map<String, String> configs = new HashMap<String, String>();

    private SysConfService sysConfService;

    private SysConfUtil() {
    }

    public static SysConfUtil getInstance() {
        return instance;
    }

    /**
     * 加载数据库数据,遍历properties中的数据 没有的初始化到数据库
     * 将数据写入到一个map里面.
     */
    public void loadConfig() {
        configs = getSysConfService().loadConfigFromDB();
        loadProperties();
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            //将配置写入数据库并添加到缓存.
            SysConf conf = new SysConf();
            conf.setName(name);
            conf.setValue(value);
            conf.setDelFlag("0");
            getSysConfService().save(conf);
            configs.put(name, value);
        }
    }

    private void loadProperties() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("conf/sys.config.properties");
            if (is != null) {
                properties.load(is);
            }
        } catch (FileNotFoundException e) {
            logger.error("Can't not load the file 'sys.config.properties' :" + e.getMessage());
        } catch (IOException e) {
            logger.error("IOException when loading  'sys.config.properties'" + e.getCause());
        }
    }

    public String getValue(String name) {
        if (MapUtils.isEmpty(configs)) {
            loadConfig();
        }
        return configs.get(name);
    }

    private SysConfService getSysConfService() {
        if (sysConfService == null) {
            sysConfService = SpringContextHolder.getBean(SysConfService.class);
        }
        return sysConfService;
    }

    /**
     * 获取附件保存路径
     *
     * @return String
     */
    public String getAttachmentFileDir() {
        String filePath = getValue("attachment_file_path");
        if (!filePath.endsWith(File.separator) && !filePath.endsWith("/")) {
            filePath += File.separator;
        }
        return filePath;
    }

    /**
     * 获取附件的URL访问地址
     *
     * @param fileName 文件名
     * @param filePath 文件路径，包括文件名
     * @return String
     */
    public String getAttachmentFileURL(String fileName, String filePath) {
        String url;
        //获取文件URL路径
        String fileUrlPrefix = SysConfUtil.getInstance().getValue("attachment_url_prefix");
        if (StringUtils.isNotEmpty(fileUrlPrefix)) {
            if (!fileUrlPrefix.endsWith("/")) {
                fileUrlPrefix += "/";
            }
            url = fileUrlPrefix + fileName;
        } else {
            String homeUrl = Global.getHomeUrl();
            if (!homeUrl.endsWith("/")) {
                homeUrl += "/";
            }
            url = homeUrl + "api/m/img/showImg?imgFile=" + filePath;
        }
        return url;
    }
}
