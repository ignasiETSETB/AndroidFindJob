package com.fish.fishapp.feines;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.Usuari;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.List;

public class FormulariContracte_Activity extends Activity {

    final Calendar c = Calendar.getInstance();

    ProgressDialog progressDialog;
    ProgressDialog ringProgressDialog;
    ScrollView scrollForm;
    Drawable originalDrawable;


    private static EditText dataIniciContracte;
    private static EditText dataFiContracte;

    // ADREÇA FISCAL
    private static EditText fiscalAdressPhoneNumber;
    private static EditText fiscalAdressEmail;
    private static EditText fiscalAdressAdress;
    private static Spinner fiscalAdressCountry;
    private static String fiscalAdressCountryString;
    private static EditText fiscalAdressLocalityName;
    private static EditText fiscalAdressPostalCode;
    private static Spinner fiscalAdressProvince;
    private static String fiscalAdressProvinceString;
    private static Spinner fiscalAdressTownName;
    private static String fiscalAdressTownNameString = "";
    private static EditText fiscalAdressWeb;

    // IDENTIFICACIÓ
    private static EditText identificationCompanyName;
    private static EditText identificationTradeName;
    private static EditText identificationNumber;
    private static Spinner tipusIdentificacio;
    private static String tipusIdentificacioString = "";

    // SIGNANT
    private static EditText signantEmail;
    private static EditText signantName;
    private static EditText signantPhoneNumber;
    private static EditText signantPosition;
    private static EditText signantIdentificationNumber;
    private static Spinner signantTipusIdentificacio;
    private static String signantTipusIdentificacioString = "";

    // LLOC TREBALL
    private static EditText llocTreballComptaCotitzacio;
    private static EditText llocTreballConveniClient;
    private static Spinner llocTreballCNAE;
    private static String llocTreballCNAEString = "";


    String idObjectHiredUser;
    String jobOffered;
    int priceHour;
    boolean hiredUserOK = false;
    ParseUser hiredUser;
    Resources res;


    @Override
    public void onResume(){

        super.onResume();
    }

    @Override
    public void onStart(){

        super.onStart();
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

        setContentView(R.layout.formulari_contracte);

        // Habilitem el botó per retrocedir de pantalla
        getActionBar().setDisplayHomeAsUpEnabled(true);

        res = getResources();


        init();

    }

