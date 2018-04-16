package com.ckfinder.connector.handlers.command;

import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.ResourceType;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.handlers.command.Command;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import com.ckfinder.connector.utils.ImageUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Leon.wang on 2016/8/29.
 */
public class ThumbnailCommand extends Command {
    private String fileName;
    private File thumbFile;
    private String ifNoneMatch;
    private long ifModifiedSince;
    private HttpServletResponse response;
    private String fullCurrentPath;

    public ThumbnailCommand() {
    }

    public void setResponseHeader(HttpServletResponse response, ServletContext sc) {
        response.setHeader("Cache-Control", "public");
        String mimetype = this.getMimeTypeOfImage(sc, response);
        if(mimetype != null) {
            response.setContentType(mimetype.concat("; name='") + this.fileName + "'");
        } else {
            response.setContentType("name='" + this.fileName + "'");
        }

        this.response = response;
    }

    private String getMimeTypeOfImage(ServletContext sc, HttpServletResponse response) {
        if(this.fileName != null && !this.fileName.equals("")) {
            String tempFileName = this.fileName.substring(0, this.fileName.lastIndexOf(46) + 1).concat(FileUtils.getFileExtension(this.fileName).toLowerCase());
            String mimeType = sc.getMimeType(tempFileName);
            if(mimeType == null) {
                response.setStatus(500);
                return null;
            } else {
                return mimeType;
            }
        } else {
            response.setStatus(500);
            return null;
        }
    }

    public void execute(OutputStream out) throws ConnectorException {
        this.validate();
        this.createThumb();
        if(this.setResponseHeadersAfterCreatingFile()) {
            try {
                FileUtils.printFileContentToResponse(this.thumbFile, out);
            } catch (IOException var6) {
                if(this.configuration.isDebugMode()) {
                    throw new ConnectorException(var6);
                }

                try {
                    this.response.sendError(403);
                } catch (IOException var5) {
                    throw new ConnectorException(var5);
                }
            }
        } else {
            try {
                this.response.reset();
                this.response.sendError(304);
            } catch (IOException var4) {
                throw new ConnectorException(var4);
            }
        }

    }

    public void initParams(HttpServletRequest request, IConfiguration configuration, Object... params) throws ConnectorException {
        super.initParams(request, configuration, params);
        this.fileName = this.getParameter(request, "FileName");

        try {
            this.ifModifiedSince = Long.valueOf(request.getDateHeader("If-Modified-Since")).longValue();
        } catch (IllegalArgumentException var5) {
            this.ifModifiedSince = 0L;
        }

        this.ifNoneMatch = request.getHeader("If-None-Match");
    }

    private void validate() throws ConnectorException {
        if(!this.configuration.getThumbsEnabled()) {
            throw new ConnectorException(501);
        } else if(!AccessControlUtil.getInstance(this.configuration).checkFolderACL(this.type, this.currentFolder, this.userRole, 16)) {
            throw new ConnectorException(103);
        } else if(!FileUtils.checkFileName(this.fileName)) {
            throw new ConnectorException(109);
        } else if(FileUtils.checkIfFileIsHidden(this.fileName, this.configuration)) {
            throw new ConnectorException(117);
        } else {
            File typeThumbDir = new File(this.configuration.getThumbsPath() + File.separator + this.type);

            try {
                this.fullCurrentPath = typeThumbDir.getAbsolutePath() + this.currentFolder;
                if(!typeThumbDir.exists()) {
                    FileUtils.mkdir(typeThumbDir, this.configuration);
                }

            } catch (SecurityException var3) {
                throw new ConnectorException(104, var3);
            }
        }
    }

    private void createThumb() throws ConnectorException {
        this.thumbFile = new File(this.fullCurrentPath, this.fileName);

        try {
            if(!this.thumbFile.exists()) {
                File e = new File(((ResourceType)this.configuration.getTypes().get(this.type)).getPath() + this.currentFolder, this.fileName);
                if(!e.exists()) {
                    throw new ConnectorException(117);
                }

                try {
                    ImageUtils.createThumb(e, this.thumbFile, this.configuration);
                } catch (Exception var3) {
                    this.thumbFile.delete();
                    throw new ConnectorException(104, var3);
                }
            }

        } catch (SecurityException var4) {
            throw new ConnectorException(104, var4);
        }
    }

    private boolean setResponseHeadersAfterCreatingFile() throws ConnectorException {
        File file = new File(this.fullCurrentPath, this.fileName);

        try {
            String e = Long.toHexString(file.lastModified()).concat("-").concat(Long.toHexString(file.length()));
            if(e.equals(this.ifNoneMatch)) {
                return false;
            } else {
                this.response.setHeader("Etag", e);
                if(file.lastModified() <= this.ifModifiedSince) {
                    return false;
                } else {
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMMM yyyy HH:mm:ss z");
                    this.response.setHeader("Last-Modified", df.format(date));
                    this.response.setContentLength((int)file.length());
                    return true;
                }
            }
        } catch (SecurityException var5) {
            throw new ConnectorException(104, var5);
        }
    }
}
