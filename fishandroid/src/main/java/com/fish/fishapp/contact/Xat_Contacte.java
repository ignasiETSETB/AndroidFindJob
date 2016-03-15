package com.fish.fishapp.contact;

import org.jivesoftware.smack.roster.packet.RosterPacket;

import java.util.Date;

public class Xat_Contacte {

    private String nomContacte;
    private String missatgePendent;
    private String jid;
    private String usuari;
    private String objectId;
    private RosterPacket.ItemStatus estat;


    public Xat_Contacte() {
        this.nomContacte = "";
        this.usuari = "";
        this.missatgePendent = "";
    }


    public String getNomContacte() {
        return nomContacte;
    }

    public void setNomContacte(String nomContacte) {
        this.nomContacte = nomContacte;
    }

    public String getMissatgePendent() {
        return missatgePendent;
    }

    public void setMissatgePendent(String missatgePendent) {
        this.missatgePendent = missatgePendent;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getUsuari() {
        return usuari;
    }

    public void setUsuari(String usuari) {
        this.usuari = usuari;
    }

    public RosterPacket.ItemStatus getEstat() {
        return estat;
    }

    public void setEstat(RosterPacket.ItemStatus estat) {
        this.estat = estat;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
