package com.fish.fishapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.fish.fishapp.utils.Utils;

public class PerfilEditar_Activity extends Activity {

	EditText editTextProfileName;
	EditText editTextProfileSurname;
	Spinner spinnerProfileGender;
	EditText editTextProfilePhoneNumber;
	EditText editTextProfileEmail;
	EditText editTextProfileLocation;
	Spinner spinnerProfileCurrency;
	ImageView imageViewProfilePicture;
	
	ProgressDialog ringProgressDialog;
	
	Boolean pictureChanged = false;
	
	@Override
	public void onResume()
	{
        super.onResume();

        editTextProfileLocation.setText(App.getInstance().usuari.profileLocationName);
	}
	
	@Override
	public void onStart()
	{

        super.onStart();

        App.getInstance()._editProfileActivity = this;
	}
	
	@Override
	public void onDestroy()
	{

		App.getInstance().log("*");
		App.getInstance().log("************** Tanquem la classe '" + this.getClass().getSimpleName() + "' **************");
		App.getInstance().log("*");

        super.onDestroy();

        App.getInstance()._editProfileActivity = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		App.getInstance().log("*");
		App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
		App.getInstance().log("*");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.perfil_editar);

		// Habilitem el botó per retrocedir de pantalla

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Amaguem el teclat

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Configurem la presentació dels elements de la vista

		Usuari usr = App.getInstance().usuari;
		
		imageViewProfilePicture = (ImageView) this.findViewById(R.id.imageViewProfilePicture);

		App.getInstance().imageCache.loadBitmap(usr.profilePictureURL, imageViewProfilePicture,usr.profilePicture);

		editTextProfileName = (EditText) this.findViewById(R.id.editTextProfileName);
		editTextProfileName.setText(usr.profileFirstName);
		
		editTextProfileSurname = (EditText) this.findViewById(R.id.editTextProfileSurname);
		editTextProfileSurname.setText(usr.profileLastName);
		
		spinnerProfileGender = (Spinner) this.findViewById(R.id.spinnerProfileGender);

		String myString2 = Utils.ObtenirSexeNom(usr.profileGender);

		ArrayAdapter myAdap2 = (ArrayAdapter) spinnerProfileGender.getAdapter();

		int spinnerPosition2 = myAdap2.getPosition(myString2);

		spinnerProfileGender.setSelection(spinnerPosition2);
		
		editTextProfilePhoneNumber = (EditText) this.findViewById(R.id.editTextProfilePhoneNumber);
		editTextProfilePhoneNumber.setText(usr.profilePhoneNumber);
		
		editTextProfileEmail = (EditText) this.findViewById(R.id.editTextProfileEmail);
		editTextProfileEmail.setText(usr.profileEmail);
		editTextProfileEmail.setEnabled(false);
		
		spinnerProfileCurrency = (Spinner) this.findViewById(R.id.spinnerProfileCurrency);

		String myString = Utils.getCurrencyDisplayString(usr.profileCurrency);

		ArrayAdapter myAdap = (ArrayAdapter) spinnerProfileCurrency.getAdapter();

		int spinnerPosition = myAdap.getPosition(myString);

		spinnerProfileCurrency.setSelection(spinnerPosition);

		editTextProfileLocation = (EditText) this.findViewById(R.id.editTextProfileLocation);
		editTextProfileLocation.setText(usr.profileLocationName);
		editTextProfileLocation.setEnabled(false);
	}
	
	public void clickImage(View view){

		// Donem l'opció al usuari per canviar la foto del seu perfil

		Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		App.getInstance().log("Canviem la foto del perfil...");

		startActivityForResult(pickPhoto, 1);
	}

	// Quan hem canviat la imatge del perfil

	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		App.getInstance().log("Resultat al canviar la imatge del perfil: " + requestCode);

		switch(requestCode) {

			case 0:

				if(resultCode == RESULT_OK){

					Uri selectedImage = imageReturnedIntent.getData();

					imageViewProfilePicture.setImageURI(selectedImage);

					pictureChanged = true;
				}

				break;

			case 1:

				if(resultCode == RESULT_OK){

					Uri selectedImage = imageReturnedIntent.getData();

					imageViewProfilePicture.setImageURI(selectedImage);

					pictureChanged = true;
				}

				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.mnu_perfil_editar, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Comprovem l'opció seleccionada del menú

		switch (item.getItemId()) {

			case R.id.action_save:

				// Presentem la barra de progrés

				ringProgressDialog = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.uploading), true, false);

				ringProgressDialog.setCancelable(true);

				// Guardem les dades actualitzades del perfil de l'usuari

				new Thread(new Runnable() {

					@Override
					public void run() {

						try {

							Usuari usr = App.getInstance().usuari;

							usr.profileFirstName = editTextProfileName.getText().toString();
							usr.profileLastName = editTextProfileSurname.getText().toString();
							usr.profileGender = Utils.ObtenirSexeValor(spinnerProfileGender.getSelectedItem().toString());
							usr.profilePhoneNumber = editTextProfilePhoneNumber.getText().toString();
							usr.profileCurrency = Utils.getCurrencyIsoString(spinnerProfileCurrency.getSelectedItem().toString());
							usr.profileEmail = editTextProfileEmail.getText().toString();
							usr.profileLocationName = editTextProfileLocation.getText().toString();

							if (pictureChanged) {

								Bitmap bitmap = ((BitmapDrawable) imageViewProfilePicture.getDrawable()).getBitmap();

								usr.profilePicture = bitmap;
							}

							App.getInstance().log("Actualitzem les dades del perfil de l'usuari...");

							// Guardem les dades

							usr.save(pictureChanged);

							pictureChanged = false;

							finish();

						} catch (Exception e) {

						}

						ringProgressDialog.dismiss();
					}

				}).start();

				return true;

			default:

				return super.onOptionsItemSelected(item);
		}
	}

	public void clickUpdateLocation(View view){

		// Actualitzem la geolocalització de l'usuari

		App.getInstance().log("Actualitzem la geoclocalització des del perfil de l'usuari");

		App.getInstance().ActualizandoGeoposicionUsuario = true;

		Intent intent = new Intent(App.getInstance().appContext, Geolocalitzacio_Activity.class);

        startActivity(intent);
	}
}
