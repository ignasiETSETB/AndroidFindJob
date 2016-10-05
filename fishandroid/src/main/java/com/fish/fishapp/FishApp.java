package com.fish.fishapp;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.parse.Parse;


public class FishApp extends Application{

    @Override
    public void onCreate() {

        App.getInstance().log("******************************************");
        App.getInstance().log("*              fish iniciat              *");
        App.getInstance().log("******************************************");

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate();

        Parse.initialize(this, "7VGlBngYlKuUPx5wMdAvgJZ0IV2TQxwPwJiAALDO", "X9j4KhnBupZGofdp3xYU5snQHdYvnQeNA5vxxuGN");
        
        // Mètode obsolet
        // PushService.setDefaultPushCallback(this, MainActivity.class);


    }

}
