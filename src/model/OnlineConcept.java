package model;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

public class OnlineConcept {
	protected String uri; //String or URI object ? Problem with Wordnet, no real URI (build one)
	protected TypeTerm type;
	
	protected Vector<OnlineConcept> parents = new Vector<OnlineConcept>(); //Superclasses or hypernyms
	
	protected Vector<OnlineConcept> childs = new Vector<OnlineConcept>(); //Subclasses or hyponyms
	
	public OnlineConcept() {
		type = null;
	}
	
	public OnlineConcept(JSONObject dbpediaJSON) throws JSONException {
		uri = dbpediaJSON.getString("@URI");
		type = TypeTerm.DBPedia;
	}
	
	public OnlineConcept(String wordnetDescription) {
		uri = "Wordnet:"+wordnetDescription;
		type = TypeTerm.Wordnet;
	}
}
