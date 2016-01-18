package com.fish.fishapp.feines;

import android.graphics.Bitmap;
import android.location.Location;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.utils.MyCallback;
import com.fish.fishapp.utils.Server;
import com.fish.fishapp.utils.ServerException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

public class FormulariEmpresa {

	String id;
	ParseUser hirerUser;
	ParseUser hiredUser;
	String jobOffered;
	int priceHour;


	String raoSocial;
	String dataIniciContracte;
	String dataFiContracte;
	String tipusIdentificacio;

	String telefonFiscal;
	String emailFiscal;
	String adrecaFiscal;
	String paisFiscal;
	String localitatFiscal;
	String municipiFiscal;
	String cpFiscal;
	String provinciaFiscal;
	String webFiscal;

	String nomEmpresaIdentificacio;
	String numeroDocumentEmpresaIdentificacio;
	String tipusDocumentEmpresaIdentificacio;
	String nomComercialEmpresaIdentificacio;

	String numeroDocumentPersonaSignant;
	String tipusDocumentPersonaSignant;
	String emailPersonaSignant;
	String nomPersonaSignant;
	String telefonPersonaSignant;
	String carrecPersonaSignant;

	String CNAELlocTreball;
	String conveniClientLlocTreball;
	String comptaCotizacionSSLlocTreball;


