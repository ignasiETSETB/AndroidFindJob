package com.fish.fishapp.notificacions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.feines.InfoContracteActivity;
import com.fish.fishapp.utils.Server;

import java.util.ArrayList;

public class LlistaNotificacions_Activity extends Activity {

    private ListView notificationsList;
    private static NotificacioAdapter adapter;
    ArrayList <Notificacio> notificacions;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llista_notificacions);
        context = this;

        notificacions = new ArrayList();


        // Lista de tracks
        notificationsList = (ListView) findViewById(R.id.llista_notificacions);

        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App.getInstance().log("Click on Item: " + position);
                if (notificacions.get(position).getStatus() == 1) {
                    // Obrir pantalla de Notificació
                    App.getInstance().log("CLICK en pendent(JOB)        : " + notificacions.get(position).getOfferedJob());
                    App.getInstance().log("CLICK en pendent(OBJECTID)   : " + notificacions.get(position).getObjectId());
                    Intent pushIntent = new Intent(LlistaNotificacions_Activity.this, InfoContracteActivity.class);

                    pushIntent.putExtra("idJobsHiring", notificacions.get(position).getObjectId());
                    pushIntent.putExtra("priceHour", notificacions.get(position).getPriceHour());
                    pushIntent.putExtra("dateFinish", notificacions.get(position).getDateEnd());
                    pushIntent.putExtra("dateStart", notificacions.get(position).getDateStart());
                    pushIntent.putExtra("jobOffered", notificacions.get(position).getOfferedJob());
                    pushIntent.putExtra("fromUser", notificacions.get(position).getHirerUSer().getObjectId());

                    finish();
                    startActivity(pushIntent);


                } else if (notificacions.get(position).getStatus() == 2) {
                    // Obrir info del contracte
                } else if (notificacions.get(position).getStatus() == 4) {
                    App.getInstance().missatgeEnPantalla("Esta oferta está rechazada");
                } else {

                }

            }
        });

        try {
            notificacions = Server.queryNotificacions();
            App.getInstance().log(notificacions.toString());
            adapter = new NotificacioAdapter(this, notificacions);
            notificationsList.setAdapter(adapter);
            //adapter.clear();

        } catch (Exception e){

            App.getInstance().log("Error:" + e.toString());

        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        try {

            notificacions = Server.queryNotificacions();
            App.getInstance().log(notificacions.toString());
            adapter = new NotificacioAdapter(this, notificacions);
            //adapter.clear();
            notificationsList.setAdapter(adapter);

        } catch (Exception e){

            App.getInstance().log("Error:" + e.toString());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            notificacions = Server.queryNotificacions();
            App.getInstance().log(notificacions.toString());
            adapter = new NotificacioAdapter(this, notificacions);
            //adapter.clear();
            notificationsList.setAdapter(adapter);

        } catch (Exception e){

            App.getInstance().log("Error:" + e.toString());

        }
    }


}
