package com.zzb.qinzhusocial.bean;

/**
 * Created by 张梓彬 on 2017/7/6 0006.
 */

public class NoticeBean {
    public String name;
    public String _id;
    public String reason;
    public String isAgree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(String isAgree) {
        this.isAgree = isAgree;
    }
}
