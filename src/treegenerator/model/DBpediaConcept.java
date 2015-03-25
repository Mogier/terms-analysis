package treegenerator.model;

import org.json.JSONException;
import org.json.JSONObject;

public class DBpediaConcept extends OnlineConcept {

	public DBpediaConcept(JSONObject dbpediaJSON, int idConcept, Integer base, Integer depth) throws JSONException {
		super(dbpediaJSON.getString("@URI"),TypeTerm.DBPedia,idConcept, depth, base, dbpediaJSON.getString("@surfaceForm") );
		System.out.println("Creating DBPedia Concept "+id+" " + label + " : " + uri);
	}
	
	public DBpediaConcept(String URI, String labelResource,int idConcept, Integer base, Integer depth) throws JSONException {
		super(URI,TypeTerm.DBPedia,idConcept, depth, base, labelResource );
		System.out.println("Creating DBPedia Concept "+id+" " + label + " : " + uri);
	}
}
