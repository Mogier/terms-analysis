package model;

import org.json.JSONException;
import org.json.JSONObject;

public class DBpediaConcept extends OnlineConcept {

	public DBpediaConcept(JSONObject dbpediaJSON, int idConcept, boolean base) throws JSONException {
		super(dbpediaJSON.getString("@URI"),TypeTerm.DBPedia,idConcept, base, dbpediaJSON.getString("@surfaceForm") );
		System.out.println("Creating DBPedia Concept "+id+" " + label + " : " + uri);
	}
	
	public DBpediaConcept(String URI, String labelResource,int idConcept, boolean base) throws JSONException {
		super(URI,TypeTerm.DBPedia,idConcept, base, labelResource );
		System.out.println("Creating DBPedia Concept "+id+" " + label + " : " + uri);
	}
}
