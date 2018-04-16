package com.ckfinder.connector.handlers.command;

import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.configuration.Events.EventTypes;
import com.ckfinder.connector.data.AfterFileUploadEventArgs;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.errors.ErrorUtils;
import com.ckfinder.connector.handlers.command.Command;
import com.ckfinder.connector.handlers.command.IPostCommand;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.ImageUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.common.utils.IdGen;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.IOFileUploadException;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Created by Leon.wang on 2017/1/17.
 */
public class FileUploadCommand extends Command implements IPostCommand {
    protected String fileName = "";
    protected String newFileName = "";
    protected String ckEditorFuncNum;
    protected String responseType;
    protected String ckFinderFuncNum;
    private String langCode;
    protected boolean uploaded;
    protected int errorCode = 0;
    private static final char[] UNSAFE_FILE_NAME_CHARS = new char[]{':', '*', '?', '|', '/'};

    public FileUploadCommand() {
        this.type = "";
        this.uploaded = false;
    }

    public void execute(OutputStream out) throws ConnectorException {
        if(this.configuration.isDebugMode() && this.exception != null) {
            throw new ConnectorException(this.errorCode, this.exception);
        } else {
            try {
                String e = this.errorCode == 0?"":ErrorUtils.getInstance().getErrorMsgByLangAndCode(this.langCode, this.errorCode, this.configuration);
                e = e.replaceAll("%1", this.newFileName);
                String path = "";
                if(!this.uploaded) {
                    this.newFileName = "";
                    this.currentFolder = "";
                } else {
                    path = ((ResourceType)this.configuration.getTypes().get(this.type)).getUrl() + this.currentFolder;
                }

                if(this.responseType != null && this.responseType.equals("txt")) {
                    out.write((this.newFileName + "|" + e).getBytes("UTF-8"));
                } else {
                    out.write("<script type=\"text/javascript\">".getBytes("UTF-8"));
                    if(this.checkFuncNum()) {
                        this.handleOnUploadCompleteCallFuncResponse(out, e, path);
                    } else {
                        this.handleOnUploadCompleteResponse(out, e);
                    }

                    out.write("</script>".getBytes("UTF-8"));
                }

            } catch (IOException var4) {
                throw new ConnectorException(104, var4);
            }
        }
    }

    protected boolean checkFuncNum() {
        return this.ckFinderFuncNum != null;
    }

    protected void handleOnUploadCompleteCallFuncResponse(OutputStream out, String errorMsg, String path) throws IOException {
        this.ckFinderFuncNum = this.ckFinderFuncNum.replaceAll("[^\\d]", "");
        out.write(("window.parent.CKFinder.tools.callFunction(" + this.ckFinderFuncNum + ", \'" + path + FileUtils.backupWithBackSlash(this.newFileName, "\'") + "\', \'" + errorMsg + "\');").getBytes("UTF-8"));
    }

    protected void handleOnUploadCompleteResponse(OutputStream out, String errorMsg) throws IOException {
        out.write("window.parent.OnUploadCompleted(".getBytes("UTF-8"));
        out.write(("\'" + FileUtils.backupWithBackSlash(this.newFileName, "\'") + "\'").getBytes("UTF-8"));
        out.write((", \'" + (this.errorCode != 0?errorMsg:"") + "\'").getBytes("UTF-8"));
        out.write(");".getBytes("UTF-8"));
    }

    public void initParams(HttpServletRequest request, IConfiguration configuration, Object... params) throws ConnectorException {
        super.initParams(request, configuration, params);
        this.ckFinderFuncNum = request.getParameter("CKFinderFuncNum");
        this.ckEditorFuncNum = request.getParameter("CKEditorFuncNum");
        this.responseType = request.getParameter("response_type");
        this.langCode = request.getParameter("langCode");
        if(this.errorCode == 0) {
            this.uploaded = this.uploadFile(request);
        }

    }

    private boolean uploadFile(HttpServletRequest request) {
        if(!AccessControlUtil.getInstance(this.configuration).checkFolderACL(this.type, this.currentFolder, this.userRole, 32)) {
            this.errorCode = 103;
            return false;
        } else {
            return this.fileUpload(request);
        }
    }

