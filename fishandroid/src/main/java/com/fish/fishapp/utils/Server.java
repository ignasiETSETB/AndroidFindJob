package com.fish.fishapp.utils;

import android.graphics.Bitmap;
import android.location.Location;

import com.fish.fishapp.App;
import com.fish.fishapp.Usuari;
import com.fish.fishapp.contact.Chat;
import com.fish.fishapp.contact.ChatMessage;
import com.fish.fishapp.feines.FormulariEmpresa;
import com.fish.fishapp.feines.FormulariTreballador;
import com.fish.fishapp.feines.Job;
import com.fish.fishapp.feines.QueryJobsParameters;
import com.fish.fishapp.notificacions.Notificacio;
import com.fish.fishapp.workerprofiles.WorkerProfile;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Server {

	//encapsula las llamadas a parse
	//no todas, las que están vinculadas a la interfaz no, ya que supondria una considerable sobrecarga de trabajo innecesaria

	static boolean done = false;
    static ParseObject auxFormulariEmpresa;

    public static void EULA_Aceptado(){

        ParseUser.getCurrentUser().put("eulaAccepted", true);

        ParseUser.getCurrentUser().saveInBackground();

        App.getInstance().log("Actualitzem el perfil amb l'EULA acceptat");
    }

    public static void EULA_No_Aceptado() {

        ParseUser.getCurrentUser().put("eulaAccepted", false);

        ParseUser.getCurrentUser().saveInBackground();

        App.getInstance().log("Actualitzem el perfil amb l'EULA no acceptat");
    }

    public static void setUserData(ParseUser user){

        // Ens guardem les dades de l'usuari identificat

        Usuari myuser = App.getInstance().usuari;

        App.getInstance().log("Actualitzem el perfil amb les dades de l'usuari identificat");

        myuser.id = user.getObjectId();                                     // Id
        myuser.profileEmail = user.getString("profileEmail");               // Email
        myuser.profileFirstName = user.getString("profileFirstName");       // Nom
        myuser.profileGender = user.getInt("profileGender");                // Sexe
        myuser.profileLastName = user.getString("profileLastName");         // Cognoms

        ParseFile fileObject = (ParseFile) user.get("profilePicture");

        // Foto del perfil
        try {
            myuser.profilePictureURL = fileObject.getUrl();
        } catch (Exception e){

        }

        myuser.profileBirthDay = user.getDate("profileBirthDay");           // Data del aniversari
        myuser.eulaAccepted = user.getBoolean("eulaAccepted");              // EULA: '1', '0' o nulo
        myuser.profilePhoneNumber = user.getString("profilePhoneNumber");   // Telèfon
        myuser.profileCurrency = user.getString("profileCurrency");         // Moneda

        ParseGeoPoint point = (ParseGeoPoint) user.get("profileLocation");

        // Obtenim les dades de la geolocalització

        if (point != null){

            myuser.profileLocation = new Location("dummyprovider");

            myuser.profileLocation.setLatitude(point.getLatitude());
            myuser.profileLocation.setLongitude(point.getLongitude());

        } else {

            myuser.profileLocation = null;
        }

        myuser.profileLocationName = user.getString("profileLocationName");
        myuser.profileLocationCountry = user.getString("profileLocationCountry");

        // Presentem totes les dades guardades

        App.getInstance().log("************************************************************************************");
        App.getInstance().log("*              Detall de les dades guardades de l'usuari identificat               *");
        App.getInstance().log("************************************************************************************");

        App.getInstance().log("----------------- Id:                " + myuser.id);
        App.getInstance().log("----------------- Email:             " + myuser.profileEmail);
        App.getInstance().log("----------------- Nom:               " + myuser.profileFirstName);
        App.getInstance().log("----------------- Cognoms:           " + myuser.profileLastName);
        App.getInstance().log("----------------- Sexe:              " + Utils.ObtenirSexeNom(myuser.profileGender) + " (" + myuser.profileGender + ")");
        App.getInstance().log("----------------- Foto:              " + myuser.profilePictureURL);
        App.getInstance().log("----------------- Data de naixement: " + myuser.profileBirthDay +  "(MM/DD/YYYY)");
        App.getInstance().log("----------------- EULA acceptat:     " + myuser.eulaAccepted);
        App.getInstance().log("----------------- Telèfon:           " + myuser.profilePhoneNumber);
        App.getInstance().log("----------------- Moneda:            " + myuser.profileCurrency);

        App.getInstance().log("----------------- Localitat:         " + myuser.profileLocationName);
        App.getInstance().log("----------------- País:              " + myuser.profileLocationCountry);

        if (point!=null){

            App.getInstance().log("----------------- Latitud:           " + myuser.profileLocation.getLatitude());
            App.getInstance().log("----------------- Longitud:          " + myuser.profileLocation.getLongitude());

        } else {

            App.getInstance().log("----------------- Localització:      " + "No disponible");
        }

        App.getInstance().log("************************************************************************************");
    }

    public static void saveLocation(Usuari usuari, final MyCallback myCallback) {

        if (usuari.profileLocationName != null && usuari.profileLocation != null && usuari.profileLocationCountry != null){

            ParseUser.getCurrentUser().put("profileLocationName", usuari.profileLocationName);
            ParseUser.getCurrentUser().put("profileLocationCountry", usuari.profileLocationCountry);

            ParseGeoPoint point = new ParseGeoPoint();

            point.setLatitude(usuari.profileLocation.getLatitude());

            point.setLongitude(usuari.profileLocation.getLongitude());

            ParseUser.getCurrentUser().put("profileLocation", point);

            App.getInstance().log("Dades de la geolocalització del usuari guardades en el seu perfil");

        } else {

            // Si Les dades obtingudes de la geolocalització no són vàlides, les esborrem del perfil del usuari

            ParseUser.getCurrentUser().remove("profileLocationName");
            ParseUser.getCurrentUser().remove("profileLocation");
            ParseUser.getCurrentUser().remove("profileLocationCountry");

            App.getInstance().log("Dades de la geolocalització del usuari eliminades");
        }

        // Guardem les dades de la geolocalització del usuari

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {

                App.getInstance().log("Perfil del usuari actualitzat amb les noves dades de la geolocalització");

                myCallback.done("", null);
            }
        });
    }


    public static ArrayList<Job> queryJobs(QueryJobsParameters queryJobsParameters, String objectId) throws ServerException{

        final ArrayList<Job> res = new ArrayList<>();

        List<ParseObject> jobs;

        ParseQuery<ParseObject> query = new ParseQuery<>("JobsWorkerProfileVw");

        query.whereDoesNotExist("deletedAt");

        if (objectId != null){

            App.getInstance().log("Cerquem la feina per Id = " + objectId);

            query.whereEqualTo("objectId", objectId);

        } else {

            App.getInstance().log("Cerquem les feines pels paràmetres donats = " + queryJobsParameters.texto);

            // Obtenim les paraules per la cerca

            ArrayList<String> palabras = new ArrayList<>();

            if (queryJobsParameters.texto != null){

                String[] splitStr = queryJobsParameters.texto.split(" ");

                for (int i = 0; i < splitStr.length; i++){

                    String str = Utils.removeDiacritics(splitStr[i].toLowerCase());

                    if (str.length() > 0){

                        palabras.add(str);
                    }
                }
            }

            // Obtenim els requisits per la cerca, és a dir, les etiquetes o paraules clau

            if (queryJobsParameters.requisitos.size() > 0){

                for (int i = 0; i < queryJobsParameters.requisitos.size(); i++){

                    String[] splitStr = queryJobsParameters.requisitos.get(i).split(" ");

                    palabras.add(Utils.removeDiacritics(splitStr[i].toLowerCase()));
                }
            }

            // Afegim les paraules al criteri de cerca

            if (palabras.size() > 0){

                query.whereContainsAll("tagsSearch", palabras);
            }

            // Acotem la cerca al preu igual o menor que el seleccionat

            if (queryJobsParameters.precioHora > 0){

                query.whereLessThanOrEqualTo("priceHour", queryJobsParameters.precioHora);
            }

            // Comprovem si s'ha especificat el sexe per la cerca

            if (queryJobsParameters.sexo != null){

                // Acotem la cerca per sexe: Home = 1, Dona = 2 i Qualsevol = 3

                if (queryJobsParameters.sexo == 1 || queryJobsParameters.sexo == 2){

                    query.whereEqualTo("gender", queryJobsParameters.sexo);
                }
            }

            // Si tenim la geolocalització de l'usuari, l'afegim al criteri de cerca

            Location myLocation = App.getInstance().usuari.profileLocation;

            if (myLocation != null){

                query.whereGreaterThanOrEqualTo("locationMaxLatitude", myLocation.getLatitude());
                query.whereLessThanOrEqualTo("locationMinLatitude", myLocation.getLatitude());
                query.whereGreaterThanOrEqualTo("locationMaxLongitude", myLocation.getLongitude());
                query.whereLessThanOrEqualTo("locationMinLongitude", myLocation.getLongitude());
            }
        }

        // Llencem la cerca de feines amb els criteris establerts

        jobs = null;
        try {

            jobs = query.find();

        } catch (ParseException e2) {

            App.getInstance().log("Error al executar la cerca: " + e2.getMessage());

            throw new ServerException();
        } catch (Exception e){
            App.getInstance().log("Error: " + e.toString());
        }

        // Si tenim resultats de la cerca, els tractem per separat

        if (jobs != null){

            App.getInstance().log("Llista amb " + jobs.size() + " feines obtingudes de la cerca llençada: " + jobs.toString());

            int j = 0;

            while (j < jobs.size()) {

                ParseObject po = jobs.get(j);

                Job job = new Job();

                job.ObjectId = po.getObjectId();

                ParseObject wp = (ParseObject) po.get("workerProfile");

                job.workerProfileId = wp.getObjectId();

                job.workerUser= (ParseUser) po.get("user");

                job.nombre = po.getString("firstName");

                job.edad = Utils.getEdad(po.getDate("birthday"));

                job.sexe = po.getInt("gender");

                //TODO: get user pointer objectid:    job.userId = po.getString("user");

                ParseFile fileObject = (ParseFile)po.get("pictureThumbnail");

                job.fotoURL = fileObject.getUrl();

	    		/*
	    		try {
	    			byte[] byteArray;
					byteArray = fileObject.getData();
					Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		    		//job.setFotoURL(fileObject.getUrl());
		    		job.foto=bmp;
				} catch (ParseException e) {
					e.printStackTrace();
				}
	    		*/

                job.precioHora = po.getInt("priceHour");

                String tags = null;

                ArrayList<String> listaTags;

                listaTags = (ArrayList<String>) po.get("tags");

                if (listaTags != null){

                    for (String s : listaTags){

                        if(tags == null){

                            tags = s;

                        } else {

                            tags = tags + " · " + s;
                        }
                    }
                }

                job.tags = tags;

                ParseGeoPoint point =  (ParseGeoPoint) po.get("location");

                if (point != null){

                    Location loc = new Location("dummyprovider");

                    loc.setLatitude(point.getLatitude());

                    loc.setLongitude(point.getLongitude());

                    job.distancia = Utils.distancia(loc, App.getInstance().usuari.profileLocation);

                } else {

                    job.distancia = "?";
                }


                job.ciudad = po.getString("locationName");

                job.moneda = Utils.getCurrencySymbolString(po.getString("currency"));

                job.availabilityCalendar = (List<Date>) po.get("workDays");

                res.add(job);

                // Presentem la feina obtinguda

                /*
                App.getInstance().log("************************************************************************************");
                App.getInstance().log("*                               Detall de la feina " + (j + 1) + "                               *");
                App.getInstance().log("************************************************************************************");

                App.getInstance().log("----------------- Id Feina:          " + job.ObjectId);
                App.getInstance().log("----------------- Id Perfil Usuari:  " + job.workerProfileId);
                App.getInstance().log("----------------- Id Usuari:         " + job.workerUser.getObjectId());
                App.getInstance().log("----------------- Nom:               " + job.nombre);
                App.getInstance().log("----------------- Edat:              " + job.edad);
                App.getInstance().log("----------------- Sexe:              " + Utils.ObtenirSexeNom(job.sexe) + " (" + job.sexe + ")");
                App.getInstance().log("----------------- Foto:              " + job.fotoURL);
                App.getInstance().log("----------------- Preu/hora:         " + job.precioHora);
                App.getInstance().log("----------------- Moneda:            " + job.moneda);
                App.getInstance().log("----------------- Etiquetes:         " + listaTags.toString());
                App.getInstance().log("----------------- Distància:         " + job.distancia);

                App.getInstance().log("----------------- Localitat:         " + job.ciudad);
                App.getInstance().log("----------------- Disponibilitat:    " + job.availabilityCalendar.toString());

                if (point != null){

                    App.getInstance().log("----------------- Latitud:           " + point.getLatitude());
                    App.getInstance().log("----------------- Longitud:          " + point.getLongitude());

                } else {

                    App.getInstance().log("----------------- Localització:      " + "No disponible");
                }
*/
                j++;
            }
        }

        App.getInstance().log("************************************************************************************");

        // Tornem la matriu amb totes les feines obtingudes

        return res;
    }





    public static ArrayList<Job> queryJobsFavorits(QueryJobsParameters queryJobsParameters, String objectId) throws ServerException{

        App.getInstance().log("---QUERY Favorits");

        final ArrayList<Job> res = new ArrayList<>();
        ArrayList<String> llistaFavoritsArray = new ArrayList<>();
        List<ParseObject> jobs;
        JSONArray llistaFavorits = new JSONArray();
        boolean teFavorits = false;

        // Comprovar si l'usuari ja té una llista de favorits
        ParseQuery<ParseObject> queryFav = ParseQuery.getQuery("JobsFavorites");
        queryFav.whereEqualTo("user", ParseUser.getCurrentUser());

        try {
            ParseObject favorits = new ParseObject("JobsFavorites");



            // Té llista de favorits
            if (queryFav.find().size() > 0) {
                teFavorits = true;
                favorits = queryFav.find().get(0);
                App.getInstance().log("---Favorits: " + favorits.getJSONArray("profiles").toString());
                llistaFavorits = favorits.getJSONArray("profiles");

                for (int i = 0; i < llistaFavorits.length(); i++){

                    try {
                        llistaFavoritsArray.add(llistaFavorits.getString(i));
                    } catch (Exception e){

                    }

                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            App.getInstance().log("ERROR: " + e.toString());
        }



        if (teFavorits) {

            App.getInstance().log("---HI HA Favorits");

            ParseQuery<ParseObject> query = new ParseQuery<>("JobsWorkerProfileVw");

            //query.whereDoesNotExist("deletedAt");
            query.whereContainedIn("objectId", llistaFavoritsArray);

            if (objectId != null) {

                App.getInstance().log("Cerquem la feina per Id = " + objectId);
                query.whereEqualTo("objectId", objectId);

            } else {

                App.getInstance().log("Cerquem les feines pels paràmetres donats = " + queryJobsParameters.texto);

                // Obtenim les paraules per la cerca
                ArrayList<String> palabras = new ArrayList<>();

                if (queryJobsParameters.texto != null) {

                    String[] splitStr = queryJobsParameters.texto.split(" ");

                    for (int i = 0; i < splitStr.length; i++) {

                        String str = Utils.removeDiacritics(splitStr[i].toLowerCase());

                        if (str.length() > 0) {

                            palabras.add(str);
                        }
                    }
                }

                // Obtenim els requisits per la cerca, és a dir, les etiquetes o paraules clau

                if (queryJobsParameters.requisitos.size() > 0) {

                    for (int i = 0; i < queryJobsParameters.requisitos.size(); i++) {

                        String[] splitStr = queryJobsParameters.requisitos.get(i).split(" ");

                        palabras.add(Utils.removeDiacritics(splitStr[i].toLowerCase()));
                    }
                }

                // Afegim les paraules al criteri de cerca

                if (palabras.size() > 0) {

                    query.whereContainsAll("tagsSearch", palabras);
                }

                // Acotem la cerca al preu igual o menor que el seleccionat

                if (queryJobsParameters.precioHora > 0) {

                    query.whereLessThanOrEqualTo("priceHour", queryJobsParameters.precioHora);
                }

                // Comprovem si s'ha especificat el sexe per la cerca

                if (queryJobsParameters.sexo != null) {

                    // Acotem la cerca per sexe: Home = 1, Dona = 2 i Qualsevol = 3

                    if (queryJobsParameters.sexo == 1 || queryJobsParameters.sexo == 2) {

                        query.whereEqualTo("gender", queryJobsParameters.sexo);
                    }
                }

                // Si tenim la geolocalització de l'usuari, l'afegim al criteri de cerca

                Location myLocation = App.getInstance().usuari.profileLocation;

                if (myLocation != null) {

                    query.whereGreaterThanOrEqualTo("locationMaxLatitude", myLocation.getLatitude());
                    query.whereLessThanOrEqualTo("locationMinLatitude", myLocation.getLatitude());
                    query.whereGreaterThanOrEqualTo("locationMaxLongitude", myLocation.getLongitude());
                    query.whereLessThanOrEqualTo("locationMinLongitude", myLocation.getLongitude());
                }
            }

            // Llencem la cerca de feines amb els criteris establerts

            try {

                jobs = query.find();

            } catch (ParseException e2) {

                App.getInstance().log("Error al executar la cerca: " + e2.getMessage());

                throw new ServerException();
            }

            // Si tenim resultats de la cerca, els tractem per separat

            if (jobs != null) {

                App.getInstance().log("Llista amb " + jobs.size() + " feines obtingudes de la cerca llençada: " + jobs.toString());

                int j = 0;

                while (j < jobs.size()) {

                    ParseObject po = jobs.get(j);

                    Job job = new Job();

                    job.ObjectId = po.getObjectId();

                    ParseObject wp = (ParseObject) po.get("workerProfile");

                    job.workerProfileId = wp.getObjectId();

                    job.workerUser = (ParseUser) po.get("user");

                    job.nombre = po.getString("firstName");

                    job.edad = Utils.getEdad(po.getDate("birthday"));

                    job.sexe = po.getInt("gender");

                    //TODO: get user pointer objectid:    job.userId = po.getString("user");

                    ParseFile fileObject = (ParseFile) po.get("pictureThumbnail");

                    job.fotoURL = fileObject.getUrl();

                    /*
                    try {
                        byte[] byteArray;
                        byteArray = fileObject.getData();
                        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        //job.setFotoURL(fileObject.getUrl());
                        job.foto=bmp;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    */

                    job.precioHora = po.getInt("priceHour");

                    String tags = null;

                    ArrayList<String> listaTags;

                    listaTags = (ArrayList<String>) po.get("tags");

                    if (listaTags != null) {

                        for (String s : listaTags) {

                            if (tags == null) {

                                tags = s;

                            } else {

                                tags = tags + " · " + s;
                            }
                        }
                    }

                    job.tags = tags;

                    ParseGeoPoint point = (ParseGeoPoint) po.get("location");

                    if (point != null) {

                        Location loc = new Location("dummyprovider");

                        loc.setLatitude(point.getLatitude());

                        loc.setLongitude(point.getLongitude());

                        job.distancia = Utils.distancia(loc, App.getInstance().usuari.profileLocation);

                    } else {

                        job.distancia = "?";
                    }


                    job.ciudad = po.getString("locationName");

                    job.moneda = Utils.getCurrencySymbolString(po.getString("currency"));

                    job.availabilityCalendar = (List<Date>) po.get("workDays");

                    res.add(job);

                    // Presentem la feina obtinguda

                    /*
                    App.getInstance().log("************************************************************************************");
                    App.getInstance().log("*                               Detall de la feina " + (j + 1) + "                               *");
                    App.getInstance().log("************************************************************************************");

                    App.getInstance().log("----------------- Id Feina:          " + job.ObjectId);
                    App.getInstance().log("----------------- Id Perfil Usuari:  " + job.workerProfileId);
                    App.getInstance().log("----------------- Id Usuari:         " + job.workerUser.getObjectId());
                    App.getInstance().log("----------------- Nom:               " + job.nombre);
                    App.getInstance().log("----------------- Edat:              " + job.edad);
                    App.getInstance().log("----------------- Sexe:              " + Utils.ObtenirSexeNom(job.sexe) + " (" + job.sexe + ")");
                    App.getInstance().log("----------------- Foto:              " + job.fotoURL);
                    App.getInstance().log("----------------- Preu/hora:         " + job.precioHora);
                    App.getInstance().log("----------------- Moneda:            " + job.moneda);
                    App.getInstance().log("----------------- Etiquetes:         " + listaTags.toString());
                    App.getInstance().log("----------------- Distància:         " + job.distancia);

                    App.getInstance().log("----------------- Localitat:         " + job.ciudad);
                    App.getInstance().log("----------------- Disponibilitat:    " + job.availabilityCalendar.toString());

                    if (point != null) {

                        App.getInstance().log("----------------- Latitud:           " + point.getLatitude());
                        App.getInstance().log("----------------- Longitud:          " + point.getLongitude());

                    } else {

                        App.getInstance().log("----------------- Localització:      " + "No disponible");
                    }
*/
                    j++;
                }
            }

            App.getInstance().log("************************************************************************************");

            // Tornem la matriu amb totes les feines obtingudes
        }

        return res;
    }





    public static ArrayList<Notificacio> queryNotificacions() throws ServerException{

        final ArrayList<Notificacio> res = new ArrayList<>();

        List<ParseObject> notificacions;

        ParseQuery<ParseObject> query = new ParseQuery<>("JobsHiring");

        query.whereEqualTo("hiredUser", ParseUser.getCurrentUser());


        // Llencem la cerca de feines amb els criteris establerts

        try {

            notificacions = query.find();

        } catch (ParseException e2) {

            App.getInstance().log("Error al executar la cerca: " + e2.getMessage());

            throw new ServerException();
        }

        // Si tenim resultats de la cerca, els tractem per separat

        if (notificacions != null){

            App.getInstance().log("Llista amb " + notificacions.size() + " notificacions obtingudes de la cerca llençada: " + notificacions.toString());

            int j = 0;

            while (j < notificacions.size()) {

                ParseObject po = notificacions.get(j);
                Notificacio notificacio = new Notificacio();
                notificacio.setObjectId(po.getObjectId());
                notificacio.setHiredUSer((ParseUser) po.get("hiredUser"));
                notificacio.setHirerUSer((ParseUser) po.get("hirerUser"));
                notificacio.setHirerData(po.getJSONObject("hirerData"));
                notificacio.setHiredData(po.getJSONObject("hiredData"));
                notificacio.setStatus(po.getInt("contractStatus"));
                try {
                    notificacio.setOfferedJob(notificacio.getHirerData().getJSONObject("contractData").getString("jobOffered"));
                } catch (Exception e){
                    notificacio.setOfferedJob("No especificado");
                }

                try {
                    notificacio.setPriceHour(notificacio.getHirerData().getJSONObject("contractData").getString("priceHour"));
                } catch (Exception e){
                    notificacio.setPriceHour("0");
                }

                try {
                    App.getInstance().log(notificacio.getHirerData().getJSONObject("contractData").getJSONObject("dateStart").getString("iso"));
                    notificacio.setDateStart(notificacio.getHirerData().getJSONObject("contractData").getJSONObject("dateStart").getString("iso").substring(0, 10));
                } catch (Exception e){
                    App.getInstance().log(e.toString());
                    notificacio.setDateStart("No especificada");
                }

                try {
                    notificacio.setDateEnd(notificacio.getHirerData().getJSONObject("contractData").getJSONObject("dateFinish").getString("iso").substring(0, 10));
                    App.getInstance().log(notificacio.getHirerData().getJSONObject("contractData").getJSONObject("dateStart").getString("iso"));

                } catch (Exception e){
                    App.getInstance().log(e.toString());
                    notificacio.setDateEnd("No especificada");
                }


                App.getInstance().log("*  Detall de la notificacio: ");
                App.getInstance().log("*  Empresa       : " + notificacio.getHirerUSer().getObjectId());
                App.getInstance().log("*  Treballador   : " + notificacio.getHiredUSer().getObjectId());
                App.getInstance().log("*  Feina         : " + notificacio.getOfferedJob());

                res.add(notificacio);
                j++;
            }
        }

        App.getInstance().log("************************************************************************************");


        return res;
    }


    /**
     * Afegeix o treu un perfil de la llista de favorits
     * @param profileObjectId
     * @param add
     */
    public static void addFavorite(String profileObjectId, boolean add){

        // Comprovar si l'usuari ja té una llista de favorits
        ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsFavorites");
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        // Si s'ha d'afegir el favorit
        if (add) {
            try {
                ParseObject favorits = new ParseObject("JobsFavorites");

                // Té llista de favorits
                if (query.find().size() > 0) {
                    favorits = query.find().get(0);
                    App.getInstance().log("---Favorits: " + favorits.getJSONArray("profiles").toString());
                    favorits.put("profiles", favorits.getJSONArray("profiles").put(profileObjectId));

                // No té llista de favorits
                } else {
                    favorits.put("user", ParseUser.getCurrentUser());
                    favorits.put("profiles", new JSONArray().put(profileObjectId));
                }

                favorits.save();

            } catch (ParseException e) {
                e.printStackTrace();
                App.getInstance().log("ERROR: " + e.toString());
            }

        // Si s'ha de treure el favorit
        } else {
            try {

                ParseObject favorits = new ParseObject("JobsFavorites");

                if (query.find().size() > 0) {
                    favorits = query.find().get(0);
                    App.getInstance().log("---Favorits: " + favorits.getJSONArray("profiles").toString());
                    JSONArray llistaFavorits = favorits.getJSONArray("profiles");
                    JSONArray novaLlistaFavorits = new JSONArray();

                    for (int i = 0; i < llistaFavorits.length(); i++){
                        String auxProfile = "";
                        try {
                            auxProfile = llistaFavorits.getString(i);
                        } catch (Exception e){

                        }

                        if (!profileObjectId.equals(auxProfile)){
                            novaLlistaFavorits.put(auxProfile);
                        }
                    }

                    favorits.put("profiles", novaLlistaFavorits);
                    favorits.save();

                }
            } catch (ParseException e) {
                e.printStackTrace();
                App.getInstance().log("ERROR: " + e.toString());
            }

        }



    }


    /**
     * Comprovar si el perfil és part dels favorits de l'usuari
     * @param profileObjectId
     */
    public static boolean esFavorit(String profileObjectId) {

        boolean ret = false;
        // Comprovar si l'usuari ja té una llista de favorits
        ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsFavorites");
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        try {
            ParseObject favorits = new ParseObject("JobsFavorites");

            // Té llista de favorits
            if (query.find().size() > 0) {
                favorits = query.find().get(0);
                App.getInstance().log("---Favorits: " + favorits.getJSONArray("profiles").toString());
                JSONArray llistaFavorits = favorits.getJSONArray("profiles");

                for (int i = 0; i < llistaFavorits.length(); i++){
                    String auxProfile = "";
                    try {
                        auxProfile = llistaFavorits.getString(i);
                    } catch (Exception e){

                    }

                    if (profileObjectId.equals(auxProfile)){
                        App.getInstance().log("ES Favorit!");
                        ret = true;
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            App.getInstance().log("ERROR: " + e.toString());
        }

        return ret;

    }



    public static void saveUser(Usuari usuari, Boolean pictureChanged) throws ServerException {

        // Assignem totes les dades actualitzades del perfil de l'usuari

        ParseUser.getCurrentUser().put("profileFirstName", usuari.profileFirstName);
        ParseUser.getCurrentUser().put("profileLastName", usuari.profileLastName);
        ParseUser.getCurrentUser().put("profileGender", usuari.profileGender);
        ParseUser.getCurrentUser().put("profilePhoneNumber", usuari.profilePhoneNumber);
        ParseUser.getCurrentUser().put("profileCurrency", usuari.profileCurrency);
        ParseUser.getCurrentUser().put("profileEmail", usuari.profileEmail);
        ParseUser.getCurrentUser().put("profileLocationName", usuari.profileLocationName);
        ParseUser.getCurrentUser().put("profileLocationCountry", usuari.profileLocationCountry);

        ParseGeoPoint point = new ParseGeoPoint();

        point.setLatitude(usuari.profileLocation.getLatitude());

        point.setLongitude(usuari.profileLocation.getLongitude());

        ParseUser.getCurrentUser().put("profileLocation", point);

        if (pictureChanged){

            Bitmap picture = usuari.profilePicture;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytearray= stream.toByteArray();
            final ParseFile file = new ParseFile("profilePicture.jpg",bytearray);

            try {
                file.save();
            } catch (ParseException e1) {
                e1.printStackTrace();
                throw new ServerException();
            }

            ParseUser.getCurrentUser().put("profilePicture", file);
        }

        // Actualitzem les dades del perfil de l'usuari a Parse

        try {

            ParseUser.getCurrentUser().save();

        } catch (ParseException e) {

            e.printStackTrace();

            throw new ServerException();
        }

        // Per seguretat, tornem a obtenir, de Parse, totes les dades del perfil de l'usauri

        setUserData(ParseUser.getCurrentUser());
    }


    /**
     * Envia un formulari de contracte de la empresa a Parse
     * @param fEmpresa
     * @throws ServerException
     */
    public static void saveFormulariEmpresa(final FormulariEmpresa fEmpresa) throws ServerException {

        ParseObject fe = null;
        JSONObject dateFinish = null;
        JSONObject dateStart = null;

        if (fEmpresa.getId() == null){
            fe = ParseObject.create("JobsHiring");
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsHiring");

            try {
                fe = query.get(fEmpresa.getId());
            } catch (ParseException e) {
                App.getInstance().log("SaveFormulariEmpresa() ERROR(getId): " + e.toString());
                e.printStackTrace();
                throw new ServerException();
            }
        }

        if (fe != null){

            fe.put("contractStatus", prepareForParse(1));
            fe.put("hirerUser", fEmpresa.getHirerUser());

            // Preparar hirerData
            JSONObject hirerData = new JSONObject();

            try {
                    // contractData
                    JSONObject contractData = new JSONObject();

                    dateFinish = new JSONObject();
                    dateFinish.put("__type", prepareForParse("Date"));
                    dateFinish.put("iso", prepareForParse(Utils.stringToJSONDate(fEmpresa.getDataFiContracte())));

                    dateStart = new JSONObject();
                    dateStart.put("__type", prepareForParse("Date"));
                    dateStart.put("iso", prepareForParse(Utils.stringToJSONDate(fEmpresa.getDataIniciContracte())));

                    contractData.put("dateFinish", dateFinish);
                    contractData.put("dateStart", dateStart);
                    contractData.put("jobOffered", prepareForParse(fEmpresa.getJobOffered()));
                    contractData.put("priceHour", prepareForParse(fEmpresa.getPriceHour()));

                hirerData.put("contractData", contractData);

                // fiscalData
                    JSONObject fiscalData = new JSONObject();

                    fiscalData.put("email", prepareForParse(fEmpresa.getEmailFiscal()));

                        // address
                        JSONObject fiscalAddress = new JSONObject();
                        fiscalAddress.put("address", prepareForParse(fEmpresa.getAdrecaFiscal()));
                        fiscalAddress.put("country", "ES");
                        fiscalAddress.put("localityName", prepareForParse(fEmpresa.getLocalitatFiscal()));
                        fiscalAddress.put("postalCode", prepareForParse(fEmpresa.getCpFiscal()));
                        fiscalAddress.put("province", prepareForParse(fEmpresa.getProvinciaFiscal()));
                        fiscalAddress.put("townName", prepareForParse(fEmpresa.getMunicipiFiscal()));
                        fiscalAddress.put("phoneNumber", prepareForParse(fEmpresa.getTelefonFiscal()));
                        fiscalAddress.put("web", prepareForParse(fEmpresa.getWebFiscal()));

                    fiscalData.put("fiscalAddress", fiscalAddress);

                        // identification
                        JSONObject identification = new JSONObject();
                        identification.put("companyName", prepareForParse(fEmpresa.getNomEmpresaIdentificacio()));
                        JSONObject documentIdentificacio = new JSONObject();
                        documentIdentificacio.put("number", prepareForParse(fEmpresa.getNumeroDocumentEmpresaIdentificacio()));
                        documentIdentificacio.put("type", prepareForParse(fEmpresa.getTipusDocumentEmpresaIdentificacio()));
                        identification.put("document", prepareForParse(documentIdentificacio));
                        identification.put("tradeName", prepareForParse(fEmpresa.getNomComercialEmpresaIdentificacio()));

                    fiscalData.put("identification", identification);

                        // signingPerson
                        JSONObject signingPerson = new JSONObject();
                        JSONObject documentSigninPerson = new JSONObject();
                        documentSigninPerson.put("number", prepareForParse(fEmpresa.getNumeroDocumentPersonaSignant()));
                        documentSigninPerson.put("type", prepareForParse(fEmpresa.getTipusDocumentPersonaSignant()));
                        signingPerson.put("document", prepareForParse(documentSigninPerson));
                        signingPerson.put("email", prepareForParse(fEmpresa.getEmailPersonaSignant()));
                        signingPerson.put("name", prepareForParse(fEmpresa.getNomPersonaSignant()));
                        signingPerson.put("phoneNumber", prepareForParse(fEmpresa.getTelefonPersonaSignant()));
                        signingPerson.put("position", prepareForParse(fEmpresa.getCarrecPersonaSignant()));

                    fiscalData.put("signingPerson", signingPerson);

                        JSONObject workingPlace = new JSONObject();
                        workingPlace.put("CNAE", prepareForParse(fEmpresa.getCNAELlocTreball()));
                        workingPlace.put("convenioCliente", prepareForParse(fEmpresa.getConveniClientLlocTreball()));
                        workingPlace.put("cuentaCotizacionSS", prepareForParse(fEmpresa.getComptaCotizacionSSLlocTreball()));

                    fiscalData.put("workingPlace", prepareForParse(workingPlace));


                    hirerData.put("fiscalData", prepareForParse(fiscalData));

            } catch (Exception e) {
                App.getInstance().log("SaveFormulariEmpresa() ERROR(hirerData): " + e.toString());
                e.printStackTrace();
            }

            fe.put("hirerData", hirerData);

            fe.put("hiredUser", fEmpresa.getHiredUser());


            App.getInstance().log(fe.toString());

            //Save
            try {
                fe.save();
                App.getInstance().log("saveFormulariEmpresa OK");


                // Enviar notificacio PUSH al treballador
                Map<String, Object> hm = new HashMap<>();

                ArrayList<String> destinataries = new ArrayList<>();
                destinataries.add(fEmpresa.getHiredUser().getObjectId());

                JSONArray destJS = new JSONArray();
                destJS.put(fEmpresa.getHiredUser().getObjectId());

                App.getInstance().log("DESTINATARIES: " + destinataries.get(0));


                hm.put("message", "Oferta de trabajo");
                hm.put("from", ParseUser.getCurrentUser().getObjectId());
                hm.put("destinataries", destinataries);
                hm.put("expDate", "2016-12-17T00:00:00.000Z");
                hm.put("jobHiring", fe.getObjectId());
                //hm.put("hirerData", fe.get("hirerData"));
                hm.put("dateFinish", fEmpresa.getDataFiContracte().toString());
                hm.put("dateStart", fEmpresa.getDataIniciContracte().toString());
                hm.put("jobOffered", prepareForParse(fEmpresa.getJobOffered()));
                hm.put("priceHour", prepareForParse(fEmpresa.getPriceHour()));


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


            } catch (Exception e) {
                e.printStackTrace();
                App.getInstance().log("saveFormulariEmpresa ERROR: " + e.toString());
                throw new ServerException();
            }
        }
    }



    /**
     * Envia un formulari de contracte de la empresa a Parse
     * @param fTreb
     * @throws ServerException
     */
    public static void saveFormulariTreballador(FormulariTreballador fTreb) throws ServerException {

        App.getInstance().log("saveFormulariTreballador()...");

        ParseObject formulariFeina = null;

        if (fTreb.getIdRegistreFeina() == null){
            // No existeix la feina
            App.getInstance().log("fTreb.getIdRegistreFeina() == null");
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsHiring");
            query.whereEqualTo("objectId", fTreb.getIdRegistreFeina());
            try {
                formulariFeina = query.find().get(0);
                App.getInstance().log("idJobsHiring: " + fTreb.getIdRegistreFeina());
                App.getInstance().log("return: " + formulariFeina.getObjectId());

            } catch (ParseException e) {
                e.printStackTrace();
                App.getInstance().log("" + e.toString());
            }
        }

        if (formulariFeina != null){

            App.getInstance().log("Formulari Feina OK");

            formulariFeina.put("contractStatus", 2);
            //formulariFeina.put("hirerUser", fTreb.getHirerUser());

            //formulariFeina.put("hiredUser", fTreb.getHiredUser());

            // Preparar hiredData
            JSONObject hiredData = new JSONObject();
            App.getInstance().log("FE: 1");
            try {
                App.getInstance().log("FE: 2");
                hiredData.put("IBAN", prepareForParse(fTreb.getIban()));
                hiredData.put("SWIFT", prepareForParse(fTreb.getSwift()));
                //hiredData.put("address", prepareForParse(fTreb.get));

                JSONObject birthDate = new JSONObject();


                birthDate.put("__type", prepareForParse("Date"));
                birthDate.put("iso", prepareForParse(Utils.stringToJSONDate(fTreb.getDataNaixement())));

                hiredData.put("birthDate", birthDate);

                JSONObject compName = new JSONObject();
                compName.put("name", prepareForParse(fTreb.getNom()));
                compName.put("surname1", prepareForParse(fTreb.getCognom1()));
                compName.put("surname2", prepareForParse(fTreb.getCognom2()));
                hiredData.put("compName", compName);

                JSONObject documentTreballador = new JSONObject();
                documentTreballador.put("number", prepareForParse(fTreb.getDocument()));
                documentTreballador.put("type", prepareForParse(fTreb.getTipusIdentificacio()));
                hiredData.put("document", documentTreballador);

                hiredData.put("email", prepareForParse(fTreb.getEmail()));
                hiredData.put("numSS", prepareForParse(fTreb.getNumeroSS()));
                hiredData.put("phoneNumber", prepareForParse(fTreb.getTelefon()));
                hiredData.put("sex", prepareForParse(fTreb.getSexe()));


            } catch (Exception e) {
                App.getInstance().log("ERROR : " + e.toString());
                e.printStackTrace();
            }

            App.getInstance().log("FE: 3");

            formulariFeina.put("hiredData", hiredData);

            App.getInstance().log("FE: " + formulariFeina.toString());

            //Save
            try {
                formulariFeina.save();
            } catch (ParseException e) {
                App.getInstance().log("ERROR : " + e.toString());
                e.printStackTrace();
            }
        } else {
            App.getInstance().log("Formulari Feina es NULL");

        }
    }



    /**
     * Marca el formulari com rebutjat
     * @param objectIdFormulari
     * @throws ServerException
     */
    public static void marcarFormulariRebutjat(String objectIdFormulari) throws ServerException {

        App.getInstance().log("saveFormulariTreballador()...");

        ParseObject formulariFeina = null;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsHiring");
        query.whereEqualTo("objectId", objectIdFormulari);
        try {
            formulariFeina = query.find().get(0);
            App.getInstance().log("idJobsHiring: " + objectIdFormulari);
            App.getInstance().log("return: " + formulariFeina.getObjectId());

        } catch (ParseException e) {
            e.printStackTrace();
            App.getInstance().log("" + e.toString());
        }

        if (formulariFeina != null){

            App.getInstance().log("Formulari Feina OK");
            formulariFeina.put("contractStatus", 4);

            //Save
            try {
                formulariFeina.save();
            } catch (ParseException e) {
                App.getInstance().log("ERROR : " + e.toString());
                e.printStackTrace();
            }
        } else {
            App.getInstance().log("Formulari Feina es NULL");

        }
    }



    public static String[] getJobsSearchWords(String str, final MyCallback myCallback){

		App.getInstance().log("getJobsSearchWords");
		HashMap<String, Object> hm= new HashMap<String, Object>();
		hm.put("searchText", str);
		hm.put("limit", 20);
		ParseCloud.callFunctionInBackground("findJobsSearch", hm, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                App.getInstance().log("Server.findJobsSearch.done");
                ArrayList<?> jobsSearchArray = (ArrayList<?>) object;
                ArrayList<String> arrResult = new ArrayList<String>();
                ParseObject po;

                for (Object aJobsSearchArray : jobsSearchArray) {
                    po = (ParseObject) aJobsSearchArray;

                    App.getInstance().log("string devuelto:" + po.getString("searchedText"));

                    arrResult.add(po.getString("searchedText"));
                }

                myCallback.done(arrResult, e);
            }
        });
		
		
		
		
		return null;
		
	}



	public static void getLocalizedString(String key, final MyCallback myCallback ){
		
		HashMap<String, Object> hm = new HashMap<String, Object>();

		hm.put("key", key);
		hm.put("langCode", "en");

		App.getInstance().log("Consultem en el servidor si està disponible l'EULA");

		ParseCloud.callFunctionInBackground("getLocalizedString", hm, new FunctionCallback<String>() {

            @Override
            public void done(String object, ParseException e) {

                App.getInstance().log("EULA disponible en el servidor");

                myCallback.done(object, e);
            }
        });
	}



	public static void logout() {
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.remove("user");
        parseInstallation.saveInBackground();
		ParseUser.getCurrentUser().logOut();
		
	}



	public static void saveWorkerProfile(WorkerProfile workerProfile, Boolean pictureChanged) throws ServerException {
		ParseObject jwp=null;
		if (workerProfile.workerProfileObjectId==null){
			jwp = ParseObject.create("JobsWorkerProfile");			
		} else {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsWorkerProfile");
			
			try {
				jwp = query.get(workerProfile.workerProfileObjectId);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new ServerException();
			}
		}
		if (jwp!=null){
			if (workerProfile.deletedAt!=null){
				jwp.put("deletedAt", workerProfile.deletedAt);
			} else {
				jwp.remove("deletedAt");
				if (pictureChanged){
					Bitmap picture = workerProfile.picture;
					if (picture!=null){
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						picture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						// get byte array here
						byte[] bytearray= stream.toByteArray();
						final ParseFile file = new ParseFile("picture.jpg",bytearray);
						try {
							file.save();
						} catch (ParseException e1) {
							e1.printStackTrace();
							throw new ServerException();
						}
						jwp.put("picture", prepareForParse(file));
					} else {
						App.getInstance().log("Picture of workerProfile is null!");
						jwp.remove("picture");
						//jwp.put("picture",null);
					}
				}
				jwp.put("workType", prepareForParse(workerProfile.workType));

				//No se pueden modificar:
				ParseGeoPoint point = new ParseGeoPoint();
				point.setLatitude(workerProfile.location.getLatitude());
				point.setLongitude(workerProfile.location.getLongitude()); 
				jwp.put("location", prepareForParse(point));
				jwp.put("locationName",prepareForParse(workerProfile.locationName));
				jwp.put("locationCountry",prepareForParse(workerProfile.locationCountry));

				jwp.put("currency", prepareForParse(workerProfile.currency));
				String str = workerProfile.distance;
				str=str.replace(" Km","");
				jwp.put("workMaxDistance",prepareForParse(Utils.parseInt(str))); //TODO: +50 is string but column is number! so?

				if (workerProfile.tags!=null){
					jwp.put("tags", workerProfile.tags);
				} else {
					jwp.remove("tags");
				}

				jwp.put("priceHour", prepareForParse(workerProfile.priceHour));
				jwp.put("priceDay", prepareForParse(workerProfile.priceDay));
				jwp.put("priceWeek", prepareForParse(workerProfile.priceWeek));

				//TODO: dates workerProfile.workerProfileAvailabilityCalendar = datesList;
				List<Date> list = App.getInstance().inEditionAvailabylity;
				for (int i=0; i!=list.size();i++){
					App.getInstance().log("Trabaja en:" + list.get(i).toString());
				}
				jwp.put("workDays", list);

				jwp.put("user", ParseUser.getCurrentUser());
			}
			//Save
			try {
				jwp.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServerException();
			}
		}
	}


	
	public static WorkerProfile readWorkerProfile(String objectId) throws ServerException{
		
		App.getInstance().log("Reading WorkerProfile with id:" + objectId);
		WorkerProfile wp = new WorkerProfile();
		ParseObject jwp=null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("JobsWorkerProfile");
		query.whereEqualTo("objectId", objectId);
		try {
			jwp = query.get(objectId);
		} catch (ParseException e) {
			//Throws an exception when there is no such object or when the network connection fails.		
			e.printStackTrace();
			throw new ServerException();
		}
		if (jwp==null) return null;
		//Transfer data
		wp.workerProfileObjectId = objectId;
		
    	ParseFile fileObject = (ParseFile) jwp.get("pictureThumbnail");

        // if pictureURL is null, it makes
        if(fileObject != null) {
            wp.pictureURL = fileObject.getUrl();
        } else {
            App.getInstance().log("readWorkerProfile null pictureURL    ");
            wp.pictureURL = "";
        }
    	
		wp.workType=jwp.getInt("workType");
		
		Location loc = new Location("dummyprovider");
		ParseGeoPoint point = (ParseGeoPoint) jwp.get("location");
		loc.setLatitude(point.getLatitude());
		loc.setLongitude(point.getLongitude());
		wp.location=loc;
		wp.locationName=jwp.getString("locationName");
		wp.locationCountry=jwp.getString("locationCountry");
		wp.currency=jwp.getString("currency");
		Integer i = jwp.getInt("workMaxDistance");
		wp.distance=i.toString();
		
		wp.tags = (ArrayList<String>) jwp.get("tags");
		wp.priceHour=jwp.getInt("priceHour");
		wp.priceDay=jwp.getInt("priceDay");
		wp.priceWeek=jwp.getInt("priceWeek");
		
		wp.availabilityCalendar=(List<Date>) jwp.get("workDays");
		App.getInstance().inEditionAvailabylity.clear();
		App.getInstance().inEditionAvailabylity = wp.availabilityCalendar;
		return wp;
	}



	public static Object prepareForParse(Object obj){
		Object res;
		if (obj==null){
			res = JSONObject.NULL;
		} else {
			res= obj;
		}
		return res;
	}



	public static ArrayList<WorkerProfile> queryWorkerProfiles(String string) throws ServerException {
		final ArrayList<WorkerProfile> res=new ArrayList<WorkerProfile>();
		List<ParseObject> workerProfiles = null;

        App.getInstance().log("Server.quertWorkerProfiles");
		
		//ask parse
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("JobsWorkerProfile");
		query.include("user");
		query.whereEqualTo("user", ParseUser.getCurrentUser());
		query.whereDoesNotExist("deletedAt");
		
		try {
			workerProfiles=query.find();
		} catch (ParseException e2) {
			e2.printStackTrace();
			throw new ServerException();
		}
		
	    
    	App.getInstance().log("Lista de WorkerProfiles recuperada:" + workerProfiles.size() );
    	if (workerProfiles!=null){
	    	int j=0;
	    	while (j < workerProfiles.size()) {
	    		ParseObject po = workerProfiles.get(j);
	    		WorkerProfile workerprofile=readWorkerProfile(po.getObjectId());
	    		if (workerprofile!=null) res.add(workerprofile);
	    		j++;
	    	}
    	}
    	
		return res;
	}



	public static Chat JobsWorkerProfile_contact(String worker_id) {
		Chat chat = new Chat();
		
		HashMap<String, Object> params= new HashMap<String, Object>();
		HashMap<String, Object> result;
		
		params.put("worker_id", worker_id);
		
		App.getInstance().log("Server.JobsWorkerProfile_contact. Worker_id:" + worker_id);
		
		try {
			//ParseObject pchat = ParseCloud.callFunction("JobsWorkerProfile_contact", params);
			result = ParseCloud.callFunction("JobsWorkerProfile_contact", params);
			App.getInstance().log(result.toString());
			Object x = result.get("objectId");
			chat.id = x.toString();
			return chat;
		} catch (ParseException e) {

			App.getInstance().log("com.parse.ParseException getCode " + e.getCode());
			e.printStackTrace();
		}
		
		return null;
	}



	public static ArrayList<Chat> Chat_list() {
		App.getInstance().log("getting chat list");
		HashMap<String, Object> params= new HashMap<String, Object>();
		ArrayList<Chat> res = new ArrayList<Chat>();
		try {
			ArrayList<ParseObject> list = ParseCloud.callFunction("Chat_list", params);
			
			for (int i=0; i<list.size(); i++){
				Chat ch = new Chat();
				ch.id=(String) list.get(i).get("id");
				App.getInstance().log("new chat added:" + ch.id);
				res.add(ch);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (res.size()>0){
			return res;
		} 
		
		return null;
	}



	public static void Chat_sendMessage(String chat_id, String content) {
		HashMap<String, Object> params= new HashMap<String, Object>();
		params.put("chat_id", chat_id);
		params.put("content", content);
		
		try {
			ParseCloud.callFunction("Chat_sendMessage", params);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}



	public static ArrayList<ChatMessage> Chat_listMessages(String chat_id) {
		
		HashMap<String, Object> params= new HashMap<String, Object>();
		params.put("chat_id", chat_id);
		
		ArrayList<ChatMessage> resultado  = new ArrayList<ChatMessage>();
		
		try {
			ArrayList<ParseObject> polist = ParseCloud.callFunction("Chat_listMessages", params);
			for (int i=0; i<polist.size(); i++){
				ParseObject po = polist.get(i);
				ChatMessage cm = new ChatMessage();
				cm.content=po.getString("content");
				cm.readed=po.getBoolean("readed");
				ParseObject p = (ParseObject) po.get("sender"); 
				cm.sender = p.getObjectId();
				cm.createdAt = po.getCreatedAt();
				resultado.add(cm);
			}
			return resultado;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


    public static String getObjectIdByXMPPUser(String XMPPUser){

        String ret = "";

        // Comprovar si l'usuari ja té una llista de favorits
        ParseQuery<ParseUser> queryFav = ParseUser.getQuery();
        //queryFav.whereEqualTo("objectId", "sCmqIX6yWe" );
        queryFav.whereMatches("objectId", "(" + XMPPUser + ")", "i");

        App.getInstance().log("----OBJECTID(XMPP) : " + XMPPUser);
        try {
            List<ParseUser> usuaris = queryFav.find();
            // Té llista de favorits
            if (usuaris != null && usuaris.size() > 0) {
                ret = usuaris.get(0).getObjectId();
                App.getInstance().log("----OBJECTID: " + ret);
            } else {
                App.getInstance().log("---NO-OBJECTID ");

            }

        } catch (ParseException e) {
            e.printStackTrace();
            App.getInstance().log("ERROR: " + e.toString());
        }

    return ret;

    }


}
