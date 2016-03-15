package com.fish.fishapp.feines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.contact.FeinesContacte_Activity;
import com.fish.fishapp.contact.Pagament_Activity;
import com.fish.fishapp.contact.Xat_Activity;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.parse.ParseUser;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FeinesDetall_Activity extends Activity {

    private String jobId;
    private String jobOffered;
    private int priceHour;

	private Job job;

	TextView textViewNombre;
	TextView textViewEdad;
	TextView textViewLocationName;
	TextView textViewDistancia;
	TextView textViewTags;
	TextView textViewRatings;

    Button botoFavorit;

    RatingBar ratingBar;

    GridView gridviewTarifa;

    ImageView imageViewPicture;

    CalendarPickerView calendari;

    boolean esFavorit = false;

    @Override
    public void onResume(){

        super.onResume();
    }

    @Override
    public void onStart(){

        super.onStart();

        App.getInstance()._jobsActivity = this;
    }

    @Override
    public void onDestroy(){

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

        setContentView(R.layout.feines_detall);

        // Habilitem el botó per retrocedir de pantalla
        getActionBar().setDisplayHomeAsUpEnabled(true);



        // Obtenim el ID de la feina seleccionada
		if (getIntent() != null && getIntent().getExtras() != null){ //retorna el primer Intent quan mor i resuccita es null

			Bundle b = getIntent().getExtras();

            jobId = b.getString("JobId");
            jobOffered = b.getString("jobOffered");
            priceHour = b.getInt("priceHour");

            App.getInstance().log("ID JOB:      " + jobId);
            App.getInstance().log("JOB OFFERED: " + jobOffered);
            App.getInstance().log("PRICE HOUR:  " + priceHour);

            botoFavorit = (Button) findViewById(R.id.btnFavorits);
            esFavorit = Server.esFavorit(jobId);
            actualitzaBotoFavorit();

		} else {

			this.finish();

			return;
		}

        // Obtenim les dades de la feina seleccionada

		ArrayList<Job> resultObject;

        try {

            App.getInstance().log("Fem la consulta a parse per obtenir la feina seleccionada");

            resultObject = Server.queryJobs(new QueryJobsParameters(), jobId);

		} catch (ServerException e) {

            App.getInstance().log("Error al intentar obtenir la feina seleccionada: " + e.getMessage());

            e.printStackTrace();

			App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));

			this.finish();

			return;
		}

        // Comprovem si hem obtingut les dades de la consulta de la feina seleccionada

		if (resultObject.size() == 1){

            job = resultObject.get(0);

		} else {

            App.getInstance().log("Total feines obtingudes: " + resultObject.size());

			App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));

			this.finish();

			return;
		}

		// Carreguem la foto de la persona seleccionada

        imageViewPicture = (ImageView) this.findViewById(R.id.imageViewPicture);

        App.getInstance().imageCache.loadBitmap(job.fotoURL, imageViewPicture, job.foto);

        // Presentem les dades de la persona seleccionada

        textViewNombre = (TextView)this.findViewById(R.id.textViewNombre);
		textViewNombre.setText(job.nombre);

		textViewEdad = (TextView) this.findViewById(R.id.textViewEdad);
		textViewEdad.setText(job.edad);

        textViewLocationName = (TextView) this.findViewById(R.id.textViewLocationName);
		textViewLocationName.setText(job.ciudad);

		textViewDistancia = (TextView)this.findViewById(R.id.textViewDistancia);
		textViewDistancia.setText(job.distancia);

		gridviewTarifa = (GridView)this.findViewById(R.id.gridviewTarifa);

		textViewTags = (TextView)this.findViewById(R.id.textViewTags);
		textViewTags.setText(job.tags);

		ratingBar = (RatingBar)this.findViewById(R.id.ratingBar);

		//ratingBar.setMax(5);
		//ratingBar.setRating(3); // Float.parseFloat("3.0"));

		textViewRatings = (TextView)this.findViewById(R.id.textViewRatings);

		if (job.ratings != null){

			textViewRatings.setText(job.ratings);

		} else {
            ratingBar.setVisibility(View.INVISIBLE);
			textViewRatings.setText(App.getInstance().getStringResource(R.string.no_ratings));
            textViewRatings.setVisibility(View.INVISIBLE);
		}
