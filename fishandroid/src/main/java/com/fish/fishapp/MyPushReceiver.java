package com.fish.fishapp;

import android.content.Context;
import android.content.Intent;

import com.fish.fishapp.feines.FeinesLlistat_Activity;
import com.fish.fishapp.feines.InfoContracteActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MyPushReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            App.getInstance().log("Push rebut");
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            App.getInstance().log("got action " + action + " on channel " + channel + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                App.getInstance().log("..." + key + " => " + json.getString(key));
            }


            if (action == ACTION_PUSH_OPEN) {
                App.getInstance().log("Push obert");
                Intent infoContracte = new Intent(context, Main_Activity.class);
                infoContracte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                infoContracte.putExtra("fromUser", json.getString("fu"));
                infoContracte.putExtra("idJobsHiring", json.getString("idJobsHiring"));
                infoContracte.putExtra("priceHour", json.getString("priceHour"));
                infoContracte.putExtra("dateFinish", json.getString("dateFinish"));
                infoContracte.putExtra("dateStart", json.getString("dateStart"));
                infoContracte.putExtra("jobOffered", json.getString("jobOffered"));


                context.getApplicationContext().startActivity(infoContracte);
            }


        } catch (JSONException e) {
            App.getInstance().log("JSONException: " + e.getMessage());
        }

    }
}

/*
public class MyPushReceiver extends BroadcastReceiver {
	private static final String TAG = "MyCustomReceiver";

	  @Override
	  public void onReceive(Context context, Intent intent) {
	    try {
	    	App.getInstance().log("onReceive MyPushReveicer");
	      String action = intent.getAction();
	      String channel = intent.getExtras().getString("com.parse.Channel");
	      JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

	      App.getInstance().log("got action " + action + " on channel " + channel + " with:");
	      Iterator itr = json.keys();
	      while (itr.hasNext()) {
	        String key = (String) itr.next();
	        App.getInstance().log("..." + key + " => " + json.getString(key));
	      }
	    } catch (JSONException e) {
	    	App.getInstance().log("JSONException: " + e.getMessage());
	    }
	  }
}
*/