    private boolean fileUpload(HttpServletRequest request) {
        try {
            DiskFileItemFactory e = new DiskFileItemFactory();
            ServletFileUpload uploadHandler = new ServletFileUpload(e);
            List items = uploadHandler.parseRequest(request);
            Iterator i$ = items.iterator();

            while(true) {
                FileItem item;
                do {
                    if(!i$.hasNext()) {
                        return false;
                    }

                    item = (FileItem)i$.next();
                } while(item.isFormField());

                String path = ((ResourceType)this.configuration.getTypes().get(this.type)).getPath() + this.currentFolder;
                this.fileName = this.getFileItemName(item);

                try {
                    if(this.validateUploadItem(item, path)) {
                        boolean var8 = this.saveTemporaryFile(path, item);
                        return var8;
                    }
                } finally {
                    item.delete();
                }
            }
        } catch (InvalidContentTypeException var18) {
            if(this.configuration.isDebugMode()) {
                this.exception = var18;
            }

            this.errorCode = 204;
            return false;
        } catch (IOFileUploadException var19) {
            if(this.configuration.isDebugMode()) {
                this.exception = var19;
            }

            this.errorCode = 104;
            return false;
        } catch (SizeLimitExceededException var20) {
            this.errorCode = 203;
            return false;
        } catch (FileSizeLimitExceededException var21) {
            this.errorCode = 203;
            return false;
        } catch (ConnectorException var22) {
            this.errorCode = var22.getErrorCode();
            return false;
        } catch (Exception var23) {
            if(this.configuration.isDebugMode()) {
                this.exception = var23;
            }

            this.errorCode = 104;
            return false;
        }
    }

    private boolean saveTemporaryFile(String path, FileItem item) throws Exception {
        File file = new File(path, this.newFileName);
        AfterFileUploadEventArgs args = new AfterFileUploadEventArgs();
        args.setCurrentFolder(this.currentFolder);
        args.setFile(file);
        args.setFileContent(item.get());
        if(!ImageUtils.isImage(file)) {
            item.write(file);
            if(this.configuration.getEvents() != null) {
                this.configuration.getEvents().run(EventTypes.AfterFileUpload, args, this.configuration);
            }

            return true;
        } else if(ImageUtils.checkImageSize(item.getInputStream(), this.configuration)) {
            ImageUtils.createTmpThumb(item.getInputStream(), file, this.getFileItemName(item), this.configuration);
            if(this.configuration.getEvents() != null) {
                this.configuration.getEvents().run(EventTypes.AfterFileUpload, args, this.configuration);
            }

            return true;
        } else if(this.configuration.checkSizeAfterScaling()) {
            ImageUtils.createTmpThumb(item.getInputStream(), file, this.getFileItemName(item), this.configuration);
            if(FileUtils.checkFileSize((ResourceType)this.configuration.getTypes().get(this.type), file.length())) {
                if(this.configuration.getEvents() != null) {
                    this.configuration.getEvents().run(EventTypes.AfterFileUpload, args, this.configuration);
                }

                return true;
            } else {
                file.delete();
                this.errorCode = 203;
                return false;
            }
        } else {
            return false;
        }
    }

    private String getFinalFileName(String path, String name) {
        File file = new File(path, name);

        for(int number = 0; file.exists(); this.errorCode = 201) {
            ++number;
            StringBuilder sb = new StringBuilder();
            sb.append(FileUtils.getFileNameWithoutExtension(name));
            sb.append("(" + number + ").");
            sb.append(FileUtils.getFileExtension(name));
            this.newFileName = sb.toString();
            file = new File(path, this.newFileName);
        }

        return this.newFileName;
    }

