package main;

import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.viz.NodeShape;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import model.DBpediaConcept;
import model.OnlineConcept;
import model.TypeTerm;
import model.WordNetConcept;
import services.SpotlightConnection;
import services.WordNetConnection;
public class Main {

	private final static String GENERATED_GEXF_FILE_PATH = "generatedFiles/";
	static Integer ids = 0;
	static Properties p;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//Configure path to Wordnet DB => Absolutely needed
		p = new Properties();
	    p.load(new FileInputStream("config.ini"));
		System.setProperty("wordnet.database.dir", p.getProperty("wordnetAbsolutePath")); //mettre les config dans fichier ext
		
		//String textRequest = "freshness outdoors differential focus spain green leaf day colour image no people photography";
		String textRequest=" Barack Obama jaguar";
		Hashtable<String, OnlineConcept> forest = getAncestors(textRequest, false," ");
		
		generateGEXFFile(forest);
				
	}
	
	private static void generateGEXFFile(Hashtable<String, OnlineConcept> forest) {
		Gexf gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();
		
		gexf.getMetadata()
			.setLastModified(date.getTime())
			.setCreator("Mael Ogier")
			.setDescription("Concepts ontology");
		gexf.setVisualization(true);

		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.DIRECTED).setMode(Mode.STATIC);
		
		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		graph.getAttributeLists().add(attrList);
		
		Attribute attUrl = attrList.createAttribute("0", AttributeType.STRING, "url");
		Attribute attStartingConcept = attrList.createAttribute("1", AttributeType.STRING, "startingConcept");
		
		Enumeration<OnlineConcept> e = forest.elements();
		Node currentNode;

	    while(e.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e.nextElement();
	    	currentNode = graph.createNode(concept.getId().toString());
	    	currentNode
	    		.setLabel(concept.getUri())
	    		.setSize(20)
	    		.getAttributeValues()
	    			.addValue(attUrl, concept.getUri())
	    			.addValue(attStartingConcept, concept.isStartingConcept().toString());
	    }
	    
	    Enumeration<OnlineConcept> e2 = forest.elements();
	    List<Node> allNodes = graph.getNodes();
	    Collections.sort(allNodes, new Comparator<Node>(){
	        public int compare(Node o1, Node o2){
	            if(o1.getId() == o2.getId())
	                return 0;
	            return Integer.parseInt(o1.getId()) < Integer.parseInt(o2.getId()) ? -1 : 1;
	        }
	   });
	    Integer idEdge=0;
	    while(e2.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e2.nextElement();
	    	if(concept!=null){
	    		Vector<OnlineConcept> parents = concept.getParents();
		    	currentNode = allNodes.get(concept.getId());
		    	for(int i=0; i<parents.size();i++) {
		    		Node parentNode = graph.getNodes().get(parents.get(i).getId());
		    		if(concept.getType()!=parents.get(i).getType()) //Equivalence DBpedia-Wordnet
		    			currentNode.connectTo(idEdge.toString(), "equiv", EdgeType.MUTUAL, parentNode);
		    		else
		    			currentNode.connectTo(idEdge.toString(), "parent", EdgeType.DIRECTED, parentNode);
		    		idEdge++;
		    	}
	    	}
	    }

		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File(GENERATED_GEXF_FILE_PATH+"obamaAndJaguar.gexf");
		Writer out;
		try {
			out =  new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println(f.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private static Hashtable<String, OnlineConcept> getAncestors(String termsToLookFor, boolean all, String separatorChar) throws Exception {
		Hashtable<String, OnlineConcept> allConcepts = new Hashtable<String, OnlineConcept>(); //Store all concepts, create only one entity per concept (URI - Object)
		Hashtable<String, OnlineConcept> tableTerms = new Hashtable<String, OnlineConcept>(); //Table used to store terms we are looking for and their ancestors (termString - Object)

// I - Detect with DBPedia Spotlight	
		SpotlightConnection spCon = new SpotlightConnection();		
		JSONObject termsSpotlighted = spCon.sendGETRequest(termsToLookFor);
	

// II - Detect terms not found and request them in Wordnet
		String[] terms = termsToLookFor.split(separatorChar); 
		Vector<String> termsNotSpotlighted= new Vector<String>(Arrays.asList(terms));
		if(termsSpotlighted.has("Resources")){
			Vector<String> surfaceForms =getAllSurfaceForms(termsSpotlighted.getJSONArray("Resources"));
			termsNotSpotlighted =  termsNotSpotlight(terms, surfaceForms.toArray(new String[surfaceForms.size()]));	
			System.out.println(termsNotSpotlighted);
		}
		
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
				OnlineConcept currentConcept = new DBpediaConcept(resources.getJSONObject(i), ids, true);
				if(allConcepts.get(currentConcept.getUri())==null)
				{
					allConcepts.put(currentConcept.getUri(),currentConcept);
					tableTerms.put(currentConcept.getUri(),currentConcept);
					ids++;
				}
			}
		}
		
		//Wordnet
		for (int j=0;j<allSynsets.size();j++) {
			Synset[] current = allSynsets.get(j);
			if (current.length !=0) {
				NounSynset currentNoun = (NounSynset) allSynsets.get(j)[0]; //Le premier du paquet ??
				OnlineConcept currentConcept = new WordNetConcept(currentNoun,ids, true); 
				if(allConcepts.get(currentConcept.getUri())==null)
				{
					allConcepts.put(currentConcept.getUri(),currentConcept);
					tableTerms.put(currentConcept.getUri(),currentConcept);
					ids++;
				}
			}
		}
		
		
		
// IV - Find parents/superclasses/hyperonyms and create entities/links between them
		Enumeration<OnlineConcept> e = tableTerms.elements();

	    while(e.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e.nextElement();
	    	recursionTerms(concept, allConcepts);
	    }
	    
// V - Find equivalences DBpedia/Wordnet
//	    Enumeration<OnlineConcept> eConcepts = allConcepts.elements();
//	    while(eConcepts.hasMoreElements()) {
//	    	
//	    	OnlineConcept concept = (OnlineConcept) eConcepts.nextElement();
//	    	System.out.println(concept.getId() + " " + concept.getUri());
//	    	if(concept.getType()==TypeTerm.DBPedia && !concept.isStartingConcept()) {
//	    		//get label, query WordNet and apply recursionTerms
//	    		Synset[] syns = wordnet.getNounSynsets(concept.getLabel());
//	    		if(syns.length!=0){
//	    			NounSynset currentNoun = (NounSynset) syns[0]; //Le premier du paquet ??
//					OnlineConcept currentConcept = new WordNetConcept(currentNoun,ids, true); 
//					if(allConcepts.get(currentConcept.getUri())==null)
//					{
//						allConcepts.put(currentConcept.getUri(),currentConcept);
//						concept.getParents().add(currentConcept);
//						currentConcept.getChilds().add(concept);
//						ids++;
//					}
//					recursionTerms(currentConcept, allConcepts);
//	    		
//	    		}
//	    		
//	    	}
//	   
//	    }
	    
		return allConcepts;
	}
	
	private static void recursionTerms(OnlineConcept concept, Hashtable<String, OnlineConcept> allConcepts) throws Exception{
		if(concept.getParents().isEmpty()){
			if (concept.getType() == TypeTerm.DBPedia){
	    		//get superClasses
				//If entity, we need to get the classes
				String sparqlClassQuery = "PREFIX rdf:" + p.getProperty("rdf") +
						"PREFIX rdfs:" +p.getProperty("rdfs") +
						" select ?o1 ?o2 where {GRAPH <http://dbpedia.org> {<"+concept.getUri()+"> rdf:type ?o1 " +
						"FILTER regex(?o1, '^^http://dbpedia.org/ontology') " +		
						"OPTIONAL{ ?o1 rdfs:label ?o2 " +						
						"FILTER(langMatches(lang(?o2), 'EN'))}}}"; 
				System.out.println(sparqlClassQuery);
				Query queryClass = QueryFactory.create(sparqlClassQuery);
				QueryExecution qexecClass = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryClass);	
				ResultSet resultsClass = qexecClass.execSelect();
				for ( ; resultsClass.hasNext() ; ) {
					QuerySolution soln = resultsClass.nextSolution();
					Resource r = soln.getResource("o1"); // Get a result variable - must be a resource
					Literal l = soln.getLiteral("o2");
					String uri = r.getURI();
					String label = l.getString();
					System.out.println(uri);
					OnlineConcept currentSuperClassConcept = new DBpediaConcept(uri,label,ids, false);
					if (allConcepts.containsKey(uri))
						currentSuperClassConcept = allConcepts.get(uri);
	    			else {
	    				allConcepts.put(uri, currentSuperClassConcept);
	    				ids++;
	    			}
	    				
					
					concept.getParents().add(currentSuperClassConcept);
					currentSuperClassConcept.getChilds().add(concept);
			    }
				qexecClass.close();
				
				//If class, get superclasses
				String sparqlQuery = "PREFIX rdfs:"+p.getProperty("rdfs") +
						" select distinct ?o ?o2 WHERE{GRAPH <http://dbpedia.org> { <"+concept.getUri()+"> rdfs:subClassOf  ?o " +
						"FILTER regex(?o, '^^http://dbpedia.org/ontology || ^^http://www.w3.org/2002/07/owl#Thing') " +
						"OPTIONAL{ ?o rdfs:label ?o2 "+
						"FILTER(langMatches(lang(?o2), 'EN'))}}}";
				System.out.println(sparqlQuery);
				Query query = QueryFactory.create(sparqlQuery);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);	
				ResultSet results = qexec.execSelect();
				for ( ; results.hasNext() ; ) {
					QuerySolution soln = results.nextSolution();
					Resource r = soln.getResource("o"); // Get a result variable - must be a resource
					Literal l = soln.getLiteral("o2");
					String uri = r.getURI();
					String label="";
					if (l!=null)
						label = l.getString();
					System.out.println(uri + " " + label);
					OnlineConcept currentSuperClassConcept = new DBpediaConcept(uri,label,ids, false);
					if (allConcepts.containsKey(uri))
						currentSuperClassConcept = allConcepts.get(uri);
	    			else {
	    				allConcepts.put(uri, currentSuperClassConcept);
	    				ids++;
	    			}
	    				
					
					concept.getParents().add(currentSuperClassConcept);
					currentSuperClassConcept.getChilds().add(concept);
			    }
				qexec.close();
	    	}
	    	else if (concept.getType() == TypeTerm.Wordnet){
	    		//Get hypernyms
	    		NounSynset currentHyper;
	    		NounSynset[] hypers = ((WordNetConcept) concept).getSynset().getHypernyms();
	    		
	    		for (int i=0;i<hypers.length;i++) {
	    			currentHyper = hypers[i];
	    			OnlineConcept currentHyperConcept = new WordNetConcept(currentHyper,ids, false);
	    			String uri = currentHyperConcept.getUri();
	    			if (allConcepts.containsKey(uri))
	    				currentHyperConcept = allConcepts.get(uri);
	    			else {
	    				allConcepts.put(uri, currentHyperConcept);
	    				ids++;
	    			}
	    				
	    				
	    			concept.getParents().add(currentHyperConcept);
	    			currentHyperConcept.getChilds().add(concept);
	    		}
	    	}
			for (int j=0;j<concept.getParents().size();j++)
				recursionTerms(concept.getParents().get(j), allConcepts);
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
}
