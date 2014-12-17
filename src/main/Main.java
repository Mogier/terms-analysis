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
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import model.OnlineConcept;
import model.TypeTerm;
import services.SpotlightConnection;
import services.WordNetConnection;
public class Main {

	private final static String GENERATED_GEXF_FILE_PATH = "generatedFiles/";
	static Integer ids = 0;
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
		
		String textRequest = "jaguar car";
		Hashtable<String, OnlineConcept> forest = getAncestors(textRequest, false);
		
		generateGEXFFile(forest);
		
		
// V -  Export model to GEXF		
		//Example GEXF file generation
//		GEXFStaticGraphExample graphExample = new GEXFStaticGraphExample();
//		graphExample.generateExample();
				
	}
	
	private static void generateGEXFFile(Hashtable<String, OnlineConcept> forest) {
		Gexf gexf = new GexfImpl();
		Calendar date = Calendar.getInstance();
		
		gexf.getMetadata()
			.setLastModified(date.getTime())
			.setCreator("Gephi.org")
			.setDescription("A Web network");
		gexf.setVisualization(true);

		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);
		
		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		graph.getAttributeLists().add(attrList);
		
		Attribute attUrl = attrList.createAttribute("0", AttributeType.STRING, "url");
		
		Enumeration<OnlineConcept> e = forest.elements();
		Node currentNode;

	    while(e.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e.nextElement();
	    	currentNode = graph.createNode(concept.getId().toString());
	    	currentNode
	    		.setLabel(concept.getId().toString())
	    		.setSize(20)
	    		.getAttributeValues()
	    			.addValue(attUrl, concept.getUri());
	    }
	    
	    Enumeration<OnlineConcept> e2 = forest.elements();
	    while(e2.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e2.nextElement();
	    	Vector<OnlineConcept> parents = concept.getParents();
	    	currentNode = graph.getNodes().get(concept.getId());
	    	System.out.println("child " + concept.getId());
	    	for(int i=0; i<parents.size();i++) {
	    		System.err.println("parent "+parents.get(i).getId());
	    		Node parentNode = graph.getNodes().get(parents.get(i).getId());
	    		currentNode.connectTo(parentNode);
	    	}
	    }
		
//		gephi.connectTo("0", webatlas);
//		gephi.connectTo("1", rtgi);
//		webatlas.connectTo("2", gephi);
//		rtgi.connectTo("3", webatlas);
//		gephi.connectTo("4", blab);

		StaxGraphWriter graphWriter = new StaxGraphWriter();
		File f = new File(GENERATED_GEXF_FILE_PATH+"static_graph_sample.gexf");
		Writer out;
		try {
			out =  new FileWriter(f, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
			System.out.println(f.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
				OnlineConcept currentConcept = new OnlineConcept(resources.getJSONObject(i), ids++);
				allConcepts.put(currentConcept.getUri(),currentConcept);
				tableTerms.put(currentConcept.getUri(),currentConcept);
			}
		}
		
		//Wordnet
		for (int j=0;j<allSynsets.size();j++) {
			NounSynset currentNoun = (NounSynset) allSynsets.get(j)[0]; //Le premier du paquet ??
			OnlineConcept currentConcept = new OnlineConcept(currentNoun,ids++); 
			allConcepts.put(currentConcept.getUri(),currentConcept);
			tableTerms.put(currentConcept.getUri(),currentConcept);
		}
		
		
		
// IV - Find parents/superclasses/hyperonyms and create entities/links between them
		Enumeration<OnlineConcept> e = tableTerms.elements();

	    while(e.hasMoreElements()) {
	    	OnlineConcept concept = (OnlineConcept) e.nextElement();
	    	recursionTerms(concept, allConcepts);
	    }

		return allConcepts;
	}
	
	private static void recursionTerms(OnlineConcept concept, Hashtable<String, OnlineConcept> allConcepts) throws Exception{
		if(concept.getParents().isEmpty()){
			if (concept.getType() == TypeTerm.DBPedia){
	    		//get superClasses
				//If entity, we need to get the classes
				String sparqlClassQuery = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						"select ?o1 where {GRAPH <http://dbpedia.org> {<"+concept.getUri()+"> rdf:type  ?o1 " +
						"FILTER regex(?o1, '^^http://dbpedia.org/ontology')}}"; 
				System.out.println(sparqlClassQuery);
				Query queryClass = QueryFactory.create(sparqlClassQuery);
				QueryExecution qexecClass = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryClass);	
				ResultSet resultsClass = qexecClass.execSelect();
				for ( ; resultsClass.hasNext() ; ) {
					QuerySolution soln = resultsClass.nextSolution();
					Resource r = soln.getResource("o1"); // Get a result variable - must be a resource
					String uri = r.getURI();
					System.out.println(uri);
					OnlineConcept currentSuperClassConcept = new OnlineConcept(uri,ids);
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
				String sparqlQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"select distinct ?o WHERE{GRAPH <http://dbpedia.org> { <"+concept.getUri()+"> rdfs:subClassOf  ?o " +
						"FILTER regex(?o, '^^http://dbpedia.org/ontology || ^^http://www.w3.org/2002/07/owl#Thing')}}";
				System.out.println(sparqlQuery);
				Query query = QueryFactory.create(sparqlQuery);
				QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);	
				ResultSet results = qexec.execSelect();
				for ( ; results.hasNext() ; ) {
					QuerySolution soln = results.nextSolution();
					Resource r = soln.getResource("o"); // Get a result variable - must be a resource
					String uri = r.getURI();
					System.out.println(uri);
					OnlineConcept currentSuperClassConcept = new OnlineConcept(uri,ids);
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
	    		NounSynset[] hypers = concept.getSynset().getHypernyms();
	    		
	    		for (int i=0;i<hypers.length;i++) {
	    			currentHyper = hypers[i];
	    			OnlineConcept currentHyperConcept = new OnlineConcept(currentHyper,ids);
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
