package com.fish.fishapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.fish.fishapp.App;
import com.fish.fishapp.R;
import com.fish.fishapp.contact.Xat_Contacte;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Utils {

	/**
	 *
	 * <br>
	 * Obtenim el sexe, segons al tipus de sexe donat: Home = 1, Dona = 2 i Qualsevol = 3
	 *
	 * @param tipusSexe Valor, corresponent al tipus de sexe
	 *
	 */

	public static String ObtenirSexeNom(Integer tipusSexe){

		String nomSexe = "";

		// Obtenim tots els elements de la matriu que conté els tipus de sexe

		String[] aSexes = App.getInstance().appContext.getResources().getStringArray(R.array.list_of_genders_both);

		// Comprovem que el valor donat estigui dins els límits de la matriu

		if (tipusSexe > 0 && tipusSexe <= aSexes.length) {

			nomSexe = aSexes[tipusSexe - 1];

		} else {

			nomSexe = aSexes[2];
		}

		// Tornem el sexe, segons al tipus de sexe donat: Home = 1, Dona = 2 i Qualsevol = 3

		return nomSexe;
	}

	/**
	 *
	 * <br>
	 * Obtenim el tipus del sexe, segons el sexe donat: Home = 1, Dona = 2 i Qualsevol = 3
	 *
	 * @param nomSexe Descripció del sexe
	 *
	 */

	public static Integer ObtenirSexeValor(String nomSexe){

		// Obtenim tots els elements de la matriu que conté els tipus de sexe

		String[] aSexes = App.getInstance().appContext.getResources().getStringArray(R.array.list_of_genders_both);

		// Obtenim la posició que ocupa el tipus de sexe donat, dins la matriu

		int tipusSexe = Arrays.binarySearch(aSexes, nomSexe);

		// Tornem el tipus del sexe, segons el sexe donat: Home = 1, Dona = 2 i Qualsevol = 3

		return tipusSexe + 1;
	}

	/**
	 *
	 * <br>
	 * Obtenim la distància entre els dos punts donats, en kilòmetres
	 *
	 * @param posicio1 Posició del primer punt
	 * @param posicio2 Posició del segon punt
	 *
	 */

	public static String distancia(Location posicio1, Location posicio2) {

		String resultat = "";

		if (posicio1 == null || posicio2 == null){

			resultat = "? Km";

		} else {

			// Obtenim la distància entre els dos punts, en metres

			Float metros = posicio1.distanceTo(posicio2);

			// Convertim a kilòmetres

			Float kilometros = metros/1000;

			// Li donem format

			DecimalFormat df = new DecimalFormat("###");

			resultat = df.format(kilometros) + " Km";
		}

		// Tornem la distància entre els dos punts donats, en kilòmetres

		return resultat;
	}

	public static String getCurrencySymbolString(String iso){
		String res="";
		if (iso!=null){
			if (iso.equals("USD")){
				res = "$";
			} else if (iso.equals("EUR")){
				res="€";
			}
		}
		
		return res;
	}
	
	public static String getCurrencyDisplayString(String iso){
		String res = "";
		if (iso != null){
			if (iso.equals("USD")){
				res = "$ USD";
			} else if (iso.equals("EUR")){
				res="€ EUR";
			}
		}

		return res;
	}
	
	public static String getCurrencyIsoString(String display){
		String res="";
		res = display.substring(2, 5);
		return res;
	}
	
	public static String getEdad(Date birthDay){

        String res="";

        // Comprovem si tenim una data

        if (birthDay == null){

            return res;

        }

		Calendar dob = Calendar.getInstance();
		dob.setTime(birthDay);
		Calendar today = Calendar.getInstance();  
		Integer age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);  
		if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
		  age--;  
		} else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
		    && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
		  age--;  
		}
		res =age.toString();
		
		return res;
		
	}

	public static String getWorkTypeDisplayString(Integer workerProfileWorkType) {
		String res="";
		// get strings from resource file
		String[] workTypes = App.getInstance().getStringArrayResource(R.array.list_of_work_types);
		if(workerProfileWorkType==null){
			workerProfileWorkType=255;
		}
		switch(workerProfileWorkType){
		case 1:
			res = workTypes[0]; //"Presential"
			break;
		case 2:
			res = workTypes[1]; //"Remote";
			break;
		case 255:
			res= workTypes[2]; //"Any";

		}
		
		return res;
	}

	public static Integer getWorkTypeCode(String string) {
		Integer res=null;
		// get strings from resource file
		String[] workTypes = App.getInstance().getStringArrayResource(R.array.list_of_work_types);
		if (string.equals(workTypes[0])){ //"Presential"
			res=1;
		} else if (string.equals(workTypes[1])){ //"Remote"
			res=2;
		} else if (string.equals(workTypes[2])){ //"Any"
			res = 255;
		}
		return res;
	}
	
	public static Integer getRadiusFromDisplay(String str){
		Integer res=1;
		App.getInstance().log("Str:" + str);
		str=str.replace(" Km","");
		if (str.equals("+50")){
			res=100;
		} else {
			res = Integer.parseInt(str);
		}
		return res;
		
		
	}

	public static Integer parseInt(String string) {
		Integer res=null;
		if (string.length()>0){
			try{
				
				res = Integer.parseInt(string);
			} catch (Exception e){
				res=999999;
			}	
		}
		App.getInstance().log("parseInt returns:" + res);
		return res;
	}
	
	public static String tagListToString(ArrayList<String> tagList){
		String res="";
		if (tagList!=null){
			for (int i=0;i!=tagList.size();i++){
				if (res.length()==0){
					res = tagList.get(i);
				} else {
					res = res + ", " + tagList.get(i);
				}
			}
		}
		App.getInstance().log("Tag List to String return " + res);
		return res;
	}

	public static void removeDateFromList(Calendar x, List<Date> inEditionAvailabylity) {
		List<Date> toBeRemoved = new ArrayList<Date>();
		
		x.set(Calendar.HOUR_OF_DAY, 0);
		x.set(Calendar.MINUTE, 0);
		x.set(Calendar.SECOND, 0);
		x.set(Calendar.MILLISECOND, 0);
		
		
		
		Calendar y = Calendar.getInstance();
		//y.setTime(date)
		for (Date i:inEditionAvailabylity){
			y.setTime(i);
			y.set(Calendar.HOUR_OF_DAY, 0);
			y.set(Calendar.MINUTE, 0);
			y.set(Calendar.SECOND, 0);
			y.set(Calendar.MILLISECOND, 0);
			
			if (y.get(Calendar.DAY_OF_MONTH)==19){
				App.getInstance().log("19");
				App.getInstance().log("a)" + i.toString());
				App.getInstance().log("b)" + x.toString());
				App.getInstance().log("c)" + y.toString());
			}
			if (y.compareTo(x)==0){
				//son iguals, el trec
				toBeRemoved.add(i);
				//inEditionAvailabylity.remove(i); //No se puede hacer directament, java.util.ConcurrentModificationException
			}
		}
		for (Date i:toBeRemoved){
			App.getInstance().log("Removing date");
			inEditionAvailabylity.remove(i);
		}
		
		
	}
	
	public static String removeDiacritics(String string){
		String res;
		HashMap<String,String> defaultDiacriticsRemovalMap = new HashMap<String,String>();
		
		defaultDiacriticsRemovalMap.put("A","\u0041\u24B6\uFF21\u00C0\u00C1\u00C2\u1EA6\u1EA4\u1EAA\u1EA8\u00C3\u0100\u0102\u1EB0\u1EAE\u1EB4\u1EB2\u0226\u01E0\u00C4\u01DE\u1EA2\u00C5\u01FA\u01CD\u0200\u0202\u1EA0\u1EAC\u1EB6\u1E00\u0104\u023A\u2C6F");
		defaultDiacriticsRemovalMap.put("AA","\uA732");
		defaultDiacriticsRemovalMap.put("AE","\u00C6\u01FC\u01E2");
		defaultDiacriticsRemovalMap.put("AO","\uA734");
		defaultDiacriticsRemovalMap.put("AU","\uA736");
		defaultDiacriticsRemovalMap.put("AV","\uA738\uA73A");
		defaultDiacriticsRemovalMap.put("AY","\uA73C");
		defaultDiacriticsRemovalMap.put("B","\u0042\u24B7\uFF22\u1E02\u1E04\u1E06\u0243\u0182\u0181");
		defaultDiacriticsRemovalMap.put("C","\u0043\u24B8\uFF23\u0106\u0108\u010A\u010C\u00C7\u1E08\u0187\u023B\uA73E");
		defaultDiacriticsRemovalMap.put("D","\u0044\u24B9\uFF24\u1E0A\u010E\u1E0C\u1E10\u1E12\u1E0E\u0110\u018B\u018A\u0189\uA779");
		defaultDiacriticsRemovalMap.put("DZ","\u01F1\u01C4");
		defaultDiacriticsRemovalMap.put("Dz","\u01F2\u01C5");
		defaultDiacriticsRemovalMap.put("E","\u0045\u24BA\uFF25\u00C8\u00C9\u00CA\u1EC0\u1EBE\u1EC4\u1EC2\u1EBC\u0112\u1E14\u1E16\u0114\u0116\u00CB\u1EBA\u011A\u0204\u0206\u1EB8\u1EC6\u0228\u1E1C\u0118\u1E18\u1E1A\u0190\u018E");
		defaultDiacriticsRemovalMap.put("F","\u0046\u24BB\uFF26\u1E1E\u0191\uA77B");
		defaultDiacriticsRemovalMap.put("G","\u0047\u24BC\uFF27\u01F4\u011C\u1E20\u011E\u0120\u01E6\u0122\u01E4\u0193\uA7A0\uA77D\uA77E");
		defaultDiacriticsRemovalMap.put("H","\u0048\u24BD\uFF28\u0124\u1E22\u1E26\u021E\u1E24\u1E28\u1E2A\u0126\u2C67\u2C75\uA78D");
		defaultDiacriticsRemovalMap.put("I","\u0049\u24BE\uFF29\u00CC\u00CD\u00CE\u0128\u012A\u012C\u0130\u00CF\u1E2E\u1EC8\u01CF\u0208\u020A\u1ECA\u012E\u1E2C\u0197");
		defaultDiacriticsRemovalMap.put("J","\u004A\u24BF\uFF2A\u0134\u0248");
		defaultDiacriticsRemovalMap.put("K","\u004B\u24C0\uFF2B\u1E30\u01E8\u1E32\u0136\u1E34\u0198\u2C69\uA740\uA742\uA744\uA7A2");
		defaultDiacriticsRemovalMap.put("L","\u004C\u24C1\uFF2C\u013F\u0139\u013D\u1E36\u1E38\u013B\u1E3C\u1E3A\u0141\u023D\u2C62\u2C60\uA748\uA746\uA780");
		defaultDiacriticsRemovalMap.put("LJ","\u01C7");
		defaultDiacriticsRemovalMap.put("Lj","\u01C8");
		defaultDiacriticsRemovalMap.put("M","\u004D\u24C2\uFF2D\u1E3E\u1E40\u1E42\u2C6E\u019C");
		defaultDiacriticsRemovalMap.put("N","\u004E\u24C3\uFF2E\u01F8\u0143\u00D1\u1E44\u0147\u1E46\u0145\u1E4A\u1E48\u0220\u019D\uA790\uA7A4");
		defaultDiacriticsRemovalMap.put("NJ","\u01CA");
		defaultDiacriticsRemovalMap.put("Nj","\u01CB");
		defaultDiacriticsRemovalMap.put("O","\u004F\u24C4\uFF2F\u00D2\u00D3\u00D4\u1ED2\u1ED0\u1ED6\u1ED4\u00D5\u1E4C\u022C\u1E4E\u014C\u1E50\u1E52\u014E\u022E\u0230\u00D6\u022A\u1ECE\u0150\u01D1\u020C\u020E\u01A0\u1EDC\u1EDA\u1EE0\u1EDE\u1EE2\u1ECC\u1ED8\u01EA\u01EC\u00D8\u01FE\u0186\u019F\uA74A\uA74C");
		defaultDiacriticsRemovalMap.put("OI","\u01A2");
		defaultDiacriticsRemovalMap.put("OO","\uA74E");
		defaultDiacriticsRemovalMap.put("OU","\u0222");
		defaultDiacriticsRemovalMap.put("P","\u0050\u24C5\uFF30\u1E54\u1E56\u01A4\u2C63\uA750\uA752\uA754");
		defaultDiacriticsRemovalMap.put("Q","\u0051\u24C6\uFF31\uA756\uA758\u024A");
		defaultDiacriticsRemovalMap.put("R","\u0052\u24C7\uFF32\u0154\u1E58\u0158\u0210\u0212\u1E5A\u1E5C\u0156\u1E5E\u024C\u2C64\uA75A\uA7A6\uA782");
		defaultDiacriticsRemovalMap.put("S","\u0053\u24C8\uFF33\u1E9E\u015A\u1E64\u015C\u1E60\u0160\u1E66\u1E62\u1E68\u0218\u015E\u2C7E\uA7A8\uA784");
		defaultDiacriticsRemovalMap.put("T","\u0054\u24C9\uFF34\u1E6A\u0164\u1E6C\u021A\u0162\u1E70\u1E6E\u0166\u01AC\u01AE\u023E\uA786");
		defaultDiacriticsRemovalMap.put("TZ","\uA728");
		defaultDiacriticsRemovalMap.put("U","\u0055\u24CA\uFF35\u00D9\u00DA\u00DB\u0168\u1E78\u016A\u1E7A\u016C\u00DC\u01DB\u01D7\u01D5\u01D9\u1EE6\u016E\u0170\u01D3\u0214\u0216\u01AF\u1EEA\u1EE8\u1EEE\u1EEC\u1EF0\u1EE4\u1E72\u0172\u1E76\u1E74\u0244");
		defaultDiacriticsRemovalMap.put("V","\u0056\u24CB\uFF36\u1E7C\u1E7E\u01B2\uA75E\u0245");
		defaultDiacriticsRemovalMap.put("VY","\uA760");
		defaultDiacriticsRemovalMap.put("W","\u0057\u24CC\uFF37\u1E80\u1E82\u0174\u1E86\u1E84\u1E88\u2C72");
		defaultDiacriticsRemovalMap.put("X","\u0058\u24CD\uFF38\u1E8A\u1E8C");
		defaultDiacriticsRemovalMap.put("Y","\u0059\u24CE\uFF39\u1EF2\u00DD\u0176\u1EF8\u0232\u1E8E\u0178\u1EF6\u1EF4\u01B3\u024E\u1EFE");
		defaultDiacriticsRemovalMap.put("Z","\u005A\u24CF\uFF3A\u0179\u1E90\u017B\u017D\u1E92\u1E94\u01B5\u0224\u2C7F\u2C6B\uA762");
		defaultDiacriticsRemovalMap.put("a","\u0061\u24D0\uFF41\u1E9A\u00E0\u00E1\u00E2\u1EA7\u1EA5\u1EAB\u1EA9\u00E3\u0101\u0103\u1EB1\u1EAF\u1EB5\u1EB3\u0227\u01E1\u00E4\u01DF\u1EA3\u00E5\u01FB\u01CE\u0201\u0203\u1EA1\u1EAD\u1EB7\u1E01\u0105\u2C65\u0250");
		defaultDiacriticsRemovalMap.put("aa","\uA733");
		defaultDiacriticsRemovalMap.put("ae","\u00E6\u01FD\u01E3");
		defaultDiacriticsRemovalMap.put("ao","\uA735");
		defaultDiacriticsRemovalMap.put("au","\uA737");
		defaultDiacriticsRemovalMap.put("av","\uA739\uA73B");
		defaultDiacriticsRemovalMap.put("ay","\uA73D");
		defaultDiacriticsRemovalMap.put("b","\u0062\u24D1\uFF42\u1E03\u1E05\u1E07\u0180\u0183\u0253");
		defaultDiacriticsRemovalMap.put("c","\u0063\u24D2\uFF43\u0107\u0109\u010B\u010D\u00E7\u1E09\u0188\u023C\uA73F\u2184");
		defaultDiacriticsRemovalMap.put("d","\u0064\u24D3\uFF44\u1E0B\u010F\u1E0D\u1E11\u1E13\u1E0F\u0111\u018C\u0256\u0257\uA77A");
		defaultDiacriticsRemovalMap.put("dz","\u01F3\u01C6");
		defaultDiacriticsRemovalMap.put("e","\u0065\u24D4\uFF45\u00E8\u00E9\u00EA\u1EC1\u1EBF\u1EC5\u1EC3\u1EBD\u0113\u1E15\u1E17\u0115\u0117\u00EB\u1EBB\u011B\u0205\u0207\u1EB9\u1EC7\u0229\u1E1D\u0119\u1E19\u1E1B\u0247\u025B\u01DD");
		defaultDiacriticsRemovalMap.put("f","\u0066\u24D5\uFF46\u1E1F\u0192\uA77C");
		defaultDiacriticsRemovalMap.put("g","\u0067\u24D6\uFF47\u01F5\u011D\u1E21\u011F\u0121\u01E7\u0123\u01E5\u0260\uA7A1\u1D79\uA77F");
		defaultDiacriticsRemovalMap.put("h","\u0068\u24D7\uFF48\u0125\u1E23\u1E27\u021F\u1E25\u1E29\u1E2B\u1E96\u0127\u2C68\u2C76\u0265");
		defaultDiacriticsRemovalMap.put("hv","\u0195");
		defaultDiacriticsRemovalMap.put("i","\u0069\u24D8\uFF49\u00EC\u00ED\u00EE\u0129\u012B\u012D\u00EF\u1E2F\u1EC9\u01D0\u0209\u020B\u1ECB\u012F\u1E2D\u0268\u0131");
		defaultDiacriticsRemovalMap.put("j","\u006A\u24D9\uFF4A\u0135\u01F0\u0249");
		defaultDiacriticsRemovalMap.put("k","\u006B\u24DA\uFF4B\u1E31\u01E9\u1E33\u0137\u1E35\u0199\u2C6A\uA741\uA743\uA745\uA7A3");
		defaultDiacriticsRemovalMap.put("l","\u006C\u24DB\uFF4C\u0140\u013A\u013E\u1E37\u1E39\u013C\u1E3D\u1E3B\u017F\u0142\u019A\u026B\u2C61\uA749\uA781\uA747");
		defaultDiacriticsRemovalMap.put("lj","\u01C9");
		defaultDiacriticsRemovalMap.put("m","\u006D\u24DC\uFF4D\u1E3F\u1E41\u1E43\u0271\u026F");
		defaultDiacriticsRemovalMap.put("n","\u006E\u24DD\uFF4E\u01F9\u0144\u00F1\u1E45\u0148\u1E47\u0146\u1E4B\u1E49\u019E\u0272\u0149\uA791\uA7A5");
		defaultDiacriticsRemovalMap.put("nj","\u01CC");
		defaultDiacriticsRemovalMap.put("o","\u006F\u24DE\uFF4F\u00F2\u00F3\u00F4\u1ED3\u1ED1\u1ED7\u1ED5\u00F5\u1E4D\u022D\u1E4F\u014D\u1E51\u1E53\u014F\u022F\u0231\u00F6\u022B\u1ECF\u0151\u01D2\u020D\u020F\u01A1\u1EDD\u1EDB\u1EE1\u1EDF\u1EE3\u1ECD\u1ED9\u01EB\u01ED\u00F8\u01FF\u0254\uA74B\uA74D\u0275");
		defaultDiacriticsRemovalMap.put("oi","\u01A3");
		defaultDiacriticsRemovalMap.put("ou","\u0223");
		defaultDiacriticsRemovalMap.put("oo","\uA74F");
		defaultDiacriticsRemovalMap.put("p","\u0070\u24DF\uFF50\u1E55\u1E57\u01A5\u1D7D\uA751\uA753\uA755");
		defaultDiacriticsRemovalMap.put("q","\u0071\u24E0\uFF51\u024B\uA757\uA759");
		defaultDiacriticsRemovalMap.put("r","\u0072\u24E1\uFF52\u0155\u1E59\u0159\u0211\u0213\u1E5B\u1E5D\u0157\u1E5F\u024D\u027D\uA75B\uA7A7\uA783");
		defaultDiacriticsRemovalMap.put("s","\u0073\u24E2\uFF53\u00DF\u015B\u1E65\u015D\u1E61\u0161\u1E67\u1E63\u1E69\u0219\u015F\u023F\uA7A9\uA785\u1E9B");
		defaultDiacriticsRemovalMap.put("t","\u0074\u24E3\uFF54\u1E6B\u1E97\u0165\u1E6D\u021B\u0163\u1E71\u1E6F\u0167\u01AD\u0288\u2C66\uA787");
		defaultDiacriticsRemovalMap.put("tz","\uA729");
		defaultDiacriticsRemovalMap.put("u","\u0075\u24E4\uFF55\u00F9\u00FA\u00FB\u0169\u1E79\u016B\u1E7B\u016D\u00FC\u01DC\u01D8\u01D6\u01DA\u1EE7\u016F\u0171\u01D4\u0215\u0217\u01B0\u1EEB\u1EE9\u1EEF\u1EED\u1EF1\u1EE5\u1E73\u0173\u1E77\u1E75\u0289");
		defaultDiacriticsRemovalMap.put("v","\u0076\u24E5\uFF56\u1E7D\u1E7F\u028B\uA75F\u028C");
		defaultDiacriticsRemovalMap.put("vy","\uA761");
		defaultDiacriticsRemovalMap.put("w","\u0077\u24E6\uFF57\u1E81\u1E83\u0175\u1E87\u1E85\u1E98\u1E89\u2C73");
		defaultDiacriticsRemovalMap.put("x","\u0078\u24E7\uFF58\u1E8B\u1E8D");
		defaultDiacriticsRemovalMap.put("y","\u0079\u24E8\uFF59\u1EF3\u00FD\u0177\u1EF9\u0233\u1E8F\u00FF\u1EF7\u1E99\u1EF5\u01B4\u024F\u1EFF");
		defaultDiacriticsRemovalMap.put("z","\u007A\u24E9\uFF5A\u017A\u1E91\u017C\u017E\u1E93\u1E95\u01B6\u0225\u0240\u2C6C\uA763");
			 
		res=string;
		for(String key: defaultDiacriticsRemovalMap.keySet()){
			//App.getInstance().log("Key:" + key);
			//App.getInstance().log("Chars:" + defaultDiacriticsRemovalMap.get(key));
			res= res.replaceAll("[" + defaultDiacriticsRemovalMap.get(key) + "]",key);
		}
		return res;

	}

	/**
	 * Converteix un String dd/mm/yyyy en una data estardar de JSON
	 * @param data
	 * @return
	 */
	public static String stringToJSONDate (String data){
		String ret = "";

		try {
			ret = data.substring(6,10) + "-" + data.substring(3,5) + "-" + data.substring(0,2) + "T00:00:00,000Z";
		} catch (Exception e) {
			ret = "0000-00-00T00:00:00,000Z";
		}

		App.getInstance().log("StringTOJSONDate: " + ret);

		return ret;
	}

	/**
	 * Converteix un String dd/mm/yyyy en una data estardar de JSON
	 * @param data
	 * @return
	 */
	public static String JSONDate2String (String data){
		String ret = "";

		try {
			ret = data.substring(6,10) + "-" + data.substring(3,5) + "-" + data.substring(0,2) + "T00:00:00,000Z";
		} catch (Exception e) {
			ret = "0000-00-00T00:00:00,000Z";
		}

		App.getInstance().log("StringTOJSONDate: " + ret);

		return ret;
	}





	/**
	 * Guarda els contactes a les SharedPreferences
	 */
	public static void guardarContactesSharedPreferences(ArrayList<Xat_Contacte> contactes, Context context){

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();

		Gson gson = new Gson();
		String jsonArrayContactes = gson.toJson(contactes);
		editor.putString("contactes", jsonArrayContactes);
		App.getInstance().log("Guardats Contactes de l'usuari = " + jsonArrayContactes);

		editor.commit();
	}

	/**
	 * Carrega a arrayContactes els contactes, guardats a SharedPreferences
	 */
	public static ArrayList<Xat_Contacte> carregarContactesSharedPreferences(Context context){

		ArrayList<Xat_Contacte> contactes = new ArrayList<>();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Matriu amb els missatges
		Gson gsonContactes = new Gson();
		String jsonContactes = prefs.getString("contactes", "");
		Type typeContactes = new TypeToken<ArrayList<Xat_Contacte>>() {}.getType();

		ArrayList<Xat_Contacte> arrayListContactes = gsonContactes.fromJson(jsonContactes, typeContactes);

		if (arrayListContactes != null) {
			contactes = arrayListContactes;
		} else {
			contactes = new ArrayList<>();
		}

		App.getInstance().log("Carregats Contactes de l'usuari = " + contactes);

		return contactes;

	}




	public static ArrayList<Xat_Contacte> afegirContacte(ArrayList<Xat_Contacte> contactes, Xat_Contacte contacte, Context context){
		carregarContactesSharedPreferences(context);
		boolean existeix = false;

		for (Xat_Contacte contacteLlista:contactes) {
			if (contacte.getUsuari().equals(contacteLlista.getUsuari())){
				existeix = true;
				if (contacte.getMissatgePendent().length() > 0){
					contacteLlista.setMissatgePendent("1");
				}
			}
		}

		if (!existeix){
			contactes.add(contacte);
		}

		guardarContactesSharedPreferences(contactes, context);

		return contactes;

	}


}
