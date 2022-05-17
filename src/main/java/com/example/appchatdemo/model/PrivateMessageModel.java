package com.example.appchatdemo.model;

import java.util.Date;

public class PrivateMessageModel {

    private String sender;
    private String receiver;
    private String time;
    private String message;
    private String file;
    private Date date;
    private String fileName;
    private String fileType;

    public PrivateMessageModel() {
    }

    public PrivateMessageModel(String sender, String receiver, String time, String message, String file, Date date, String fileName, String fileType) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.message = message;
        this.file = file;
        this.date = date;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
