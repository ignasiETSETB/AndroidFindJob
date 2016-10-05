package com.fish.fishapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fish.fishapp.feines.Job;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.Utils;
import com.fish.fishapp.workerprofiles.WorkerProfile;
import com.parse.ParseUser;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.customobjects.model.QBCustomObject;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class App {

	private static App instance;
	
	public Activity _loginActivity;
	public Activity _acceptEULAActivity;
	public Activity _eULAActivity;
	public Activity _jobsActivity;
	public Activity _getLocationActivity;
	public Activity _editProfileActivity;
	public Activity _workerProfilesActivity;

	public Context appContext; 
	public Server server;
	public static Usuari usuari;
	public static int userID;
	public static String userName;
	public static String customOId;
	public static String ID;
	public static String disponibilidad;
	public static String tags;
	public Utils utils;

	public ImageCache imageCache = new ImageCache();

	public Boolean ActualizandoGeoposicionUsuario = false;
	
	public List<Date> inEditionAvailabylity = new ArrayList<Date>();

	public Job goingToContactJob;

	public AbstractXMPPConnection connection;


	// XMPP
	XMPPTCPConnectionConfiguration.Builder configBuilder;

	public String servidor_Host = "82.223.74.138";
	public String servidor_Nom = "localhost";

	public String usuariOrigen_Compte;     // Usuari propietari de la conversa
	public String usuariOrigen_Nom;        // Nom que es visualitzarà dins la conversa
	public String usuariOrigen_Email;      // Email del propietari de la conversa
	public String usuariOrigen_Password;   // Password del propietari de la conversa



	private App(){

		server = new Server();
		usuari = new Usuari();
		utils = new Utils();
	}
	
	public static App getInstance(){

		if (instance == null){

			instance = new App();
		}

		return instance;
	}

	/**
	 *
	 * <br>
	 * Escribim en el log el missatge donat. Tipus: "debug"
	 *
	 * @param missatge Missatge a escriure a l'arxiu log
	 *
	 */

	public void log(String missatge){

		Log.d("FishApp", missatge);
	}

	/**
	 *
	 * <br>
	 * Presentem en pantalla un missatge informatiu
	 *
	 * @param missatge Missatge a presentar
	 *
	 */

	public void missatgeEnPantalla(String missatge){

		Toast.makeText(appContext, missatge, Toast.LENGTH_SHORT).show();
	}
	
	public String getStringResource(int id){

		return this.appContext.getResources().getString(id);
	}
	
	public String[] getStringArrayResource(int id){

		return this.appContext.getResources().getStringArray(id);
	}



	public void connectarXMPP (Integer opponentId){

		QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
		privateChatManager.createDialog(opponentId, new QBEntityCallback<QBDialog>() {
			@Override
			public void onSuccess(QBDialog dialog, Bundle args) {

			}

			@Override
			public void onError(QBResponseException errors) {

			}
		});
		/*

		// XMPP
		usuariOrigen_Compte = ParseUser.getCurrentUser().getObjectId().toLowerCase();
		usuariOrigen_Password = ParseUser.getCurrentUser().getObjectId();

		// Configurem la connexió XMPP

		configBuilder = XMPPTCPConnectionConfiguration.builder();

		configBuilder.setUsernameAndPassword(usuariOrigen_Compte, usuariOrigen_Password);
		configBuilder.setServiceName(servidor_Nom);
		configBuilder.setHost(servidor_Host);
		configBuilder.setDebuggerEnabled(true);
		configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

		App.getInstance().log("************************************************************************************");
		App.getInstance().log("*                 Configuració de la connexió amb el servidor XMPP                 *");
		App.getInstance().log("************************************************************************************");
		App.getInstance().log("*        Usuari:         " + usuariOrigen_Compte);
		App.getInstance().log("*        Password:       " + usuariOrigen_Password);
		App.getInstance().log("*        Nom del servei: " + servidor_Nom);
		App.getInstance().log("*        Host:           " + servidor_Host);
		App.getInstance().log("************************************************************************************");

		// Creem la connexió XMPP

		connection = new XMPPTCPConnection(configBuilder.build());

		connection.setPacketReplyTimeout(10000);
		connection.setFromMode(XMPPConnection.FromMode.USER);



		final Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {

				if (!connection.isConnected()){
					try{
						// Ens connectem amb el servidor
						log("Connectant amb el servidor '" + servidor_Host + "'...");
						connection.connect();
						log("Connexió amb el servidor '" + servidor_Host + "' establerta.");

					} catch (SmackException e) {
						log("Error al connectar amb el servidor (SmackException): " + e.getMessage());
					} catch (IOException e) {
						log("Error al connectar amb el servidor (IOException): " + e.getMessage());
					} catch (XMPPException e) {
						log("Error al connectar amb el servidor (XMPPException): " + e.getMessage());
					}
				}

				// Ens loguegem

				if (connection.isConnected()) {

					if (!loginXMPP()) {

						// Creem un temporitzador de 3 segons (3000 ms). Al finalitzar, torna a intentar loguejar a l'usuari.
						// És per donar temps a haver-se creat l'usuari, si no estava donat d'alta previament
						// Això només passaria en el cas d'usuaris anteriors a la incorporació del xat a l'App

						log("Esperem 3 segons, abans de tornar a intentar loguejar a l'usuari");

						TimerTask comptador = new TimerTask() {

							@Override
							public void run() {

								if(!loginXMPP()) {
									log("Usuari no loguejat, desprès del segon intent.");
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

			}
		});

		thread.start();
		*/
	}

	/**
	 *
	 * Ens loguegem
	 */
	public boolean loginXMPP (){

		Boolean resultat = false;

		// Ens loguegem
		try {
			log("Loguejant a l'usuari '" + usuariOrigen_Compte + "'...");
			connection.login();
			resultat = true;

		} catch (SmackException e) {
			log("Error al identificar a l'usuari (SmackException): " + e.getMessage());
		} catch (IOException e) {
			log("Error al identificar a l'usuari (IOException): " + e.getMessage());
		} catch (XMPPException e) {
			log("Error al identificar a l'usuari (XMPPException): " + e.getMessage());
		}

		// Creem el compte de l'usuari

		if (!resultat) {
			resultat = crearUsuariXMPP();
		}

		// Comprovem si ens hem loguejat

		if (resultat) {
			log("Usuari '" + usuariOrigen_Compte + "' loguejat.");
		}else{
			log("L'usuari '" + usuariOrigen_Compte + "' no s'ha pogut loguejar.");
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

			manager.sensitiveOperationOverInsecureConnection(true);
			manager.createAccount(usuariOrigen_Compte.toLowerCase(), usuariOrigen_Password, atributs);

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


	public static void setUserID(int id) {
		userID = id;
	}

	public static void setID(String id) {
		ID = id;
	}

	public static void setUserData(QBCustomObject co) {
		usuari.ObjectID = co.getCustomObjectId();
		usuari.userID = co.getUserId();
		usuari.profileFirstName = co.getString("username");
		//usuari.profileGender = co.getInteger("profileGender");
		usuari.profileCurrency = co.getString("profileCurrency");
		//Double co.getLocation
		//usuari.profileLocation = co.getLocation("profileLocation");
		usuari.profilePhoneNumber = co.getString("profilePhoneNumber");
	}



	public static int getUserID(){
		return userID;
	}

	public static String getID() {
		return ID;
	}

	public static Usuari getUsuari() {
		return usuari;
	}

	public static void setUserName (String name){
		userName=name;
	}
	public static String getUserName() {
		//Treure despres presentació
		if(userName==null)
			userName="Andrés Gòmez";
		return userName;
	}

	public static void setCustomId (String CustomId){
		customOId=CustomId;
	}
	public static String getCustomId() {
		return customOId;
	}

	public static void setDisponibilidad (String disponiBilidad){
		disponibilidad=disponiBilidad;
	}
	public static String getDisponibilidad(){
		return disponibilidad;
	}
	public static void setTags (String tag){
		tags=tag;
	}
	public static String getTags(){
		return tags;
	}
}