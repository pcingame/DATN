package com.example.appchatdemo.model;

import java.io.Serializable;

public class MemberModel implements Serializable {

    private String membername, memberid, memberurl;

    public MemberModel(String membername, String memberid, String memberurl) {
        this.membername = membername;
        this.memberid = memberid;
        this.memberurl = memberurl;
    }

    public MemberModel() {
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getMemberurl() {
        return memberurl;
    }

    public void setMemberurl(String memberurl) {
        this.memberurl = memberurl;
    }
}
