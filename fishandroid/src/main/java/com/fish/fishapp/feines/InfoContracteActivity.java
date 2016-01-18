package com.fish.fishapp.feines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoContracteActivity extends Activity {

    String dateFinish, dateStart, jobOffered, priceHour, idJobsHiring, fromUser = "";
    TextView feinaView, dataIniciView, dataFiView, preuView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contracte);

        // Vista
        feinaView = (TextView) findViewById(R.id.feinaInfoContracte);
        dataIniciView = (TextView) findViewById(R.id.dataIniciInfoContracte);
        dataFiView = (TextView) findViewById(R.id.dataFiInfoContracte);
        preuView = (TextView) findViewById(R.id.preuInfoContracte);

        App.getInstance().log("INFO_ACTIVITY");
        if (getIntent().getExtras() != null){

            if (getIntent().getStringExtra("idJobsHiring") != null){
                idJobsHiring = getIntent().getStringExtra("idJobsHiring");
            }

            if (getIntent().getStringExtra("priceHour") != null){
                priceHour = getIntent().getStringExtra("priceHour");
            }
            if (getIntent().getStringExtra("dateFinish") != null){
                dateFinish = getIntent().getStringExtra("dateFinish");
            }
            if (getIntent().getStringExtra("dateStart") != null){
                dateStart = getIntent().getStringExtra("dateStart");
            }
            if (getIntent().getStringExtra("jobOffered") != null){
                jobOffered = getIntent().getStringExtra("jobOffered");
            }

            if (getIntent().getStringExtra("fromUser") != null){
                fromUser = getIntent().getStringExtra("fromUser");
            }


            App.getInstance().log(idJobsHiring);
            App.getInstance().log(dateFinish);
            App.getInstance().log(dateStart);
            App.getInstance().log(jobOffered);
            App.getInstance().log(priceHour);

            feinaView.setText(jobOffered);
            dataIniciView.setText(dateStart);
            dataFiView.setText(dateFinish);
            preuView.setText(priceHour);

        } else {

        }
    }


    /**
     * Click en Acceptar
     * @param v
     */
    public void clickAcceptarContracte(View v){

        Intent formTreballador = new Intent(InfoContracteActivity.this, FormulariTreballador_Activity.class);

        formTreballador.putExtra("idJobsHiring", getIntent().getStringExtra("idJobsHiring"));
        formTreballador.putExtra("priceHour", getIntent().getStringExtra("priceHour"));
        formTreballador.putExtra("dateFinish", getIntent().getStringExtra("dateFinish"));
        formTreballador.putExtra("dateStart", getIntent().getStringExtra("dateStart"));
        formTreballador.putExtra("jobOffered", getIntent().getStringExtra("jobOffered"));
        formTreballador.putExtra("fromUser", getIntent().getStringExtra("fromUser"));

        startActivity(formTreballador);
        this.finish();



    }


    /**
     * Click en rebutjar
     * @param v
     */
    public void clickRebutjarContracte(View v){

        // Enviar notificacio PUSH de Rebuig a la Empresa
        Map<String, Object> hm = new HashMap<>();

        ArrayList<String> destinataries = new ArrayList<>();
        destinataries.add(fromUser);

        App.getInstance().log("DESTINATARIES: " + destinataries.get(0));


        hm.put("message", ParseUser.getCurrentUser().get("profileFirstName") + " rechazó la oferta '" + jobOffered + "'" );
        hm.put("from", ParseUser.getCurrentUser().getObjectId());
        hm.put("destinataries", destinataries);


        try {
            ParseCloud.callFunctionInBackground("Notifications_sendPushAndroid", hm, new FunctionCallback<Object>() {

                @Override
                public void done(Object object, ParseException e) {
                    if (e == null) {
                        App.getInstance().log("sol·licitud sendPush OK");
                    } else {
                        App.getInstance().log("sol·licitud sendPush ERROR: " + e.toString());
                    }

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.finish();

    }




}
