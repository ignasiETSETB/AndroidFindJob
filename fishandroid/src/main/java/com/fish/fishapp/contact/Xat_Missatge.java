package com.fish.fishapp.contact;

import java.util.Date;

public class Xat_Missatge {

    private int id;
    private String body;
    private String remitent;
    private Date data;
    private boolean enviat;


    public Xat_Missatge(int id, String body, String remitent, Date data, boolean enviat) {

        this.id = id;
        this.body = body;
        this.remitent = remitent;
        this.data = data;
        this.enviat = enviat;
    }

    public Xat_Missatge() {

        this.id = -1;
        this.body = "";
        this.remitent = "";
        this.data = new Date();
        this.enviat = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRemitent() {
        return remitent;
    }

    public void setRemitent(String remitent) {
        this.remitent = remitent;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isEnviat() {
        return enviat;
    }

    public void setEnviat(boolean enviat) {
        this.enviat = enviat;
    }
}
