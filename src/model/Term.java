package model;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

public class Term {
	protected String uri; //String or URI object ? Problem with Wordnet, no real URI (build one)
	
	protected Vector<Term> parents = new Vector<Term>(); //Superclasses or hypernyms
	
	protected Vector<Term> childs = new Vector<Term>(); //Subclasses or hyponyms
	
	public Term() {
		
	}
	
	public Term(JSONObject dbpediaJSON) throws JSONException {
		uri = dbpediaJSON.getString("@URI");
	}
	
	public Term(String wordnetDescription) {
		
	}
}
