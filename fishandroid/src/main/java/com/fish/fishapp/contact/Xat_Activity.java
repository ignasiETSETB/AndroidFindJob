package com.fish.fishapp.contact;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.Usuari;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Xat_Activity extends Activity {
    public static Boolean fake=true;
    Context context;

    XMPPTCPConnectionConfiguration.Builder configBuilder;


    ScheduledExecutorService scheduler;
    ScheduledFuture<?> result;

    SharedPreferences prefs;

    ArrayList<Xat_Missatge> arrayMissatges;
    ArrayList<Xat_Contacte> arrayContactes;
    //Borrar després presentació


    private ChatManager chatManager;
    private ChatMessageListener messageListener;
    private ChatManagerListener chatManagerListener;

    private String usuariDesti_Nom, usuariDesti_Compte, usuariDesti_ObjectId;
    private int userid;
    private boolean finalitzar = false;

    Button botoEnviar;

    ListView llistaMissatges;

    TextView textEnviar;

    private Xat_Missatge_Adapter_Activity adapter;


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

        finalitzar = true;

        //chatManager.removeChatListener(chatManagerListener);

        // Guardem els missatges del xat

        guardarMissatgesSharedPreferences();

        //if (!connection.isConnected()) {

        //    App.getInstance().log("Usuari '" + usuariOrigen_Compte + "' desconnectat del xat.");
        //}

        // Tanquem el temporitzador
        scheduler.shutdown();

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

        setContentView(R.layout.xat);

        this.context = this.getApplicationContext();

        // Habilitem el botó per retrocedir de pantalla

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtenim les dades de contacte del propietari de la conversa
        App.getInstance().usuariOrigen_Nom= App.getInstance().getUserName();
        /*
        if (App.getInstance().connection == null || !App.getInstance().connection.isConnected()){
            userid=Integer.parseInt(getIntent().getStringExtra("userid"));
            App.getInstance().connectarXMPP(userid);
        }


        App.getInstance().usuariOrigen_Nom = ParseUser.getCurrentUser().getString("profileFirstName").trim() + " " + ParseUser.getCurrentUser().getString("profileLastName").trim();
        try {
            App.getInstance().usuariOrigen_Email = ParseUser.getCurrentUser().getString("profileEmail").trim();
        } catch (Exception e) {
            App.getInstance().usuariOrigen_Email = "No facilitado";
        }
        App.getInstance().usuariOrigen_Compte = ParseUser.getCurrentUser().getObjectId().toLowerCase();
        App.getInstance().usuariOrigen_Password = ParseUser.getCurrentUser().getObjectId();
        */
        // Obtenim les dades de l'usuari destí, per iniciar el xat i recuperar la conversa anterior, si existeix

        //usuariDesti_Nom = getIntent().getStringExtra("nom_contacte");
        usuariDesti_ObjectId = getIntent().getStringExtra("worker_id");
        usuariDesti_Compte = getIntent().getStringExtra("nom_contacte").toLowerCase() + "@" + App.getInstance().servidor_Nom;
        usuariDesti_Nom= getIntent().getStringExtra("nom_contacte");
        //usuariDesti_Compte = "test" + "@" + servidor_Nom;
        //usuariDesti_Compte = "fishapp" + "@" + servidor_Nom;

        // Informem dels usuaris a connectar a la conversa

        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*                  Dades dels usuaris que participen a la conversa                 *");
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("* Remitent    - Nom usuari:       " + App.getInstance().usuariOrigen_Nom);
        App.getInstance().log("* Remitent    - Email usuari:     " + App.getInstance().usuariOrigen_Email);
        App.getInstance().log("* Remitent    - Compte usuari:    " + App.getInstance().usuariOrigen_Compte);
        App.getInstance().log("* Remitent    - Paswword usuari:  " + App.getInstance().usuariOrigen_Password);
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("* Destinatari - Nom usuari:       " + usuariDesti_Nom);
        App.getInstance().log("* Destinatari - Compte usuari:    " + usuariDesti_Compte);
        App.getInstance().log("************************************************************************************");

        // Configurem la presentació dels elements de la vista

        afegirContacte(usuariDesti_Compte, usuariDesti_Nom, usuariDesti_ObjectId);

        botoEnviar = (Button) findViewById(R.id.boto_enviar);
        llistaMissatges = (ListView) findViewById(R.id.llista_missatges);
        textEnviar = (TextView) findViewById(R.id.text_enviar);

        llistaMissatges.setDivider(null);
        llistaMissatges.setDividerHeight(0);

        // Carreguem els missatges guardats en una matriu
        carregarMissatgesSharedPreferences();

        // Carreguem la matriu amb els missatges, a la llista


            arrayMissatges.clear();
            Date date = new Date();
            Date date2 = new Date();
            Xat_Missatge fakeMissatge = new Xat_Missatge(userid, "Hola, podrias empezar este lunes?", App.getInstance().usuariOrigen_Nom, date, true);
            Xat_Missatge fakeMissatge2 = new Xat_Missatge(userid + 2, "Sin problema, a que hora me presento?", usuariDesti_Nom, date2, false);
            arrayMissatges.add(fakeMissatge);
            arrayMissatges.add(fakeMissatge2);
            adapter = new Xat_Missatge_Adapter_Activity(this, arrayMissatges);

            llistaMissatges.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            llistaMissatges.setSelection(llistaMissatges.getCount() - 1);
            fake=false;

        /*
        if (!App.getInstance().connection.isConnected()) {
            //botoEnviar.setEnabled(false);
            App.getInstance().connectarXMPP(userid);
        }
           */
        createChatManager();

        // Comprovem la connexió al servidor cada 5 segons
       scheduler = Executors.newSingleThreadScheduledExecutor();


        // Enviem el missatge escrit per l'usuari, al premer el botó "Enviar"
        botoEnviar.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (textEnviar.getText().length() > 0) {
                   Date date2=new Date();
                   Xat_Missatge fakeMissatge =new Xat_Missatge(userid,textEnviar.getText().toString(),usuariDesti_Nom,date2,true);
                   arrayMissatges.add(fakeMissatge);
                   textEnviar.setText("");
               }
               runOnUiThread(new Runnable() {

                   @Override
                   public void run() {

                       llistaMissatges.setAdapter(adapter);
                       adapter.notifyDataSetChanged();
                       llistaMissatges.setSelection(llistaMissatges.getCount() - 1);
                   }
               });



           }


            /*
                    public void onClick(View v) {

                        if (App.getInstance().connection.isConnected()) {

                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    if (!App.getInstance().connection.isConnected()) {
                                        try {
                                            App.getInstance().connection.disconnect();
                                            App.getInstance().log("Connectant amb el servidor '" + App.getInstance().servidor_Host + "'...");
                                            App.getInstance().connection.connect();

                                        } catch (XMPPException e) {
                                            App.getInstance().log("Error: " + e.getMessage());
                                        } catch (SmackException e) {
                                            App.getInstance().log("Error: " + e.getMessage());
                                        } catch (IOException e) {
                                            App.getInstance().log("Error: " + e.getMessage());
                                        }
                                    }

                                    // Comprovem si s'ha escrit un text, abans d'enviar-lo

                                    if (textEnviar.getText().length() > 0) {

                                        try {

                                            // Creem el missatge i el configurem

                                            Message message = new Message();
                                            message.setFrom(App.getInstance().connection.getUser());
                                            message.setSubject(App.getInstance().usuariOrigen_Nom);
                                            message.setBody(textEnviar.getText().toString());
                                            App.getInstance().log("Missatge enviat a '" + usuariDesti_Nom + "', a les " + new Date() + ": " + textEnviar.getText().toString());
                                            Chat chat = chatManager.createChat(usuariDesti_Compte, messageListener);

                                            // Enviem el missatge
                                            chat.sendMessage(message);
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    textEnviar.setText("");
                                                }
                                            });

                                            if (message.getBody() != null) {
                                                final String missatgeRebut = message.getBody();

                                                // Muntem el missatge per presentar-lo
                                                Xat_Missatge auxMss = new Xat_Missatge();

                                                auxMss.setRemitent(App.getInstance().usuariOrigen_Compte);
                                                auxMss.setBody(missatgeRebut);
                                                auxMss.setData(new Date());
                                                auxMss.setEnviat(true);

                                                // Actualitzem la matriu amb els missatges

                                                arrayMissatges.add(auxMss);

                                                // Actualitzem la llista
                                                runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        llistaMissatges.setAdapter(adapter);
                                                        adapter.notifyDataSetChanged();
                                                        llistaMissatges.setSelection(llistaMissatges.getCount() - 1);
                                                    }
                                                });

                                                // Enviar notificació Push al destinatari
                                                Map<String, Object> hm = new HashMap<>();

                                                ArrayList<String> destinataries = new ArrayList<>();
                                                destinataries.add(usuariDesti_ObjectId);

                                                JSONArray destJS = new JSONArray();
                                                destJS.put(usuariDesti_ObjectId);

                                                App.getInstance().log("DESTINATARIES: " + destinataries.get(0));


                                                hm.put("message", "Nuevo mensaje");
                                                hm.put("from", ParseUser.getCurrentUser().getObjectId());
                                                hm.put("destinataries", destinataries);
                                                hm.put("expDate", "2016-12-17T00:00:00.000Z");
                                                //hm.put("jobHiring", fe.getObjectId());
                                                //hm.put("hirerData", fe.get("hirerData"));
                                                //hm.put("dateFinish", fEmpresa.getDataFiContracte().toString());
                                                //hm.put("dateStart", fEmpresa.getDataIniciContracte().toString());
                                                //hm.put("jobOffered", prepareForParse(fEmpresa.getJobOffered()));
                                                //hm.put("priceHour", prepareForParse(fEmpresa.getPriceHour()));


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


                                            }

                                        } catch (SmackException.NotConnectedException e) {

                                            App.getInstance().connection.disconnect();

                                            //App.getInstance().missatgeEnPantalla("Mensaje no enviado (Sin conexión)");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                            thread.start();
                        } else {
                            App.getInstance().missatgeEnPantalla("No estás conectado prueba en unos segundos");
                        }
                    }
            */
                }
            );

        }



    /**
     * Carrega a arrayMissatges els missatges del xat, guardats a SharedPreferences
     */
    void carregarMissatgesSharedPreferences(){
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Matriu amb els missatges
        Gson gsonMissatges = new Gson();
        String jsonMissatges = this.prefs.getString(usuariDesti_Compte, "");
        Type typeMissatges = new TypeToken<ArrayList<Xat_Missatge>>() {}.getType();

        ArrayList<Xat_Missatge> arrayListMissatges = gsonMissatges.fromJson(jsonMissatges, typeMissatges);

        if (arrayListMissatges != null) {
            arrayMissatges = arrayListMissatges;
        } else {
            arrayMissatges = new ArrayList<>();
        }

        App.getInstance().log("Missatges a la conversa amb l'usuari de destí '" + usuariDesti_Compte + "' = " + arrayMissatges);
    }



    /**
     * Guardem els missatges de arrayMissatges a les SharedPreferences
     */
    void guardarMissatgesSharedPreferences(){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        // Matriu amb els missatges del xat

        Gson gson = new Gson();

        String jsonArrayMissatges = gson.toJson(arrayMissatges);

        editor.putString(usuariDesti_Compte, jsonArrayMissatges);

        editor.commit();
    }


    /**
     * Guardem els missatges de arrayMissatges a les SharedPreferences
     */
    void guardarContactesSharedPreferences(){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String jsonArrayContactes = gson.toJson(arrayContactes);
        editor.putString("contactes", jsonArrayContactes);
        App.getInstance().log("Guardats Contactes de l'usuari = " + jsonArrayContactes);

        editor.commit();
    }

    /**
     * Carrega a arrayContactes els contactes, guardats a SharedPreferences
     */
    void carregarContactesSharedPreferences(){

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Matriu amb els missatges
        Gson gsonContactes = new Gson();
        String jsonContactes = this.prefs.getString("contactes", "");
        Type typeContactes = new TypeToken<ArrayList<Xat_Contacte>>() {}.getType();

        ArrayList<Xat_Contacte> arrayListContactes = gsonContactes.fromJson(jsonContactes, typeContactes);

        if (arrayListContactes != null) {
            arrayContactes = arrayListContactes;
        } else {
            arrayContactes = new ArrayList<>();
        }

        App.getInstance().log("Carregats Contactes de l'usuari = " + arrayContactes);
    }

    void afegirContacte(String usuari, String nom, String objectId){
        carregarContactesSharedPreferences();
        boolean existeix = false;
        Xat_Contacte contacte = new Xat_Contacte();
        contacte.setNomContacte(nom);
        contacte.setUsuari(usuari);
        contacte.setObjectId(objectId);

        for (Xat_Contacte contacteLlista:arrayContactes) {
            if (contacte.getUsuari().equals(contacteLlista.getUsuari())){
                existeix = true;
            }
        }

        if (!existeix){
            arrayContactes.add(contacte);
            guardarContactesSharedPreferences();
        }
    }




    private void createChatManager(){
        /*
        chatManager = ChatManager.getInstanceFor(App.getInstance().connection);
        chatManagerListener =  new ChatManagerListener() {

            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {

                chat.addMessageListener(new ChatMessageListener() {

                    @Override
                    public void processMessage(Chat chat, Message message) {

                        App.getInstance().log("Missatge rebut: " + message.getBody());

                        // Comprovem si el missatge està buit

                        if (message.getBody() != null) {

                            final String missatgeRebut = message.getBody();

                            final String remitent = message.getFrom();

                            Xat_Missatge auxMss = new Xat_Missatge();

                            auxMss.setRemitent(usuariDesti_Nom);
                            auxMss.setBody(missatgeRebut);
                            auxMss.setData(new Date());
                            auxMss.setEnviat(false);

                            arrayMissatges.add(auxMss);

                            // Actualitzem la llista dels missatges del xat

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    llistaMissatges.setAdapter(adapter);

                                    adapter.notifyDataSetChanged();

                                    llistaMissatges.setSelection(llistaMissatges.getCount() - 1);
                                }
                            });
                        }
                    }
                });
            }
        };

        chatManager.addChatListener(chatManagerListener);
        */

    }


}
