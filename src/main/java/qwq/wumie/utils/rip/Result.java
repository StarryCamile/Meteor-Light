/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.rip;

public class Result {
    String status;
    String par;
    String idcard;
    String born;
    String sex;
    String att;
    String postno;
    String areano;
    String style_simcall;
    String style_citynm;
    String msg;

    public Result(String status, String idcard, String par, String born, String sex, String att, String postno, String areano, String style_simcall, String style_citynm, String msg) {
        this.status = status;
        this.idcard = idcard;
        this.par = par;
        this.born = born;
        this.sex = sex;
        this.att = att;
        this.postno = postno;
        this.areano = areano;
        this.style_simcall = style_simcall;
        this.style_citynm = style_citynm;
        this.msg = msg;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdcard() {
        return this.idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPar() {
        return this.par;
    }

    public void setPar(String par) {
        this.par = par;
    }

    public String getBorn() {
        return this.born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAtt() {
        return this.att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    public String getPostno() {
        return this.postno;
    }

    public void setPostno(String postno) {
        this.postno = postno;
    }

    public String getAreano() {
        return this.areano;
    }

    public void setAreano(String areano) {
        this.areano = areano;
    }

    public String getStyle_simcall() {
        return this.style_simcall;
    }

    public void setStyle_simcall(String style_simcall) {
        this.style_simcall = style_simcall;
    }

    public String getStyle_citynm() {
        return this.style_citynm;
    }

    public void setStyle_citynm(String style_citynm) {
        this.style_citynm = style_citynm;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
