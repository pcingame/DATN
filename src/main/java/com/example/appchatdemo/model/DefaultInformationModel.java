package com.example.appchatdemo.model;

public class DefaultInformationModel {
    private String emailInfo;
    private String nameInfo;

    public DefaultInformationModel() {
    }

    public DefaultInformationModel(String emailInfo, String nameInfo) {
        this.emailInfo = emailInfo;
        this.nameInfo = nameInfo;
    }

    public String getEmailInfo() {
        return emailInfo;
    }

    public void setEmailInfo(String emailInfo) {
        this.emailInfo = emailInfo;
    }

    public String getNameInfo() {
        return nameInfo;
    }

    public void setNameInfo(String nameInfo) {
        this.nameInfo = nameInfo;
    }
}
