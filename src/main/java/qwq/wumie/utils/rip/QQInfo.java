/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.rip;

public class QQInfo {
    String status;
    String message;
    String qq;
    String phone;
    String phonediqu;

    public QQInfo(String status, String message, String qq, String phone, String phonediqu) {
        this.status = status;
        this.message = message;
        this.qq = qq;
        this.phone = phone;
        this.phonediqu = phonediqu;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQq() {
        return this.qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhonediqu() {
        return this.phonediqu;
    }

    public void setPhonediqu(String phonediqu) {
        this.phonediqu = phonediqu;
    }
}
