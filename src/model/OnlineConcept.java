package model;

import java.util.Vector;

public class OnlineConcept {
	protected String uri; //String or URI object ? Problem with Wordnet, no real URI (build one)
	protected TypeTerm type;
	protected Integer id;
	protected Boolean startingConcept;
	protected String label;
	protected Vector<OnlineConcept> parents;//Superclasses or hypernyms
	protected Vector<OnlineConcept> childs; //Subclasses or hyponyms

	public OnlineConcept(String localUri, TypeTerm localType, Integer localId, Boolean localStartingConcept, String localLabel) {
		uri = localUri;
		type = localType;
		id = localId;
		startingConcept = localStartingConcept;
		label = localLabel;
		parents = new Vector<OnlineConcept>();
		childs = new Vector<OnlineConcept>();		
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean isStartingConcept() {
		return startingConcept;
	}

	public void setStartingConcept(boolean startingConcept) {
		this.startingConcept = startingConcept;
	}
	
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
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
		
}
