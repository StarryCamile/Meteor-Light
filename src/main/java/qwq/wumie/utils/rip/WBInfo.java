/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.rip;

public class WBInfo {
    String status;
    String message;
    String id;
    String phone;
    String phonediqu;

    public WBInfo(String status, String message, String id, String phone, String phonediqu) {
        this.status = status;
        this.message = message;
        this.id = id;
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

    public String getID() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
