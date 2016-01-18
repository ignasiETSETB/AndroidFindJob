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

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

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

    Context context;

    XMPPTCPConnectionConfiguration.Builder configBuilder;

    AbstractXMPPConnection connection;

    ScheduledExecutorService scheduler;
    ScheduledFuture<?> result;

    SharedPreferences prefs;

    ArrayList<Xat_Missatge> arrayMissatges;

    private ChatManager chatManager;
    private ChatMessageListener messageListener;

    private boolean finalitzar = false;

    Button botoEnviar;

    ListView llistaMissatges;

    TextView textEnviar;

    private Xat_Missatge_Adapter_Activity adapter;

    // Variables configuració

    String servidor_Host = "82.223.74.138";
    String servidor_Nom = "localhost";

    String usuariOrigen_Compte;     // Usuari propietari de la conversa
    String usuariOrigen_Nom;        // Nom que es visualitzarà dins la conversa
    String usuariOrigen_Email;      // Email del propietari de la conversa
    String usuariOrigen_Password;   // Password del propietari de la conversa

    String usuariDesti_Compte;      // Usuari per connectar al xat
    String usuariDesti_Nom;         // Nom que es visualitzarà dins la conversa

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

        App.getInstance().log("Desconnectant l'usuari '" + usuariOrigen_Compte + "' del xat...");

        finalitzar = true;

        connection.disconnect();

        // Guardem els missatges del xat

        guardarMissatgesSharedPreferences();

        if (!connection.isConnected()) {

            App.getInstance().log("Usuari '" + usuariOrigen_Compte + "' desconnectat del xat.");
        }

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

        Usuari myuser = App.getInstance().usuari;

        usuariOrigen_Nom = (myuser.profileFirstName.trim() + " " + myuser.profileLastName).trim();
        usuariOrigen_Email = myuser.profileEmail.trim();
        usuariOrigen_Compte = myuser.id.toLowerCase();
        usuariOrigen_Password = myuser.id.toLowerCase();

        //usuariOrigen_Compte = "test";
        //usuariOrigen_Password = "test";

        // Obtenim les dades de l'usuari destí, per iniciar el xat i recuperar la conversa anterior, si existeix

        usuariDesti_Nom = getIntent().getStringExtra("nom_contacte");
        usuariDesti_Compte = getIntent().getStringExtra("usuari_contacte") + "@" + servidor_Nom;

        //usuariDesti_Compte = "test" + "@" + servidor_Nom;
        //usuariDesti_Compte = "fishapp" + "@" + servidor_Nom;

        // Informem dels usuaris a connectar a la conversa

        App.getInstance().log("*");
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*                  Dades dels usuaris que participen a la conversa                 *");
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("* Remitent    - Nom usuari:       " + usuariOrigen_Nom);
        App.getInstance().log("* Remitent    - Email usuari:     " + usuariOrigen_Email);
        App.getInstance().log("* Remitent    - Compte usuari:    " + usuariOrigen_Compte);
        App.getInstance().log("* Remitent    - Paswword usuari:  " + usuariOrigen_Password);
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("* Destinatari - Nom usuari:       " + usuariDesti_Nom);
        App.getInstance().log("* Destinatari - Compte usuari:    " + usuariDesti_Compte);
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*");

        // Configurem la presentació dels elements de la vista

        botoEnviar = (Button)findViewById(R.id.boto_enviar);

        llistaMissatges = (ListView)findViewById(R.id.llista_missatges);

        textEnviar = (TextView)findViewById(R.id.text_enviar);

        botoEnviar.setEnabled(false);

        llistaMissatges.setDivider(null);

        llistaMissatges.setDividerHeight(0);

        // Carreguem els missatges guardats en una matriu

        carregarMissatgesSharedPreferences();

        // Carreguem la matriu amb els missatges, a la llista

        adapter = new Xat_Missatge_Adapter_Activity(this, arrayMissatges);

        llistaMissatges.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        llistaMissatges.setSelection(llistaMissatges.getCount() - 1);

        // Configurem la connexió XMPP

        configBuilder = XMPPTCPConnectionConfiguration.builder();

        configBuilder.setUsernameAndPassword(usuariOrigen_Compte, usuariOrigen_Password);
        configBuilder.setServiceName(servidor_Nom);
        configBuilder.setHost(servidor_Host);
        configBuilder.setDebuggerEnabled(true);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        App.getInstance().log("*");
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*                 Configuració de la connexió amb el servidor XMPP                 *");
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*        Usuari:         " + usuariOrigen_Compte);
        App.getInstance().log("*        Password:       " + usuariOrigen_Password);
        App.getInstance().log("*        Nom del servei: " + servidor_Nom);
        App.getInstance().log("*        Host:           " + servidor_Host);
        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*");

        // Creem la connexió XMPP

        connection = new XMPPTCPConnection(configBuilder.build());

        connection.setPacketReplyTimeout(1000);

        // Iniciem la connexió amb el xat

        connectarXMPP();

        // Comprovem la connexió al servidor cada 5 segons

        scheduler = Executors.newSingleThreadScheduledExecutor();

        /*
        result = scheduler.scheduleAtFixedRate (new Runnable() {

                    public void run() {

                        if (!connection.isConnected() && !finalitzar){

                            App.getInstance().log("Usuari '" + usuariOrigen_Compte + "' no connectat amb el xat. Intentant establir la connexió...");

                            try {

                                connection.connect();

                                connection.login();

                            } catch (SmackException e) {

                                App.getInstance().log("Error: " + e.getMessage());

                            } catch (IOException e) {

                                App.getInstance().log("Error: " + e.getMessage());

                            } catch (XMPPException e) {

                                App.getInstance().log("Error: " + e.getMessage());
                            }
                        } else {

                            App.getInstance().log("Usuari '" + usuariOrigen_Compte + "' connectat al xat.");
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);
                */


        // Enviem el missatge escrit per l'usuari, al premer el botó "Enviar"

        botoEnviar.setOnClickListener(

                new View.OnClickListener() {

                    public void onClick(View v) {

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                if (!connection.isConnected()) {

                                    try {

                                        connection.disconnect();

                                        App.getInstance().log("Connectant amb el servidor '" +  servidor_Host + "'...");

                                        connection.connect();

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

                                        message.setFrom(connection.getUser());

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

                                            auxMss.setRemitent(usuariOrigen_Compte);
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
                                        }

                                    } catch (SmackException.NotConnectedException e) {

                                        connection.disconnect();

                                        //App.getInstance().missatgeEnPantalla("Mensaje no enviado (Sin conexión)");

                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        thread.start();
                    }
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

    public void connectarXMPP (){

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {

                if (!connection.isConnected()){

                    try{

                        // Ens connectem amb el servidor

                        App.getInstance().log("Connectant amb el servidor '" +  servidor_Host + "'...");

                        connection.connect();

                        App.getInstance().log("Connexió amb el servidor '" + servidor_Host + "' establerta.");

                    } catch (SmackException e) {

                        App.getInstance().log("Error al connectar amb el servidor (SmackException): " + e.getMessage());

                    } catch (IOException e) {

                        App.getInstance().log("Error al connectar amb el servidor (IOException): " + e.getMessage());

                    } catch (XMPPException e) {

                        App.getInstance().log("Error al connectar amb el servidor (XMPPException): " + e.getMessage());
                    }
                }

                // Ens loguegem

                if (connection.isConnected()) {

                    if (!loginXMPP()) {

                        // Creem un temporitzador de 3 segons (3000 ms). Al finalitzar, torna a intentar loguejar a l'usuari.
                        // És per donar temps a haver-se creat l'usuari, si no estava donat d'alta previament
                        // Això només passaria en el cas d'usuaris anteriors a la incorporació del xat a l'App

                        App.getInstance().log("Esperem 3 segons, abans de tornar a intentar loguejar a l'usuari");

                        TimerTask comptador = new TimerTask() {

                            @Override
                            public void run() {

                                if(!loginXMPP()) {

                                    App.getInstance().log("Usuari no loguejat, desprès del segon intent.");

                                    return;
                                }
                            }
                        };

                        Timer timer = new Timer();

                        timer.schedule(comptador, 3000);
                    }

                } else {

                    return;
                }

                // Gestionem el Listener dels missatges, pels missatges rebuts

                chatManager = ChatManager.getInstanceFor(connection);

                chatManager.addChatListener(new ChatManagerListener() {

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
                });

                // Activem el botó "Enviar"

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        botoEnviar.setEnabled(true);
                    }
                });
            }
        });

        thread.start();
    }

    /**
     *
     * Ens loguegem
     */
    public boolean loginXMPP (){

        Boolean resultat = false;

        // Ens loguegem

        try {

            App.getInstance().log("Loguejant a l'usuari '" + usuariOrigen_Compte + "'...");

            connection.login();

            resultat = true;

        } catch (SmackException e) {

            App.getInstance().log("Error al identificar a l'usuari (SmackException): " + e.getMessage());

        } catch (IOException e) {

            App.getInstance().log("Error al identificar a l'usuari (IOException): " + e.getMessage());

        } catch (XMPPException e) {

            App.getInstance().log("Error al identificar a l'usuari (XMPPException): " + e.getMessage());
        }

        // Creem el compte de l'usuari

        if (!resultat) {

            resultat = crearUsuariXMPP();
        }

        // Comprovem si ens hem loguejat

        if (resultat) {

            App.getInstance().log("Usuari '" + usuariOrigen_Compte + "' loguejat.");

        }else{

            App.getInstance().log("L'usuari '" + usuariOrigen_Compte + "' no s'ha pogut loguejar.");
        }

        return resultat;
    }

    /**
     * Si arribem aquí, és que l'usuari que inicia la conversa no està donat d'alta. Creem l'usuari.
     */
    public boolean crearUsuariXMPP() {

        Boolean resultat = false;

        AccountManager manager = AccountManager.getInstance(connection);

        Map<String, String> atributs = new HashMap<String, String>();

        atributs.put("name", usuariOrigen_Nom);
        atributs.put("email", usuariOrigen_Email);

        try {

            manager.createAccount(usuariOrigen_Compte, usuariOrigen_Password, atributs);

            App.getInstance().log("*");
            App.getInstance().log("************************************************************************************");
            App.getInstance().log("*    Usuari 'Origen' donat d'alta:");
            App.getInstance().log("************************************************************************************");
            App.getInstance().log("*        Nom:      " + usuariOrigen_Nom);
            App.getInstance().log("*        Email:    " + usuariOrigen_Email);
            App.getInstance().log("*        Compte:   " + usuariOrigen_Compte);
            App.getInstance().log("*        Password: " + usuariOrigen_Password);
            App.getInstance().log("************************************************************************************");
            App.getInstance().log("*");

            resultat = true;

        } catch (SmackException.NoResponseException e) {

            App.getInstance().log("Error al crear un nou compte SmackException.NoResponseException: " + e.getMessage());

        } catch (XMPPException.XMPPErrorException e) {

            App.getInstance().log("Error al crear un nou compte XMPPException.XMPPErrorException: " + e.getMessage());

        } catch (SmackException.NotConnectedException e) {

            App.getInstance().log("Error al crear un nou compte SmackException.NotConnectedException: " + e.getMessage());
        }

        if (!resultat) {

            App.getInstance().log("No s'ha pogut crear el compte '" + usuariOrigen_Compte + "'");
        }

        return resultat;
    }
}
