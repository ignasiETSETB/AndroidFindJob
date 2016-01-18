package com.fish.fishapp.notificacions;

import com.parse.ParseUser;

import org.json.JSONObject;

/**
 * Created by emilio on 15/01/16.
 */
public class Notificacio {

    String objectId;
    ParseUser hirerUSer;
    ParseUser hiredUSer;
    String dateStart;
    String dateEnd;
    String offeredJob;
    String priceHour;
    JSONObject hirerData;
    JSONObject hiredData;
    int status;

    public ParseUser getHirerUSer() {
        return hirerUSer;
    }

    public void setHirerUSer(ParseUser hirerUSer) {
        this.hirerUSer = hirerUSer;
    }

    public ParseUser getHiredUSer() {
        return hiredUSer;
    }

    public void setHiredUSer(ParseUser hiredUSer) {
        this.hiredUSer = hiredUSer;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getOfferedJob() {
        return offeredJob;
    }

    public void setOfferedJob(String offeredJob) {
        this.offeredJob = offeredJob;
    }

    public String getPriceHour() {
        return priceHour;
    }

    public void setPriceHour(String priceHour) {
        this.priceHour = priceHour;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JSONObject getHirerData() {
        return hirerData;
    }

    public void setHirerData(JSONObject hirerData) {
        this.hirerData = hirerData;
    }

    public JSONObject getHiredData() {
        return hiredData;
    }

    public void setHiredData(JSONObject hiredData) {
        this.hiredData = hiredData;
    }
}
