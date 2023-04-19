package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class DocumentManageModel implements Serializable {
    private String extension;
    private String fileName;
    private String fileSize;
    private String verfy;
    private String userName;
    private String link;
    private String  attachment;
    private String  type;
    private String  status;

    public DocumentManageModel() {
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getVerfy() {
        return verfy;
    }

    public void setVerfy(String verfy) {
        this.verfy = verfy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
