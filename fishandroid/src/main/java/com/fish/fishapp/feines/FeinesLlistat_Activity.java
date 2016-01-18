package com.fish.fishapp.feines;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fish.fishapp.App;
import com.fish.fishapp.PerfilEditar_Activity;
import com.fish.fishapp.R;
import com.fish.fishapp.contact.Xat_Activity;
import com.fish.fishapp.notificacions.LlistaNotificacions_Activity;
import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.fish.fishapp.utils.Utils;
import com.fish.fishapp.workerprofiles.WorkerProfilesActivity;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeinesLlistat_Activity extends Activity {

	ArrayList<Job> arrayListJob;

	JobAdapter adapter;

    LinearLayout linearLayoutWorkerProfileTags;

    ArrayList<String> tagList;

    EditText editTextNewTag;

    Activity self;

    private ListView listViewJobs;
    private ProgressDialog pd = null;
    private Object data = null;
    private View linearLayoutSearchSettings;
    private EditText editTextSearch;
    private EditText editTextPriceSearch;
	private Spinner spinnerGenderSearch;
	private LinearLayout linearLayoutSearch;
	private TextView textViewNoHayResultados;
    private ScrollView scrollViewSearch;
	private ArrayAdapter<String> textSearchAdapter;
	private QueryJobsParameters queryJobsParameters = new QueryJobsParameters();
	private ListView mListView;
	private Adapter mAdapter;
	private List<String> mMain = new ArrayList<String>();
	private Context mContext = this;
	private Menu menu;
	  
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

        App.getInstance()._jobsActivity = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        App.getInstance().log("*");
        App.getInstance().log("************** Iniciem la classe '" + this.getClass().getSimpleName() + "' **************");
        App.getInstance().log("*");

        super.onCreate(savedInstanceState);

		setContentView(R.layout.feines_llistat);

        // Amaguem el botó per retrocedir de pantalla

        getActionBar().setDisplayHomeAsUpEnabled(false);

        // Amaguem el teclat

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Configurem la presentació dels elements de la vista

        linearLayoutSearchSettings = this.findViewById(R.id.linearLayoutSearchSettings);

		linearLayoutSearchSettings.setVisibility(View.GONE);
		
		editTextSearch = (EditText) this.findViewById(R.id.editTextSearch);

		editTextPriceSearch  = (EditText) this.findViewById(R.id.editTextPriceSearch);

		spinnerGenderSearch = (Spinner)  this.findViewById(R.id.spinnerGenderSearch);

		spinnerGenderSearch.setSelection(2);
		
		textViewNoHayResultados = (TextView) this.findViewById(R.id.textViewNoHayResultados);

		textViewNoHayResultados.setVisibility(View.GONE);
		
		linearLayoutSearch = (LinearLayout) this.findViewById(R.id.linearLayoutSearch);

		tagList = new ArrayList<String>();
		
		editTextNewTag = (EditText) this.findViewById(R.id.editTextNewTag);

		linearLayoutWorkerProfileTags = (LinearLayout) this.findViewById(R.id.linearLayoutWorkerProfileTags);

        scrollViewSearch = (ScrollView) this.findViewById(R.id.scrollViewSearch);

		updateTags();
		
		editTextSearch.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				Server.getJobsSearchWords(s.toString(), new MyCallback() {

		  			@Override
		  			public void done(ArrayList object, Exception e) {

		  				//Elimino

		  				linearLayoutSearch.removeAllViews();

                        for (Object anObject : object) {

                            //creo un text i li poso listener

                            TextView rowTextView = new TextView(FeinesLlistat_Activity.this);
                            rowTextView.setText(anObject.toString());
                            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            rowTextView.setClickable(true);

                            rowTextView.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    TextView tv = (TextView) v;
                                    editTextSearch.setText(tv.getText());
                                    linearLayoutSearch.removeAllViews();

                                }
                            });

                            linearLayoutSearch.addView(rowTextView);
                        }
		  			}

					@Override
					public void done(String object, Exception e) {

					}
		  		});
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		// Presentem la barra de progrès mentre es carreguen les feines

        this.pd = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);

        // Iniciem un thread pere descarregar la llista de feines

        queryJobsParameters = new QueryJobsParameters();

        new DownloadTask().execute(queryJobsParameters);
	}
	
	private class DownloadTask extends AsyncTask<QueryJobsParameters, Void, Object> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(QueryJobsParameters... args) {

        	ArrayList<Job> resultObject;

            App.getInstance().log("************************************************************************************");
            App.getInstance().log("*                  Detall de les dades per la cerca de feines                      *");
            App.getInstance().log("************************************************************************************");

            App.getInstance().log("----------------- Text:              " + args[0].texto);
            App.getInstance().log("----------------- Preu/hora:         " + args[0].precioHora);
            App.getInstance().log("----------------- Requisits:         " + args[0].requisitos);
            App.getInstance().log("----------------- Sexe:              " + Utils.ObtenirSexeNom(args[0].sexo) + " (" + args[0].sexo + ")");

            App.getInstance().log("************************************************************************************");

            App.getInstance().log("Fem la consulta a parse per obtenir la llista de feines disponibles, segons el filtre donat...");

            try {

                resultObject = Server.queryJobs(args[0], null);

            } catch (ServerException e) {

                App.getInstance().log("Error al intentar obtenir la llista de feines disponibles: " + e.getMessage());

                e.printStackTrace();

                App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));

                return null;
			}

            return resultObject;
        }

        @Override
		protected void onPostExecute(Object result) {

            // Quan acabem de rebre totes les feines resultants, comprovem quàntes hem obtingut

        	if (FeinesLlistat_Activity.this.pd != null) {
        		
        		ArrayList<Job> arr = (ArrayList<Job>) result;
        		
            	FeinesLlistat_Activity.this.arrayListJob = arr;

                App.getInstance().log("Total feines obtingudes: " + arr.size());

                // Si no tenim feines, presentem el títol "No hay resultados", si no, l'amaguem

            	if (arr.size() == 0){

            		textViewNoHayResultados.setVisibility(View.VISIBLE);

            	} else {

            		textViewNoHayResultados.setVisibility(View.GONE);
            	}

                // Carreguem les dades de les feines obtingudes a la vista 'activity_item_job', i omplim la llista

        		adapter = new JobAdapter(FeinesLlistat_Activity.this, R.layout.activity_item_job, arrayListJob.toArray(new Job[arrayListJob.size()]));
        		
        		listViewJobs = (ListView) findViewById(R.id.listViewJobs);

        		listViewJobs.setAdapter(adapter);
        		
        		 // L'usuari ha seleccionat una feina del llistat mostrat

        		listViewJobs.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Obtenim les dades de la feina seleccionada

                        Job  itemJob = (Job) listViewJobs.getItemAtPosition(position);

                        App.getInstance().log("Nom del candidat seleccionat: " + itemJob.nombre);
                        App.getInstance().log("Id del candidat seleccionat: " + itemJob.ObjectId);

                        // Presentem el detall de la feina seleccionada

                        Intent intent = new Intent(FeinesLlistat_Activity.this, FeinesDetall_Activity.class);

                        // Informem al sistema del ID de la feina seleccionada

                        Bundle b = new Bundle();

                        b.putString("JobId", itemJob.ObjectId);
                        b.putString("jobOffered", itemJob.nombre);
                        b.putInt("priceHour", itemJob.precioHora);

                        intent.putExtras(b);

                        startActivity(intent);
                    }
                });

        		FeinesLlistat_Activity.this.pd.dismiss();
            }
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Carreguem el menú en el formulari

		getMenuInflater().inflate(R.menu.mnu_main, menu);
		
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        // Comprovem l'opció seleccionada del menú

        switch (item.getItemId()) {

            case R.id.action_buscar:

                if (linearLayoutSearchSettings.getVisibility() == View.VISIBLE){

                    App.getInstance().log("Amaguem els criteris de cerca i iniciem la cerca");

                    linearLayoutSearchSettings.setVisibility(View.GONE);

                    doSearch();

                } else {

                    App.getInstance().log("Visualitzem els criteris de cerca");

                    linearLayoutSearchSettings.setVisibility(View.VISIBLE);
                }

                return true;

            case R.id.action_notificaciones:

                App.getInstance().log("Opció notificacions seleccionada");

                Intent intentNotificacions = new Intent(App.getInstance().appContext, LlistaNotificacions_Activity.class);
                startActivity(intentNotificacions);

                return true;

            case R.id.action_chat:

                App.getInstance().log("Opció xat seleccionada");

                // Intent intent_chat = new Intent(App.getInstance().appContext, ChatListActivity.class);

                // Obrim el xat amb el nom de la persona seleccionada per iniciar el xat

                Intent intent_chat = new Intent(App.getInstance().appContext, Xat_Activity.class);

                // Assignem l'usuari i el nom del contacte per iniciar el xat i recuperar la conversa anterior, si existeix

                intent_chat.putExtra("usuari_contacte", "test");
                //intent_chat.putExtra("nom_contacte", job.nombre);
                intent_chat.putExtra("nom_contacte", "Remitente");

                startActivity(intent_chat);

                return true;

            case R.id.action_trabajo:

                App.getInstance().log("Opció perfil del treballador seleccionada");

                //Intent intent_trabajo = new Intent(App.getInstance().appContext, FeinesLlistat_Activity.class);

                Intent intent_trabajos = new Intent(App.getInstance().appContext, WorkerProfilesActivity.class);

                startActivity(intent_trabajos);

                return true;

            case R.id.action_favoritos:

                App.getInstance().log("Opció favorits seleccionada");

                return true;

            case R.id.action_perfil:

                App.getInstance().log("Opció perfil de l'usuari seleccionada");

                Intent intent_perfil = new Intent(App.getInstance().appContext, PerfilEditar_Activity.class);

                startActivity(intent_perfil);

                return true;

            case R.id.action_logout:

                App.getInstance().log("Opció sortir seleccionada");

                App.getInstance().usuari.setLocation(null, null, null);
                App.getInstance().usuari.EULA_No_Aceptado();
                App.getInstance().usuari.logout();
                ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                parseInstallation.remove("user");
                parseInstallation.saveInBackground();

                if (ParseUser.getCurrentUser() != null) {
                    ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser());
                }

                finish();

                return true;
            /*
            case R.id.action_refresh:

                // Show the ProgressDialog on this thread

                this.pd = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);

                // Start a new thread that will download all the data

                new DownloadTask().execute(queryJobsParameters);

                return true;
            */
            default:

                return super.onOptionsItemSelected(item);
        }
	}

	public void doSearch() {

        // Amaguem els criteris de cerca

		linearLayoutSearchSettings.setVisibility(View.GONE);
				
        // Amaguem el teclat

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Presentem la barra de progrès

        this.pd = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.downloading), true, false);

        // Assignem els criteris per la cerca de feines

		queryJobsParameters = new QueryJobsParameters();
		
		if (editTextSearch.getText().length() > 0){

			queryJobsParameters.texto = editTextSearch.getText().toString();
		}

		Integer precio_h = 0;

        if (editTextPriceSearch.getText().length() > 0) {

            try {

                precio_h = Integer.parseInt(editTextPriceSearch.getText().toString());

            } catch (Exception e) {

                App.getInstance().log("Error en el preu/hora donat per la cerca: " + e.getMessage());

                e.printStackTrace();
            }
        }

		if (precio_h > 0){

            queryJobsParameters.precioHora = precio_h;
        }

		if (tagList.size() > 0){

            queryJobsParameters.requisitos = tagList;
        }

		queryJobsParameters.sexo = Utils.ObtenirSexeValor(spinnerGenderSearch.getSelectedItem().toString());

        // Llencem la cerca

        new DownloadTask().execute(queryJobsParameters);
	}
	
	private void updateTags(){

		View tagLine;

		TextView valor;

		ImageButton botoEliminar;

		Integer i;

		linearLayoutWorkerProfileTags.removeAllViews();

		if (tagList != null){

			for (i = 0; i < tagList.size(); i++){

				LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				tagLine = inflater.inflate(R.layout.tag_item,null);

				valor = (TextView) tagLine.findViewById(R.id.textViewDescription);

				valor.setText(tagList.get(i));

				botoEliminar = (ImageButton) tagLine.findViewById(R.id.imageButton1);

				botoEliminar.setTag(i);

				botoEliminar.setOnClickListener(new Button.OnClickListener(){
	
					@Override
					public void onClick(View v) {

						App.getInstance().log("Eliminem l'etiqueta :" + v.getTag().toString());

						tagList.remove(Integer.parseInt(v.getTag().toString()));

						updateTags();

						//ViewGroup vg = (ViewGroup) v.getParent();

						//vg.setVisibility(View.GONE);
					}
				});

				linearLayoutWorkerProfileTags.addView(tagLine);
			}

            // Ens posicionem al final de la llista perquè es pugui veure el botó reset

            scrollViewSearch.fullScroll(scrollViewSearch.FOCUS_DOWN);
		}
	}

	public void clickAddTag(View view){

		if (editTextNewTag.getText().toString().length() > 0){

			if (tagList == null) {

				tagList = new ArrayList<String>();

			}

			tagList.add(editTextNewTag.getText().toString());

			updateTags();

			editTextNewTag.setText("");
		}
	}

    public void clickReset(View view){

        // Buidem els valors anterior

        if (editTextSearch != null) editTextSearch.setText("");

        if (editTextPriceSearch != null) editTextPriceSearch.setText("");

        // Per defecte, el sexe és "Cualquiera"

        spinnerGenderSearch.setSelection(2);

        // Buidem la llista d'etiquetes

        if (tagList != null) {

            tagList.clear();

            updateTags();
        }
    }
 }
