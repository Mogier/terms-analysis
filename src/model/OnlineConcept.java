package model;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import edu.smu.tspell.wordnet.NounSynset;

public class OnlineConcept {
	protected String uri; //String or URI object ? Problem with Wordnet, no real URI (build one)
	protected TypeTerm type;
	protected NounSynset synset = null; //Store synset if type = WordNet
	
	protected Vector<OnlineConcept> parents = new Vector<OnlineConcept>(); //Superclasses or hypernyms
	
	protected Vector<OnlineConcept> childs = new Vector<OnlineConcept>(); //Subclasses or hyponyms
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public TypeTerm getType() {
		return type;
	}

	public void setType(TypeTerm type) {
		this.type = type;
	}

	public Vector<OnlineConcept> getParents() {
		return parents;
	}

	public void setParents(Vector<OnlineConcept> parents) {
		this.parents = parents;
	}

	public Vector<OnlineConcept> getChilds() {
		return childs;
	}

	public void setChilds(Vector<OnlineConcept> childs) {
		this.childs = childs;
	}
	

	public NounSynset getSynset() {
		return synset;
	}

	public void setSynset(NounSynset synset) {
		this.synset = synset;
	}

	public OnlineConcept() {
		type = null;
	}
	
	public OnlineConcept(JSONObject dbpediaJSON) throws JSONException {
		uri = dbpediaJSON.getString("@URI");
		type = TypeTerm.DBPedia;
		System.out.println("Creating DBPedia Concept : " + uri);
	}
	
	public OnlineConcept(NounSynset wordnetDescription) {
		uri = "Wordnet:"+wordnetDescription.getWordForms()[0];
		type = TypeTerm.Wordnet;
		synset = wordnetDescription;
		System.out.println("Creating Wordnet Concept : " + uri);
	}	
}
