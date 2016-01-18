package com.fish.fishapp;

import android.graphics.Bitmap;
import android.location.Location;

import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;

import java.util.ArrayList;
import java.util.Date;

public class Usuari {

	public String id;
	public String profileEmail;
	public String profileFirstName;
	public String profileLastName;
	public String profilePhoneNumber;
	public String profileCurrency;
	public String profileLocationName;
	public String profileLocationCountry;

    public Location profileLocation;

    public Date profileBirthDay;
    public Boolean eulaAccepted;

	public Bitmap profilePicture;
	public String profilePictureURL;

	public Integer profileGender;

/**
 * Informem que l'usuari ha acceptat l'EULA
 *
*/
	public void EULA_Aceptado(){

        App.getInstance().log("L'usuari ha acceptat l'EULA");

        Server.EULA_Aceptado();

		eulaAccepted = true;
	}

    public void EULA_No_Aceptado() {

        // Informem que l'usuari no ha acceptat l'EULA

        App.getInstance().log("L'usuari no ha acceptat l'EULA");

        Server.EULA_No_Aceptado();

        eulaAccepted = false;
    }

	public void setLocation(Location location, String locationName, String countryCode) {
		
		this.profileLocation = location;
		this.profileLocationName = locationName;
		this.profileLocationCountry = countryCode;

        // Ens guardem les dades de la localització de l'usuari identificat

        Server.saveLocation(this, new MyCallback(){

			@Override
			public void done(String object, Exception e) {}

			@Override
			public void done(ArrayList object, Exception e) {}
		});
	}

    public void save(Boolean pictureChanged) {
        try {
            Server.saveUser(this,pictureChanged);
        } catch (ServerException e) {
            e.printStackTrace();
            App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));
        }
    }

    public void logout() {
        Server.logout();
    }
}
