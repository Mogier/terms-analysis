package model;

import java.util.Vector;

public class OnlineConcept {
	protected String uri;
	protected TypeTerm type;
	protected Integer id;
	protected Integer depth;
	protected Integer startingConcept; //0 : starting | 1 : created | 2 : created through equivalences
	protected String label;
	protected Vector<OnlineConcept> parents;//Superclasses or hypernyms
	protected Vector<OnlineConcept> childs; //Subclasses or hyponyms

	public OnlineConcept(String localUri, TypeTerm localType, Integer localId, Integer localDepth, Integer localStartingConcept, String localLabel) {
		uri = localUri;
		type = localType;
		id = localId;
		depth = localDepth;
		startingConcept = localStartingConcept;
		label = localLabel;
		parents = new Vector<OnlineConcept>();
		childs = new Vector<OnlineConcept>();	
		

		System.out.println("Creating Base Concept "+id+" " + label + " : " + uri);
	}
	
	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getStartingConcept() {
		return startingConcept;
	}

	public void setStartingConcept(Integer startingConcept) {
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