	String IBANTreballador;
	String SWIFTTreballador;
	String adrecaTreballador;
	String DataNaixementTreballador;
	String nomTreballador;
	String cognom1Treballador;
	String cognom2Treballador;
	String numeroDocumentTreballador;
	String tipusDocumentTreballador;
	String emailTreballador;
	String numeroSSTreballador;
	String telefonTreballador;
	String sexeTreballador;





	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRaoSocial() {
		return raoSocial;
	}

	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}

	public String getDataIniciContracte() {
		return dataIniciContracte;
	}

	public void setDataIniciContracte(String dataIniciContracte) {
		this.dataIniciContracte = dataIniciContracte;
	}

	public String getTipusIdentificacio() {
		return tipusIdentificacio;
	}

	public void setTipusIdentificacio(String tipusIdentificacio) {
		this.tipusIdentificacio = tipusIdentificacio;
	}

	public String getDataFiContracte() {
		return dataFiContracte;
	}

	public void setDataFiContracte(String dataFiContracte) {
		this.dataFiContracte = dataFiContracte;
	}

	public String getTelefonFiscal() {
		return telefonFiscal;
	}

	public void setTelefonFiscal(String telefonFiscal) {
		this.telefonFiscal = telefonFiscal;
	}

	public String getEmailFiscal() {
		return emailFiscal;
	}

	public void setEmailFiscal(String emailFiscal) {
		this.emailFiscal = emailFiscal;
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


	public String getAdrecaFiscal() {
		return adrecaFiscal;
	}

	public void setAdrecaFiscal(String adrecaFiscal) {
		this.adrecaFiscal = adrecaFiscal;
	}

	public String getPaisFiscal() {
		return paisFiscal;
	}

	public void setPaisFiscal(String paisFiscal) {
		this.paisFiscal = paisFiscal;
	}

	public String getLocalitatFiscal() {
		return localitatFiscal;
	}

	public void setLocalitatFiscal(String localitatFiscal) {
		this.localitatFiscal = localitatFiscal;
	}

	public String getMunicipiFiscal() {
		return municipiFiscal;
	}

	public void setMunicipiFiscal(String municipiFiscal) {
		this.municipiFiscal = municipiFiscal;
	}

	public String getCpFiscal() {
		return cpFiscal;
	}

	public void setCpFiscal(String cpFiscal) {
		this.cpFiscal = cpFiscal;
	}

	public String getProvinciaFiscal() {
		return provinciaFiscal;
	}

	public void setProvinciaFiscal(String provinciaFiscal) {
		this.provinciaFiscal = provinciaFiscal;
	}

	public String getWebFiscal() {
		return webFiscal;
	}

	public void setWebFiscal(String webFiscal) {
		this.webFiscal = webFiscal;
	}

	public String getNomEmpresaIdentificacio() {
		return nomEmpresaIdentificacio;
	}

	public void setNomEmpresaIdentificacio(String nomEmpresaIdentificacio) {
		this.nomEmpresaIdentificacio = nomEmpresaIdentificacio;
	}

	public String getNumeroDocumentEmpresaIdentificacio() {
		return numeroDocumentEmpresaIdentificacio;
	}

	public void setNumeroDocumentEmpresaIdentificacio(String numeroDocumentEmpresaIdentificacio) {
		this.numeroDocumentEmpresaIdentificacio = numeroDocumentEmpresaIdentificacio;
	}

	public String getTipusDocumentEmpresaIdentificacio() {
		return tipusDocumentEmpresaIdentificacio;
	}

	public void setTipusDocumentEmpresaIdentificacio(String tipusDocumentEmpresaIdentificacio) {
		this.tipusDocumentEmpresaIdentificacio = tipusDocumentEmpresaIdentificacio;
	}

	public String getNomComercialEmpresaIdentificacio() {
		return nomComercialEmpresaIdentificacio;
	}

	public void setNomComercialEmpresaIdentificacio(String nomComercialEmpresaIdentificacio) {
		this.nomComercialEmpresaIdentificacio = nomComercialEmpresaIdentificacio;
	}

	public String getNumeroDocumentPersonaSignant() {
		return numeroDocumentPersonaSignant;
	}

	public void setNumeroDocumentPersonaSignant(String numeroDocumentPersonaSignant) {
		this.numeroDocumentPersonaSignant = numeroDocumentPersonaSignant;
	}

	public String getTipusDocumentPersonaSignant() {
		return tipusDocumentPersonaSignant;
	}

	public void setTipusDocumentPersonaSignant(String tipusDocumentPersonaSignant) {
		this.tipusDocumentPersonaSignant = tipusDocumentPersonaSignant;
	}

	public String getEmailPersonaSignant() {
		return emailPersonaSignant;
	}

	public void setEmailPersonaSignant(String emailPersonaSignant) {
		this.emailPersonaSignant = emailPersonaSignant;
	}

	public String getNomPersonaSignant() {
		return nomPersonaSignant;
	}

	public void setNomPersonaSignant(String nomPersonaSignant) {
		this.nomPersonaSignant = nomPersonaSignant;
	}

	public String getTelefonPersonaSignant() {
		return telefonPersonaSignant;
	}

	public void setTelefonPersonaSignant(String telefonPersonaSignant) {
		this.telefonPersonaSignant = telefonPersonaSignant;
	}

	public String getCarrecPersonaSignant() {
		return carrecPersonaSignant;
	}

	public void setCarrecPersonaSignant(String carrecPersonaSignant) {
		this.carrecPersonaSignant = carrecPersonaSignant;
	}

	public String getCNAELlocTreball() {
		return CNAELlocTreball;
	}

	public void setCNAELlocTreball(String CNAELlocTreball) {
		this.CNAELlocTreball = CNAELlocTreball;
	}

	public String getConveniClientLlocTreball() {
		return conveniClientLlocTreball;
	}

	public void setConveniClientLlocTreball(String conveniClientLlocTreball) {
		this.conveniClientLlocTreball = conveniClientLlocTreball;
	}

	public String getComptaCotizacionSSLlocTreball() {
		return comptaCotizacionSSLlocTreball;
	}

	public void setComptaCotizacionSSLlocTreball(String comptaCotizacionSSLlocTreball) {
		this.comptaCotizacionSSLlocTreball = comptaCotizacionSSLlocTreball;
	}

	public String getIBANTreballador() {
		return IBANTreballador;
	}

	public void setIBANTreballador(String IBANTreballador) {
		this.IBANTreballador = IBANTreballador;
	}

	public String getSWIFTTreballador() {
		return SWIFTTreballador;
	}

	public void setSWIFTTreballador(String SWIFTTreballador) {
		this.SWIFTTreballador = SWIFTTreballador;
	}

	public String getAdrecaTreballador() {
		return adrecaTreballador;
	}

	public void setAdrecaTreballador(String adrecaTreballador) {
		this.adrecaTreballador = adrecaTreballador;
	}

	public String getDataNaixementTreballador() {
		return DataNaixementTreballador;
	}

	public void setDataNaixementTreballador(String dataNaixementTreballador) {
		DataNaixementTreballador = dataNaixementTreballador;
	}

	public String getNomTreballador() {
		return nomTreballador;
	}

	public void setNomTreballador(String nomTreballador) {
		this.nomTreballador = nomTreballador;
	}

	public String getCognom1Treballador() {
		return cognom1Treballador;
	}

	public void setCognom1Treballador(String cognom1Treballador) {
		this.cognom1Treballador = cognom1Treballador;
	}

	public String getCognom2Treballador() {
		return cognom2Treballador;
	}

	public void setCognom2Treballador(String cognom2Treballador) {
		this.cognom2Treballador = cognom2Treballador;
	}

	public String getNumeroDocumentTreballador() {
		return numeroDocumentTreballador;
	}

	public void setNumeroDocumentTreballador(String numeroDocumentTreballador) {
		this.numeroDocumentTreballador = numeroDocumentTreballador;
	}

	public String getTipusDocumentTreballador() {
		return tipusDocumentTreballador;
	}

	public void setTipusDocumentTreballador(String tipusDocumentTreballador) {
		this.tipusDocumentTreballador = tipusDocumentTreballador;
	}

	public String getEmailTreballador() {
		return emailTreballador;
	}

	public void setEmailTreballador(String emailTreballador) {
		this.emailTreballador = emailTreballador;
	}

	public String getNumeroSSTreballador() {
		return numeroSSTreballador;
	}

	public void setNumeroSSTreballador(String numeroSSTreballador) {
		this.numeroSSTreballador = numeroSSTreballador;
	}

	public String getTelefonTreballador() {
		return telefonTreballador;
	}

	public void setTelefonTreballador(String telefonTreballador) {
		this.telefonTreballador = telefonTreballador;
	}

	public String getSexeTreballador() {
		return sexeTreballador;
	}

	public void setSexeTreballador(String sexeTreballador) {
		this.sexeTreballador = sexeTreballador;
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

	public void save() {

		App.getInstance().log("Formulari.save()");

        try {
			App.getInstance().log("Formulari.save(): ");
            Server.saveFormulariEmpresa(this);
        } catch (ServerException e) {
            e.printStackTrace();
			App.getInstance().missatgeEnPantalla(App.getInstance().getStringResource(R.string.server_error));
			App.getInstance().log("ERROR: " + e.toString());
        }
    }

    public void logout() {
        Server.logout();
    }
}
