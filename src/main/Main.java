package main;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;


import model.GEXFStaticGraphExample;
import model.OnlineConcept;
import model.TypeTerm;
import services.SpotlightConnection;
import services.WordNetConnection;
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//Configure path to Wordnet DB => Absolutely needed
		Properties p = new Properties();
	    p.load(new FileInputStream("config.ini"));
		System.setProperty("wordnet.database.dir", p.getProperty("wordnetAbsolutePath")); //mettre les config dans fichier ext
		
//		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
//		Synset[] synsets = database.getSynsets("physical entity", SynsetType.NOUN); 
//		NounSynset noun = (NounSynset) synsets[0];
//		System.out.println(noun.getHypernyms().length);
		
		String textRequest = "tulip flower rose";
		Hashtable<String, OnlineConcept> forest = getAncestors(textRequest, false);
		
		generateGEXFFile(forest);
		
		
// V -  Export model to GEXF		
		//Example GEXF file generation
//		GEXFStaticGraphExample graphExample = new GEXFStaticGraphExample();
//		graphExample.generateExample();
				
	}
	
	private static void generateGEXFFile(Hashtable<String, OnlineConcept> forest) {
		// TODO Auto-generated method stub
		
	}

	private static Hashtable<String, OnlineConcept> getAncestors(String termsToLookFor, boolean all) throws Exception {
		Hashtable<String, OnlineConcept> allConcepts = new Hashtable<String, OnlineConcept>(); //Store all concepts, create only one entity per concept (URI - Object)
		Hashtable<String, OnlineConcept> tableTerms = new Hashtable<String, OnlineConcept>(); //Table used to store terms we are looking for and their ancestors (termString - Object)
		
// I - Detect with DBPedia Spotlight		
		//Example DBPedia Spotlight
		SpotlightConnection spCon = new SpotlightConnection();		
		JSONObject termsSpotlighted = spCon.sendGETRequest(termsToLookFor);
	

// II - Detect terms not found and request them in Wordnet
		String[] terms = termsToLookFor.split(" "); 
		Vector<String> termsNotSpotlighted= new Vector<String>(Arrays.asList(terms));
		if(termsSpotlighted.has("Resources")){
			Vector<String> surfaceForms =getAllSurfaceForms(termsSpotlighted.getJSONArray("Resources"));
			termsNotSpotlighted =  termsNotSpotlight(terms, surfaceForms.toArray(new String[surfaceForms.size()]));	
			System.out.println(termsNotSpotlighted);
		}
		
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
		if(termsSpotlighted.has("Resources")){
			JSONArray resources = termsSpotlighted.getJSONArray("Resources");
			for (int i=0; i< resources.length(); i++) {
				OnlineConcept currentConcept = new OnlineConcept(resources.getJSONObject(i));
				allConcepts.put(currentConcept.getUri(),currentConcept);
				tableTerms.put(currentConcept.getUri(),currentConcept);
			}
		}
		
		//Wordnet
		for (int j=0;j<allSynsets.size();j++) {
			NounSynset currentNoun = (NounSynset) allSynsets.get(j)[0]; //Le premier du paquet ??
			OnlineConcept currentConcept = new OnlineConcept(currentNoun); 
			allConcepts.put(currentConcept.getUri(),currentConcept);
			tableTerms.put(currentConcept.getUri(),currentConcept);
		}
		
		
		
// IV - Find parents/superclasses/hyperonyms and create entities/links between them
		Enumeration<OnlineConcept> e = tableTerms.elements();

	    while(e.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e.nextElement();
	    	recursionTerms(concept, allConcepts);
	    }

		return tableTerms;
	}
	
	private static void recursionTerms(OnlineConcept concept, Hashtable<String, OnlineConcept> allConcepts){
		if(!((concept.getUri()=="Wordnet:abstraction") || (concept.getUri()=="http://www.w3.org/2002/07/owl#Thing"))){
			if(concept.getParents().isEmpty()){
				if (concept.getType() == TypeTerm.DBPedia){
		    		//get superClasses
		    		
		    	}
		    	else if (concept.getType() == TypeTerm.Wordnet){
		    		//Get hypernyms
		    		NounSynset currentHyper;
		    		NounSynset[] hypers = concept.getSynset().getHypernyms();
		    		
		    		for (int i=0;i<hypers.length;i++) {
		    			currentHyper = hypers[i];
		    			OnlineConcept currentHyperConcept = new OnlineConcept(currentHyper);
		    			String uri = currentHyperConcept.getUri();
		    			if (allConcepts.containsKey(uri))
		    				currentHyperConcept = allConcepts.get(uri);
		    			else
		    				allConcepts.put(uri, currentHyperConcept);
		    				
		    			concept.getParents().add(currentHyperConcept);
		    			currentHyperConcept.getChilds().add(concept);
		    		}
		    	}
				for (int j=0;j<concept.getParents().size();j++)
					recursionTerms(concept.getParents().get(j), allConcepts);
			}

		}
		    	
	}

	private static Vector<String> getAllSurfaceForms(JSONArray ressources) throws JSONException
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
	
	private static Vector<String> termsNotSpotlight(String[] allTerms, String[] termsSpotlighted)
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
	
	private static void getSuperClass(){
		//Execute SPARQL query
		//select ?o where { <concept.getURI()> rdfs:subClassOf  ?o}
//		String sparqlQuery = "select ?o where { <"+ concept.getUri()+"> rdfs:subClassOf  ?o}";
//		System.out.println("Query : " + sparqlQuery);
//		Query query = QueryFactory.create(sparqlQuery);
//		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);	
//		ResultSet results = qexec.execSelect();
//		//ResultSetFormatter.out(System.out, results, query);
//		qexec.close();
	}
}