/*
		// Configuramos y añadimos el calendario

		calPart = new CalendarPart();

        vg = (ViewGroup) this.findViewById(R.id.calendarLinearLayout);
		
		df = new SimpleDateFormat("yyyy-M-dd");

		avui = new Date();

        App.getInstance().inEditionAvailabylity = job.availabilityCalendar;

        calPart.paintCalendar(vg, this, df.format(avui), true, false);
*/
        // Inicialitzem el calendari

        inicialitzarCalendari();
    }

    public void inicialitzarCalendari(){

        Calendar dataFinal = Calendar.getInstance();
        Calendar dataInicial = Calendar.getInstance();

        // Calculem 3 mesos, a partir de la data d'avui, com data màxima del calendari

        dataFinal.add(Calendar.MONTH, 3);

        // Obtenim la data d'avui

        dataInicial.get(Calendar.DATE);

        // Inicialitzem el calendari amb múltiple selecció

        calendari = (CalendarPickerView) findViewById(R.id.calendar_view);

        calendari.init(dataInicial.getTime(), dataFinal.getTime())
                .withSelectedDate(dataInicial.getTime()).displayOnly();
                //.inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        // Marquem les dates disponibles del treballador

        calendari.highlightDates(diesSeleccionats());
        //calendari.setEnabled(false);
    }

    private ArrayList<Date> diesSeleccionats(){

        App.getInstance().log("Actualitzant el calendari amb les dates disponibles del treballador '" + job.nombre + "'...");

        ArrayList<Date> diesSeleccionats = new ArrayList<>();

        // Obtenim la data d'avui

        Date dataActual = new Date();

        // Comprovem si la data a afegir al calendari és igual o superior al moment actual

        for (int i = 0; i < job.availabilityCalendar.size(); i++){

            if (job.availabilityCalendar.get(i).compareTo(dataActual) >= 0 ) {

                App.getInstance().log("Data afegida: " + job.availabilityCalendar.get(i));

                diesSeleccionats.add(job.availabilityCalendar.get(i));

            } else {

                App.getInstance().log("Data descartada: " + job.availabilityCalendar.get(i));

            }
        }

        /*

        // Algunes dates d'exemple

        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");

        Date primeraData = null;
        Date segonaData = null;
        Date terceraData = null;
        Date cuartaData = null;

        try {

            primeraData = sdf.parse("30/01/2015");
            segonaData = sdf.parse("01/01/2015");
            terceraData = sdf.parse("13/01/2015");
            cuartaData = sdf.parse("05/01/2015");

            diesSeleccionats.add(primeraData);
            diesSeleccionats.add(segonaData);
            diesSeleccionats.add(terceraData);
            diesSeleccionats.add(cuartaData);

        } catch (ParseException e) {

            e.printStackTrace();

        } catch (Exception e) {

            App.getInstance().log(e.toString());
        }

        */

        return diesSeleccionats;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Acció al prémer el botó per retrocedir de pantalla

            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);

                return true;
        }

        return super.onOptionsItemSelected(item);
	}

	public void clickContact(View view){

		App.getInstance().log("Id de l'usuari identificat: " + App.getInstance().usuari.id);
		App.getInstance().log("Id de la feina (perfil) de l'usuari seleccionat: " + jobId);
        App.getInstance().log("Id de l'usuari(perfil) seleccionat: " + job.workerProfileId);
        App.getInstance().log("Id de l'usuari seleccionat: " + job.workerUser.getObjectId());

        //Si no ha contactado antes, pantalla de aceptación
        //Si ya ha contactado, directo al chat
        //Si eres tu, no hay contacto

        // Iniciem el contacte amb la persona seleccionada

        Intent intent = new Intent(App.getInstance().appContext, Xat_Activity.class);

		//Bundle b = new Bundle();

		//b.putString("worker_id", job.workerProfileId);

		App.getInstance().goingToContactJob = job;

		//intent.putExtras(b);
        App.getInstance().log("----------------------------------------");
        App.getInstance().log(job.workerUser.toString());
        App.getInstance().log(job.workerUser.getObjectId());

        intent.putExtra("worker_id", job.workerUser.getObjectId());
        intent.putExtra("nom_contacte", job.nombre);
        intent.putExtra("usuari_contacte", job.workerUser.getObjectId().toLowerCase());

        startActivity(intent);

        finish();
	}


    public void clickContract(View view){

        App.getInstance().log("Id de l'usuari identificat: " + App.getInstance().usuari.id);
        App.getInstance().log("Id de la feina (perfil) de l'usuari seleccionat: " + jobId);
        App.getInstance().log("Id de l'usuari seleccionat: " + job.workerProfileId);
        App.getInstance().log("Id de l'usuari seleccionat: " + job.workerUser.getObjectId());

        Intent intent = new Intent(App.getInstance().appContext, FormulariContracte_Activity.class);

        App.getInstance().goingToContactJob = job;

        //intent.putExtras(b);

        intent.putExtra("worker_profile_id", job.workerProfileId);
        intent.putExtra("worker_id", job.workerUser.getObjectId());
        intent.putExtra("jobOffered", jobOffered);
        intent.putExtra("priceHour", priceHour);

        startActivity(intent);

        finish();
    }


    public void clickFavorit(View view){
        App.getInstance().log("Click a Favorit");

        // Si és favorit, eliminar de favorits
        if (esFavorit) {
            esFavorit = false;
            Server.addFavorite(jobId, false);

        // Si no és favorit, afegir a favorits
        }else {
            esFavorit = true;
            Server.addFavorite(jobId, true);
        }

        actualitzaBotoFavorit();

    }


    public void clickPagament(View view){

        App.getInstance().log("Id de l'usuari identificat: " + App.getInstance().usuari.id);
        App.getInstance().log("Id de la feina (perfil) de l'usuari seleccionat: " + jobId);
        App.getInstance().log("Id de l'usuari seleccionat: " + job.workerProfileId);

        // Iniciem el formulari de pagament
        Intent intent = new Intent(App.getInstance().appContext, Pagament_Activity.class);
        intent.putExtra("id_Emissor", App.getInstance().usuari.id);
        intent.putExtra("id_Receptor", job.workerProfileId);
        startActivity(intent);

        finish();
    }

    private void actualitzaBotoFavorit(){

        if (esFavorit){
            botoFavorit.setText("Quitar favorito");
        } else {
            botoFavorit.setText("Añadir favorito");
        }

    }
}
