package com.fish.fishapp.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseUser;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Xat_Llista_Activity extends Activity {

    Context context;

    private ArrayList<Xat_Contacte> arrayContactes = new ArrayList<>();
    private ListView llistaContactes;
    private Xat_Contacte_Adapter adapter;

    PacketCollector collector;
    Thread threadPacketCollector;

    SharedPreferences prefs;



    // Habilitem la reconexió amb el servidor_Host XMPP pel xat

    static {

        try {

            Class.forName("org.jivesoftware.smack.ReconnectionManager");

        } catch (ClassNotFoundException ex) {

            // Problema al reconnectar amb el servidor
        }
    }

    @Override
    protected void onDestroy() {

        App.getInstance().log("Desconnectant l'usuari '" + App.getInstance().usuariOrigen_Compte + "' del xat...");

        App.getInstance().log("*");
        App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.xat_llista);

        this.context = this.getApplicationContext();

        // Habilitem el botó per retrocedir de pantalla

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtenim les dades de contacte del propietari de la conversa

        App.getInstance().usuariOrigen_Compte = ParseUser.getCurrentUser().getObjectId().toLowerCase();
        App.getInstance().usuariOrigen_Password = ParseUser.getCurrentUser().getObjectId();

        if (App.getInstance().connection == null || !App.getInstance().connection.isConnected()){
            App.getInstance().connectarXMPP();
        }

        llistaContactes = (ListView)findViewById(R.id.xat_llista_contactes);


        arrayContactes = Utils.carregarContactesSharedPreferences(context);

        adapter = new Xat_Contacte_Adapter(this, arrayContactes);
        llistaContactes.setAdapter(adapter);

        llistaContactes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Actualitzar estat del contacte
                App.getInstance().log("CLICK EN: " + arrayContactes.get(position).getMissatgePendent());
                arrayContactes.get(position).setMissatgePendent("");
                Utils.guardarContactesSharedPreferences(arrayContactes, context);

                adapter.clear();
                adapter.addAll(arrayContactes);
                // fire the event
                adapter.notifyDataSetChanged();

                // Aturar el listener de missatges, ja que es possarà en marxa el de la Xat_Activity
                destroyPacketCollector();

                // Obrim el xat amb el nom de la persona seleccionada per iniciar el xat
                Intent intent_chat = new Intent(App.getInstance().appContext, Xat_Activity.class);

                // Assignem el nom del contacte i l'usuari, per iniciar el xat i recuperar la conversa anterior, si existeix
                intent_chat.putExtra("nom_contacte", arrayContactes.get(position).getNomContacte());
                intent_chat.putExtra("usuari_contacte", arrayContactes.get(position).getUsuari().split("@")[0]);
                intent_chat.putExtra("worker_id", Server.getObjectIdByXMPPUser(arrayContactes.get(position).getUsuari().split("@")[0]));

                App.getInstance().log("************************************************************************************");
                App.getInstance().log("*    Iniciem una conversa amb:");
                App.getInstance().log("*        Nom:    " + arrayContactes.get(position).getNomContacte());
                App.getInstance().log("*        Usuari: " + arrayContactes.get(position).getUsuari());
                App.getInstance().log("************************************************************************************");

                startActivity(intent_chat);

            }
        });

        createPacketCollector();

    }


    @Override
    protected void onResume() {
        App.getInstance().log("ONRESUME XAT LLISTA ACTIVITY");
        createPacketCollector();
        arrayContactes = Utils.carregarContactesSharedPreferences(context);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(arrayContactes);
                // fire the event
                adapter.notifyDataSetChanged();
            }
        });

        super.onResume();
    }



    /**
     *
     * @param missatge
     * @param usuariDesti
     */
    void actualitzarMissatges(Xat_Missatge missatge, String usuariDesti) {
        App.getInstance().log("actualitzarMissatges");


        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gsonMissatges = new Gson();
        String jsonMissatges = prefs.getString(usuariDesti, "");
        Type typeMissatges = new TypeToken<ArrayList<Xat_Missatge>>() {
        }.getType();

        ArrayList<Xat_Missatge> arrayListMissatges = gsonMissatges.fromJson(jsonMissatges, typeMissatges);

        ArrayList<Xat_Missatge> arrayMissatges;

        if (arrayListMissatges != null) {
            arrayMissatges = arrayListMissatges;
        } else {
            arrayMissatges = new ArrayList<>();
        }

        App.getInstance().log("Missatges a la conversa amb l'usuari de destí '" + usuariDesti + "' = " + arrayMissatges);


        arrayMissatges.add(missatge);


        // Guardar les prefs amb el missatge rebut
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String jsonArrayMissatges = gson.toJson(arrayMissatges);
        editor.putString(usuariDesti, jsonArrayMissatges);
        editor.commit();
    }


    /**
     *
     */
    private void createPacketCollector(){

        threadPacketCollector = new Thread(new Runnable(){

            @Override
            public void run() {

                if (!App.getInstance().connection.isConnected()){
                    App.getInstance().connectarXMPP();
                }else {


                    collector = App.getInstance().connection.createPacketCollector(new AndFilter());
                    while (true) {
                        Packet packet = collector.nextResult();
                        if (packet instanceof Message) {
                            Message message = (Message) packet;
                            if (message != null && message.getBody() != null) {
                                App.getInstance().log("LLISTA - MISSATGE REBUT " + packet.getFrom() + " : " + (message != null ? message.getBody() : "NULL"));


                                // Guardar missatge a Prefs

                                String usuariDesti = packet.getFrom().split("/")[0];
                                String nomDesti = ((Message) packet).getSubject();
                                String objectId = (Server.getObjectIdByXMPPUser(usuariDesti));

                                Xat_Contacte contacte = new Xat_Contacte();
                                contacte.setUsuari(usuariDesti);
                                contacte.setNomContacte(nomDesti);
                                contacte.setObjectId(objectId);
                                contacte.setMissatgePendent("1");

                                arrayContactes = Utils.afegirContacte(arrayContactes, contacte, context);


                                Xat_Missatge missatgeRebut = new Xat_Missatge();
                                missatgeRebut.setBody(((Message) packet).getBody());
                                missatgeRebut.setRemitent(((Message) packet).getSubject());

                                actualitzarMissatges(missatgeRebut, usuariDesti);


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.clear();
                                        adapter.addAll(arrayContactes);
                                        // fire the event
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }
                        }
                    }

                }

            }
        });

        threadPacketCollector.start();
    }

    private void destroyPacketCollector(){
        App.getInstance().log("DESTROY PACKET COLLECTOR (LLISTA CONTACTES)");

        try {
            threadPacketCollector.interrupt();
        } catch (Exception e){
            App.getInstance().log("ERROR Thread: " + e.toString());
        }

        try{
            App.getInstance().connection.removePacketCollector(collector);
        } catch (Exception e) {
            App.getInstance().log("ERROR PacketCollector: " + e.toString());
        }
    }




}
