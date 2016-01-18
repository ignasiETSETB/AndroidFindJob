package com.fish.fishapp.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.feines.Job;
import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;

import java.util.ArrayList;

public class FeinesContacte_Activity extends Activity {

	String worker_id;

	WebView webViewFirstContact;

	ImageView imageViewPhoto;

	TextView textViewSup;
	TextView textViewInf;

	Job job;

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onDestroy()
    {
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

		setContentView(R.layout.feines_contacte);

        // Habilitem el botó per retrocedir de pantalla

        getActionBar().setDisplayHomeAsUpEnabled(true);

		// Obtenim l'Id del candidat seleccionat

		worker_id = getIntent().getStringExtra("worker_id");

		if(worker_id == null){

			this.finish();

			return;
		}

		/*
		if (getIntent() != null && getIntent().getExtras() != null){

			Bundle b = getIntent().getExtras();

			worker_id = b.getString("worker_id");

		} else {

			this.finish();

			return;
		}
		*/

		job = App.getInstance().goingToContactJob;

        // Carreguem la foto de la persona seleccionada

        imageViewPhoto = (ImageView) this.findViewById(R.id.imageViewPhoto);

        App.getInstance().imageCache.loadBitmap(job.fotoURL, imageViewPhoto,job.foto);

        // Presentem les dades de la persona seleccionada

        textViewSup = (TextView) this.findViewById(R.id.textViewSup);
		textViewSup.setText(job.nombre);

		textViewInf = (TextView) this.findViewById(R.id.textViewInf);
		textViewInf.setText(job.tags);

		// Preparem el control Web per presentar les dades del servidor. Codificació UTF-8 pels accents

        webViewFirstContact = (WebView) this.findViewById(R.id.webViewFirstContact);

		WebSettings webViewSettings = webViewFirstContact.getSettings();

		webViewSettings.setDefaultTextEncodingName("utf-8");

        // Obtenim el text informatiu abans d'establir el contacte

        Server.getLocalizedString("JOBS_CONTACT_ADVICE", new MyCallback() {

			@Override
			public void done(String object, Exception e) {

                App.getInstance().log("Hem descarregat el text informatiu, del servidor, abans de contactar amb la persona seleccionada.");

                // El contigut del "object" és la cadena HTML que conté el text del EULA (<html>...</html>) Codificat UTF-8, pels accents

				webViewFirstContact.loadData(object, "text/html; charset=utf-8", "utf-8");
			}

			@Override
			public void done(ArrayList object, Exception e) {}
		});
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

	public void clickAccept(View view){

		// Obrim el xat amb el nom de la persona seleccionada per iniciar el xat

		Intent intent_chat = new Intent(App.getInstance().appContext, Xat_Activity.class);

		// Assignem el nom del contacte i l'usuari, per iniciar el xat i recuperar la conversa anterior, si existeix

		intent_chat.putExtra("nom_contacte", job.nombre);
		intent_chat.putExtra("usuari_contacte", worker_id.toLowerCase());

		App.getInstance().log("************************************************************************************");
		App.getInstance().log("*    Iniciem una conversa amb:");
		App.getInstance().log("*");
		App.getInstance().log("*        Nom:    " + job.nombre);
		App.getInstance().log("*        Usuari: " + worker_id.toLowerCase());
		App.getInstance().log("************************************************************************************");

		startActivity(intent_chat);

		// Tanquem aquesta finestra

		finish();
	}

	/*
	public void clickAccept(View view){

		//Create or get Chat

		Chat chat = Server.JobsWorkerProfile_contact(worker_id);
		
		if (chat != null){

			Intent intent = new Intent(App.getInstance().appContext, ChatActivity.class);

			//Paso el chat_id

			Bundle b = new Bundle();

			b.putString("chat_id", chat.id); //Your id

			intent.putExtras(b); //Put your id to your next Intent

	        startActivity(intent);

	        finish();

		} else {

			finish();
		}
	}
	*/
}
