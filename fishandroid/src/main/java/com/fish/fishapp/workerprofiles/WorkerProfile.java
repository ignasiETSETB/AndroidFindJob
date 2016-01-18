package com.fish.fishapp.workerprofiles;

import android.graphics.Bitmap;
import android.location.Location;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.Usuari;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WorkerProfile {
	
	public String workerProfileObjectId;
	
	public String pictureURL;
	public Bitmap picture;
	public Integer workType;
	public Location location;
	public String locationName;
	public String locationCountry;
	public String distance;
	public ArrayList<String> tags;
	public String currency;
	public Integer priceHour;
	public Integer priceDay;
	public Integer priceWeek;
	public List<Date> availabilityCalendar;
	public Date deletedAt;

	
	
	public void getNewWorkerProfile(){
		
		Usuari usuari = App.getInstance().usuari;
		
		//Alimenta los datos por defecto, procedentes del perfil de usuario
		pictureURL= usuari.profilePictureURL;
		picture = usuari.profilePicture;
		workType = 1;
		locationName = usuari.profileLocationName;
		location= usuari.profileLocation;
		App.getInstance().log("locationcountry is:" + usuari.profileLocationCountry);
		locationCountry= usuari.profileLocationCountry;
		distance="1";
		tags=null;
		currency= usuari.profileCurrency;
		priceHour=0;
		priceDay=0;
		priceWeek=0;
		availabilityCalendar = new ArrayList<Date>();
		
	}
	
	public void save(Boolean pictureChanged) {
		//Save to parse
		try {
			Server.saveWorkerProfile(this, pictureChanged);
		} catch (ServerException e) {
			e.printStackTrace();
			App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));
		}
	}	
}