    /**
     * Inicialitzar components formulari
     */
    public void init() {


        // DADES FIXES
        idObjectHiredUser = getIntent().getStringExtra("worker_id");
        jobOffered = getIntent().getStringExtra("jobOffered");
        priceHour = getIntent().getIntExtra("priceHour", 0);

        scrollForm = (ScrollView) findViewById(R.id.scrollForm);

        // Camps del formulari
        dataIniciContracte = (EditText) findViewById(R.id.dataIniciContracte);
        dataIniciContracte.setFocusable(false);
        dataIniciContracte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogDataInici(v);
            }

        });
        dataFiContracte = (EditText) findViewById(R.id.dataFiContracte);
        dataFiContracte.setFocusable(false);
        dataFiContracte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialogDataFi(v);
            }

        });

        // FISCAL
        fiscalAdressEmail = (EditText) findViewById(R.id.emailFiscal);
        // ADRESS
        fiscalAdressPhoneNumber = (EditText) findViewById(R.id.fiscalAddress_phoneNumber);
        fiscalAdressAdress = (EditText) findViewById(R.id.fiscalAddress_address);
        fiscalAdressCountry = (Spinner) findViewById(R.id.fiscalAddress_country);
        fiscalAdressProvince = (Spinner) findViewById(R.id.fiscalAddress_province);
        fiscalAdressTownName = (Spinner) findViewById(R.id.fiscalAddress_townName);
        fiscalAdressLocalityName = (EditText) findViewById(R.id.fiscalAddress_localityName);
        fiscalAdressPostalCode = (EditText) findViewById(R.id.fiscalAddress_postalCode);
        fiscalAdressWeb = (EditText) findViewById(R.id.fiscalAddress_web);
        // IDENTIFICACIÓ
        identificationCompanyName = (EditText) findViewById(R.id.identification_companyName);
        identificationTradeName = (EditText) findViewById(R.id.identification_tradeName);
        identificationNumber = (EditText) findViewById(R.id.identification_number);
        tipusIdentificacio = (Spinner) findViewById(R.id.identification_type);
        // SIGNANT
        signantTipusIdentificacio = (Spinner) findViewById(R.id.signing_identification_type);
        signantIdentificationNumber = (EditText) findViewById(R.id.signing_identification_number);
        signantEmail = (EditText) findViewById(R.id.signing_email);
        signantName = (EditText) findViewById(R.id.signing_name);
        signantPhoneNumber = (EditText) findViewById(R.id.signing_phone_number);
        signantPosition = (EditText) findViewById(R.id.signing_position);
        // LLOC TREBALL
        llocTreballComptaCotitzacio = (EditText) findViewById(R.id.workingPlace_cotitzacio);
        llocTreballConveniClient = (EditText) findViewById(R.id.workingPlace_conveniClient);
        llocTreballCNAE = (Spinner) findViewById(R.id.workingPlace_cnae);

        originalDrawable = dataIniciContracte.getBackground();

        App.getInstance().log("ID HIRED_USER - " + idObjectHiredUser);


        progressDialog = new ProgressDialog(this);


        // SPINNER TIPUS IDENTIFICACIÓ
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.form_tipus_identificacio_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tipusIdentificacio.setAdapter(adapter);
        tipusIdentificacio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipusIdentificacioString = getResources().getStringArray(R.array.form_tipus_identificacio_array_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        signantTipusIdentificacio.setAdapter(adapter);
        signantTipusIdentificacio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                signantTipusIdentificacioString = getResources().getStringArray(R.array.form_tipus_identificacio_array_values)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        // SPINNER MUNICIPI
        ArrayAdapter<CharSequence> adapterMunicipi = ArrayAdapter.createFromResource(
                this, R.array.form_municipis_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fiscalAdressTownName.setAdapter(adapterMunicipi);
        fiscalAdressTownName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fiscalAdressTownNameString = getResources().getStringArray(R.array.form_municipis_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER PROVINCIA
        ArrayAdapter<CharSequence> adapterProvincia = ArrayAdapter.createFromResource(
                this, R.array.form_provincies_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fiscalAdressProvince.setAdapter(adapterProvincia);
        fiscalAdressProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fiscalAdressProvinceString = getResources().getStringArray(R.array.form_provincies_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // SPINNER PAIS
        ArrayAdapter<CharSequence> adapterPais = ArrayAdapter.createFromResource(
                this, R.array.form_paisos_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fiscalAdressCountry.setAdapter(adapterPais);
        fiscalAdressCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fiscalAdressCountryString = getResources().getStringArray(R.array.form_paisos_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // SPINNER CNAE
        ArrayAdapter<CharSequence> adapterCNAE = ArrayAdapter.createFromResource(
                this, R.array.form_cnae_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        llocTreballCNAE.setAdapter(adapterCNAE);
        llocTreballCNAE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                llocTreballCNAEString = getResources().getStringArray(R.array.form_cnae_array_values)[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }



    public void clickCancelar(View view) {
        finish();
    }


    public void clickAcceptar(View view) {

        App.getInstance().log(" Botó enviar formulari");
        boolean campsCorrectes = true;

        // Comprovar camps obligatoris

        // LLOC TREBALL

        /*
        if (llocTreballComptaCotitzacio.getText().length() < 9){
            campsCorrectes = false;
            llocTreballComptaCotitzacio.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, llocTreballComptaCotitzacio.getTop());
        } else {
            llocTreballComptaCotitzacio.setBackground(originalDrawable);
        }

        if (llocTreballConveniClient.getText().length() == 0){
            campsCorrectes = false;
            llocTreballConveniClient.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, llocTreballConveniClient.getTop());
        } else {
            llocTreballConveniClient.setBackground(originalDrawable);
        }

        if (llocTreballCNAEString.length() == 0){
            campsCorrectes = false;
            llocTreballCNAE.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, llocTreballCNAE.getTop());
        } else {
            llocTreballCNAE.setBackground(originalDrawable);
        }


        // SIGNANT
        if (signantTipusIdentificacioString.length() == 0){
            campsCorrectes = false;
            signantTipusIdentificacio.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, signantTipusIdentificacio.getTop());
        } else {
            signantTipusIdentificacio.setBackground(originalDrawable);
        }

        if (signantIdentificationNumber.getText().length() == 0){
            campsCorrectes = false;
            signantIdentificationNumber.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, signantIdentificationNumber.getTop());
        } else {
            signantIdentificationNumber.setBackground(originalDrawable);
        }

        if (signantPosition.getText().length() == 0){
            campsCorrectes = false;
            signantPosition.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, signantPosition.getTop());
        } else {
            signantPosition.setBackground(originalDrawable);
        }

        if (signantName.getText().length() == 0){
            campsCorrectes = false;
            signantName.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, signantName.getTop());
        } else {
            signantName.setBackground(originalDrawable);
        }

        // FISCAL
        if (fiscalAdressTownNameString.length() == 0){
            campsCorrectes = false;
            fiscalAdressTownName.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressTownName.getTop());
        } else {
            fiscalAdressTownName.setBackground(originalDrawable);
        }


        if (fiscalAdressProvinceString.length() == 0){
            campsCorrectes = false;
            fiscalAdressProvince.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressProvince.getTop());
        } else {
            fiscalAdressProvince.setBackground(originalDrawable);
        }

        if (fiscalAdressPostalCode.getText().length() < 5){
            campsCorrectes = false;
            fiscalAdressPostalCode.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressPostalCode.getTop());
        } else {
            fiscalAdressPostalCode.setBackground(originalDrawable);
        }


        if (fiscalAdressLocalityName.getText().length() == 0){
            campsCorrectes = false;
            fiscalAdressLocalityName.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressLocalityName.getTop());
        } else {
            fiscalAdressLocalityName.setBackground(originalDrawable);
        }

        if (fiscalAdressCountryString.length() == 0){
            campsCorrectes = false;
            fiscalAdressCountry.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressCountry.getTop());
        } else {
            fiscalAdressCountry.setBackground(originalDrawable);
        }

        if (fiscalAdressAdress.getText().length() == 0){
            campsCorrectes = false;
            fiscalAdressAdress.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressAdress.getTop());
        } else {
            fiscalAdressAdress.setBackground(originalDrawable);
        }

        if (fiscalAdressEmail.getText().length() == 0){
            campsCorrectes = false;
            fiscalAdressEmail.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressEmail.getTop());
        } else {
            fiscalAdressEmail.setBackground(originalDrawable);
        }

        if (fiscalAdressPhoneNumber.getText().length() == 0){
            campsCorrectes = false;
            fiscalAdressPhoneNumber.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, fiscalAdressPhoneNumber.getTop());
        } else {
            fiscalAdressPhoneNumber.setBackground(originalDrawable);
        }


        // IDENTIFICACIÓ
        if (tipusIdentificacioString.length() == 0){
            campsCorrectes = false;
            tipusIdentificacio.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, tipusIdentificacio.getTop());
        } else {
            tipusIdentificacio.setBackground(originalDrawable);
        }

        if (identificationNumber.getText().length() == 0){
            campsCorrectes = false;
            identificationNumber.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, identificationNumber.getTop());
        } else {
            identificationNumber.setBackground(originalDrawable);
        }

        if (identificationCompanyName.getText().length() == 0){
            campsCorrectes = false;
            identificationCompanyName.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, identificationCompanyName.getTop());
        } else {
            identificationCompanyName.setBackground(originalDrawable);
        }


        // DATES
        if (dataFiContracte.getText().length() == 0){
            campsCorrectes = false;
            dataFiContracte.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
            scrollForm.scrollTo(0, dataFiContracte.getTop());
        } else {
            dataFiContracte.setBackground(originalDrawable);
        }

        if (dataIniciContracte.getText().length() == 0){
           campsCorrectes = false;
           dataIniciContracte.setBackground(getResources().getDrawable(R.drawable.border_error_formulari));
           dataIniciContracte.requestFocus();
        } else {
            dataIniciContracte.setBackground(originalDrawable);
        }
*/


        if (campsCorrectes) {
            // Presentem la barra de progrés
            ringProgressDialog = ProgressDialog.show(this, "", App.getInstance().getStringResource(R.string.uploading), true, false);
            ringProgressDialog.setCancelable(true);

            // Guardem les dades actualitzades del contracte

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        Usuari usr = App.getInstance().usuari;
                        FormulariEmpresa fe = new FormulariEmpresa();

                        fe.setJobOffered(jobOffered);
                        fe.setPriceHour(priceHour);

                        fe.setDataIniciContracte(dataIniciContracte.getText().toString());
                        fe.setDataFiContracte(dataFiContracte.getText().toString());

                        fe.setEmailFiscal(fiscalAdressEmail.getText().toString());

                        // FISCAL ADRESS
                        fe.setAdrecaFiscal(fiscalAdressAdress.getText().toString());
                        fe.setPaisFiscal(fiscalAdressCountryString);
                        fe.setProvinciaFiscal(fiscalAdressProvinceString);
                        fe.setLocalitatFiscal(fiscalAdressLocalityName.getText().toString());
                        fe.setMunicipiFiscal(fiscalAdressTownNameString);
                        fe.setTelefonFiscal(fiscalAdressPhoneNumber.getText().toString());
                        fe.setCpFiscal(fiscalAdressPostalCode.getText().toString());
                        fe.setWebFiscal(fiscalAdressWeb.getText().toString());

                        // FISCAL IDENTIFICATION
                        fe.setNomEmpresaIdentificacio(identificationCompanyName.getText().toString());
                        fe.setTipusDocumentEmpresaIdentificacio(tipusIdentificacioString);
                        fe.setNumeroDocumentEmpresaIdentificacio(identificationNumber.getText().toString());
                        fe.setNomComercialEmpresaIdentificacio(identificationTradeName.getText().toString());

                        // SIGNING
                        fe.setTipusDocumentPersonaSignant(signantTipusIdentificacioString);
                        fe.setNumeroDocumentPersonaSignant(signantIdentificationNumber.getText().toString());
                        fe.setEmailPersonaSignant(signantEmail.getText().toString());
                        fe.setNomPersonaSignant(signantName.getText().toString());
                        fe.setTelefonPersonaSignant(signantPhoneNumber.getText().toString());
                        fe.setCarrecPersonaSignant(signantPosition.getText().toString());

                        // LLOC TREBALL
                        fe.setCNAELlocTreball(llocTreballCNAEString);
                        fe.setConveniClientLlocTreball(llocTreballConveniClient.getText().toString());
                        fe.setComptaCotizacionSSLlocTreball(llocTreballComptaCotitzacio.getText().toString());


                        fe.setHirerUser(ParseUser.getCurrentUser());

                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("objectId", idObjectHiredUser);

                        try {
                            hiredUser = query.find().get(0);
                            fe.setHiredUser(hiredUser);
                            fe.save();
                            finish();

                        } catch (ParseException e1) {
                            App.getInstance().log("ERROR FORM: HiredUSer no encontrado");
                            e1.printStackTrace();
                        }

                    } catch (Exception e) {
                        App.getInstance().log("ERROR FORM: " + e.toString());
                        e.printStackTrace();

                    }

                    ringProgressDialog.dismiss();
                }

            }).start();
        } else {
            Toast.makeText(this, "Hay campos incompletos o incorrectos", Toast.LENGTH_LONG).show();
        }


    }





    // DATEPICKERS

    // INICI CONTRACTE
    public static class datePickerInici extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String ceroDia = "";
            String ceroMes = "";
            if (day < 10) {
                ceroDia = "0";
            }

            if ( (month + 1) < 10) {
                ceroMes = "0";
            }

            dataIniciContracte.setText( ceroDia + day + "/" +  ceroMes + (month + 1) + "/" + year);
        }
    }

    public void showDatePickerDialogDataInici(View v) {
        DialogFragment newFragment = new datePickerInici();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    // FI CONTRACTE
    public static class datePickerFi extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String ceroDia = "";
            String ceroMes = "";
            if (day < 10) {
                ceroDia = "0";
            }

            if ( (month + 1) < 10) {
                ceroMes = "0";
            }

            dataFiContracte.setText(ceroDia + day + "/" + ceroMes + (month + 1) + "/" + year);
        }
    }

    public void showDatePickerDialogDataFi(View v) {
        DialogFragment newFragment = new datePickerFi();
        newFragment.show(getFragmentManager(), "datePicker");
    }


}
