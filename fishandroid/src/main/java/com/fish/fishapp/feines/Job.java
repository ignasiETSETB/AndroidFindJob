package com.fish.fishapp.feines;

import android.graphics.Bitmap;

import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class Job {
	public String ObjectId;
	public String nombre;
	public String edad;
    public Integer sexe;
	public String fotoURL;
	public Bitmap foto;
	public String moneda;
	public Integer precioHora;
	public String ciudad;
	public String distancia;
	public String tags;
	public String ratings;
	public List<Date> availabilityCalendar;
	public String workerProfileId;
	public ParseUser workerUser;
	public String workerId;
	public String userId;
}
