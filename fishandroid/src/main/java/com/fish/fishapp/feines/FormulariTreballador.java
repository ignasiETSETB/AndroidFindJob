package com.fish.fishapp.feines;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormulariTreballador {

	String id;
	String idRegistreFeina;
	ParseUser hirerUser;
	String hirerUserId;
	ParseUser hiredUser;
	String jobOffered;
	int priceHour;


	ParseObject registreFeina;

	String nom;
	String cognom1;
	String cognom2;
	String tipusIdentificacio;
	String document;
	String nacionalitat;
	String sigla;
	String carrer;
	String numero;
	String porta;
	String codiPostal;
	String poblacio;
	String municipi;
	String provincia;
	String pais;
	String telefon;
	String numeroSS;
	String email;
	String dataNaixement;
	String sexe;
	String nivellFormatiu;
	String iban;
	String swift;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ParseUser getHirerUser() {
		return hirerUser;
	}

	public void setHirerUser(ParseUser hirerUser) {
		this.hirerUser = hirerUser;
	}

	public ParseUser getHiredUser() {
		return hiredUser;
	}

	public void setHiredUser(ParseUser hiredUser) {
		this.hiredUser = hiredUser;
	}

	public String getJobOffered() {
		return jobOffered;
	}

	public void setJobOffered(String jobOffered) {
		this.jobOffered = jobOffered;
	}

	public int getPriceHour() {
		return priceHour;
	}

	public void setPriceHour(int priceHour) {
		this.priceHour = priceHour;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCognom1() {
		return cognom1;
	}

	public void setCognom1(String cognom1) {
		this.cognom1 = cognom1;
	}

	public String getCognom2() {
		return cognom2;
	}

	public void setCognom2(String cognom2) {
		this.cognom2 = cognom2;
	}

	public String getTipusIdentificacio() {
		return tipusIdentificacio;
	}

	public void setTipusIdentificacio(String tipusIdentificacio) {
		this.tipusIdentificacio = tipusIdentificacio;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getNacionalitat() {
		return nacionalitat;
	}

	public void setNacionalitat(String nacionalitat) {
		this.nacionalitat = nacionalitat;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getCarrer() {
		return carrer;
	}

	public void setCarrer(String carrer) {
		this.carrer = carrer;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPorta() {
		return porta;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}

	public String getCodiPostal() {
		return codiPostal;
	}

	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
	}

	public String getPoblacio() {
		return poblacio;
	}

	public void setPoblacio(String poblacio) {
		this.poblacio = poblacio;
	}

	public String getMunicipi() {
		return municipi;
	}

	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public String getNumeroSS() {
		return numeroSS;
	}

	public void setNumeroSS(String numeroSS) {
		this.numeroSS = numeroSS;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDataNaixement() {
		return dataNaixement;
	}

	public void setDataNaixement(String dataNaixement) {
		this.dataNaixement = dataNaixement;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public String getNivellFormatiu() {
		return nivellFormatiu;
	}

	public void setNivellFormatiu(String nivellFormatiu) {
		this.nivellFormatiu = nivellFormatiu;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getSwift() {
		return swift;
	}

	public void setSwift(String swift) {
		this.swift = swift;
	}

	public ParseObject getRegistreFeina() {
		return registreFeina;
	}

	public void setRegistreFeina(ParseObject registreFeina) {
		this.registreFeina = registreFeina;
	}

	public String getIdRegistreFeina() {
		return idRegistreFeina;
	}

	public void setIdRegistreFeina(String idRegistreFeina) {
		this.idRegistreFeina = idRegistreFeina;
	}


	public String getHirerUserId() {
		return hirerUserId;
	}

	public void setHirerUserId(String hirerUserId) {
		this.hirerUserId = hirerUserId;
	}

	public void update() {

		App.getInstance().log(" FormulariTreballador.update()");

        try {

			// Guardar formulari
            Server.saveFormulariTreballador(this);
			App.getInstance().log(" Formulari saved OK");

			// Enviar notificacio PUSH d'aceptació a la Empresa
			Map<String, Object> hm = new HashMap<>();
			App.getInstance().log("DESTINATARI: " + hirerUserId);

			ArrayList<String> destinataries = new ArrayList<>();
			destinataries.add(hirerUserId);


			hm.put("message", ParseUser.getCurrentUser().get("profileFirstName") + " aceptó la oferta '" + jobOffered + "'" );
			hm.put("from", ParseUser.getCurrentUser().getObjectId());
			hm.put("destinataries", destinataries);


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
        } catch (ServerException e) {
            e.printStackTrace();
            App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));
        }
    }

    public void logout() {
        Server.logout();
    }
}
