package com.fish.fishapp.notificacions;

import android.app.Activity;
import android.os.Bundle;

import com.fish.fishapp.R;
import com.fish.fishapp.utils.Server;

public class LlistaNotificacions_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista_notificacions);

        try {
            Server.queryNotificacions();
        } catch (Exception e){

        }

    }
}
