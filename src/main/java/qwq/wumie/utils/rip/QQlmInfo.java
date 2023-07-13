/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.rip;

public class QQlmInfo {
    String status;
    String message;
    String qq;
    String qqlm;

    public QQlmInfo(String status, String message, String qq, String qqlm) {
        this.status = status;
        this.message = message;
        this.qq = qq;
        this.qqlm = qqlm;
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

    public String getQqlm() {
        return this.qqlm;
    }

    public void setQqlm(String qqlm) {
        this.qqlm = qqlm;
    }
}

