package com.fish.fishapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.fish.fishapp.feines.InfoContracteActivity;
import com.parse.ParseInstallation;
import com.parse.PushService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Presentacio_Activity extends Activity {

    // Duració del retard de la pantalla de presentació, en milisegons

    private static final long RETARDO_PANTALLA = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Amaguem la barra superior per ocupar tota la pantalla

        getActionBar().hide();

        setContentView(R.layout.presentacio);

        // Fem servir una font personalitzada i l'apliquem als texts de la pantalla

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/courbd.ttf");

        TextView fish = (TextView) this.findViewById(R.id.textViewWelcomeFish);

        fish.setTypeface(type);

        TextView behappy = (TextView) this.findViewById(R.id.textViewBeHappy);

        behappy.setTypeface(type);

        //TextView welcome = (TextView) this.findViewById(R.id.textViewWelcome);

        //welcome.setTypeface(type);

        // Creem un temporitzador de 4 segons. Al finalitzar, inicia l'activitat principal de fish

        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                // Iniciem l'activitat principal de fish

                Intent intent = new Intent(Presentacio_Activity.this, Main_Activity.class);

                startActivity(intent);

                finish();
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, RETARDO_PANTALLA);

    }

}
