package com.fish.fishapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fish.fishapp.feines.Job;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public Usuari usuari;
	public Utils utils;

	public ImageCache imageCache = new ImageCache();

	public Boolean ActualizandoGeoposicionUsuario = false;
	
	public List<Date> inEditionAvailabylity = new ArrayList<Date>();

	public Job goingToContactJob;

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
}