    private boolean validateUploadItem(FileItem item, String path) {
        if(item.getName() != null && item.getName().length() > 0) {
            this.fileName = this.getFileItemName(item);
            // 更改文件名称
            //this.newFileName = this.fileName;
            String extension = FileUtils.getFileExtension(this.fileName);
            this.newFileName = IdGen.uuid() + "." + extension;

            char[] checkFileExt = UNSAFE_FILE_NAME_CHARS;
            int e = checkFileExt.length;

            for(int i$ = 0; i$ < e; ++i$) {
                char c = checkFileExt[i$];
                this.newFileName = this.newFileName.replace(c, '_');
            }

            if(this.configuration.isDisallowUnsafeCharacters()) {
                this.newFileName = this.newFileName.replace(';', '_');
            }

            if(this.configuration.forceASCII()) {
                this.newFileName = FileUtils.convertToASCII(this.newFileName);
            }

            if(!this.newFileName.equals(this.fileName)) {
                this.errorCode = 207;
            }

            if(FileUtils.checkIfDirIsHidden(this.currentFolder, this.configuration)) {
                this.errorCode = 109;
                return false;
            } else if(FileUtils.checkFileName(this.newFileName) && !FileUtils.checkIfFileIsHidden(this.newFileName, this.configuration)) {
                int var9 = FileUtils.checkFileExtension(this.newFileName, (ResourceType)this.configuration.getTypes().get(this.type), this.configuration, true);
                if(var9 == 1) {
                    this.errorCode = 105;
                    return false;
                } else {
                    if(var9 == 2) {
                        this.newFileName = FileUtils.renameFileWithBadExt(this.newFileName);
                    }

                    try {
                        File var10 = new File(path, this.getFinalFileName(path, this.newFileName));
                        if(FileUtils.checkFileSize((ResourceType)this.configuration.getTypes().get(this.type), item.getSize()) || this.configuration.checkSizeAfterScaling() && ImageUtils.isImage(var10)) {
                            if(this.configuration.getSecureImageUploads() && ImageUtils.isImage(var10) && !ImageUtils.checkImageFile(item)) {
                                this.errorCode = 204;
                                return false;
                            } else if(!FileUtils.checkIfFileIsHtmlFile(var10.getName(), this.configuration) && FileUtils.detectHtml(item)) {
                                this.errorCode = 206;
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            this.errorCode = 203;
                            return false;
                        }
                    } catch (SecurityException var7) {
                        if(this.configuration.isDebugMode()) {
                            this.exception = var7;
                        }

                        this.errorCode = 104;
                        return false;
                    } catch (IOException var8) {
                        if(this.configuration.isDebugMode()) {
                            this.exception = var8;
                        }

                        this.errorCode = 104;
                        return false;
                    }
                }
            } else {
                this.errorCode = 102;
                return false;
            }
        } else {
            this.errorCode = 202;
            return false;
        }
    }

    public void setResponseHeader(HttpServletResponse response, ServletContext sc) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
    }

    private String getFileItemName(FileItem item) {
        Pattern p = Pattern.compile("[^\\\\/]+$");
        Matcher m = p.matcher(item.getName());
        return m.find()?m.group(0):"";
    }

    protected boolean checkParam(String reqParam) throws ConnectorException {
        if(reqParam != null && !reqParam.equals("")) {
            if(Pattern.compile("(/\\.|\\p{Cntrl}|//|\\\\|[:*?<>\"\\|])").matcher(reqParam).find()) {
                this.errorCode = 102;
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    protected boolean checkHidden() throws ConnectorException {
        if(FileUtils.checkIfDirIsHidden(this.currentFolder, this.configuration)) {
            this.errorCode = 109;
            return true;
        } else {
            return false;
        }
    }

    protected boolean checkConnector(HttpServletRequest request) throws ConnectorException {
        if(this.configuration.enabled() && this.configuration.checkAuthentication(request)) {
            return true;
        } else {
            this.errorCode = 500;
            return false;
        }
    }

    protected boolean checkIfCurrFolderExists(HttpServletRequest request) throws ConnectorException {
        String tmpType = this.getParameter(request, "type");
        File currDir = new File(((ResourceType)this.configuration.getTypes().get(tmpType)).getPath() + this.currentFolder);
        if(currDir.exists() && currDir.isDirectory()) {
            return true;
        } else {
            this.errorCode = 116;
            return false;
        }
    }
}
