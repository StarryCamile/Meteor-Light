/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.rip;

public class LOLInfo {
    String status;
    String message;
    String qq;
    String name;
    String daqu;

    public LOLInfo(String status, String message, String qq, String name, String daqu) {
        this.status = status;
        this.message = message;
        this.qq = qq;
        this.name = name;
        this.daqu = daqu;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDaqu() {
        return this.daqu;
    }

    public void setDaqu(String daqu) {
        this.daqu = daqu;
    }
}
