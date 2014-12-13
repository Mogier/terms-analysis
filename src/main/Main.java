package main;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;


import model.GEXFStaticGraphExample;
import model.OnlineConcept;
import services.SpotlightConnection;
import services.WordNetConnection;
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Vector<OnlineConcept> allTerms = new Vector<OnlineConcept>();
		Properties p = new Properties();
	    p.load(new FileInputStream("config.ini"));
		
		//Configure path to Wordnet DB => Absolutely needed
		System.setProperty("wordnet.database.dir", p.getProperty("wordnetAbsolutePath")); //mettre les config dans fichier ext
		String textRequest = "jaguar car";

// I - Detect with DBPedia Spotlight		
		//Example DBPedia Spotlight
		SpotlightConnection spCon = new SpotlightConnection();		
		JSONObject termsSpotlighted = spCon.sendGETRequest(textRequest);
	

// II - Detect terms not found and request them in Wordnet
		Vector<String> surfaceForms =getAllSurfaceForms(termsSpotlighted.getJSONArray("Resources"));
		String[] terms = textRequest.split(" ");
		Vector<String> termsNotSpotlighted =  termsNotSpotlight(terms, surfaceForms.toArray(new String[surfaceForms.size()]));
 
		//Example WordNet
		WordNetConnection wordnet = new WordNetConnection();
		Vector<Synset[]> allSynsets = new Vector<Synset[]>();
		
		for(int i=0; i<termsNotSpotlighted.size();i++) {
			Synset[] currentSynsets = wordnet.getNounSynsets(termsNotSpotlighted.get(i));
			allSynsets.add(currentSynsets);
		}
		
// III - Create "base" terms
// From DBPedia : use URI provided
// From Wordnet : "Wordnet:" + nounSynset.getWordForms()[0]
		
		//DBPedia
		JSONArray resources = termsSpotlighted.getJSONArray("Resources");
		for (int i=0; i< resources.length(); i++) {
			OnlineConcept currentTerm = new OnlineConcept(resources.getJSONObject(i));
			allTerms.add(currentTerm);
		}
		
		//Wordnet
		for (int j=0;j<allSynsets.size();j++) {
			NounSynset currentNoun = (NounSynset) allSynsets.get(j)[0]; //Le premier du paquet ??
			allTerms.add(new OnlineConcept(currentNoun.getWordForms()[0]));
		}
		
		
// IV - Find parents/superclasses/hyperonyms and create entities/links between them
		
		
// V -  Export model to GEXF		
		//Example GEXF file generation
//		GEXFStaticGraphExample graphExample = new GEXFStaticGraphExample();
//		graphExample.generateExample();
				
	}

	public static Vector<String> getAllSurfaceForms(JSONArray ressources) throws JSONException
	{
		Vector<String> surfaceForms = new Vector<String>();
		
		for(int i=0; i<ressources.length();i++) {
			JSONObject row = ressources.getJSONObject(i);
			
			if (row != null) {
				String currentSurfaceForm = row.getString("@surfaceForm");
				surfaceForms.add(currentSurfaceForm);
			}
		}
		
		return surfaceForms;	
	}
	
	public static Vector<String> termsNotSpotlight(String[] allTerms, String[] termsSpotlighted)
	{
		Vector<String> termsNotSpotlighted = new Vector<String>();
		
		for(int i=0;i<allTerms.length;i++) {
			if(!Arrays.asList(termsSpotlighted).contains(allTerms[i])) {
				termsNotSpotlighted.add(allTerms[i]);
			}
		}
		System.out.println("Terms not spotlighted :");
		System.out.println(termsNotSpotlighted.toString());
		return termsNotSpotlighted;
	}
}
