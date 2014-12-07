package main;

import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;

import model.GEXFStaticGraphExample;
import services.SpotightConnection;
import services.WordNetConnection;
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//Configure path to Wordnet DB => Absolutely needed
		System.setProperty("wordnet.database.dir", "/home/mael/Documents/WordNet-3.0/dict");
		
		//Example WordNet
		WordNetConnection wordnet = new WordNetConnection();
		//wordnet.printHyponyms("fly");
		wordnet.printHypernyms("fly");
		
		//Example DBPedia Spotlight
//		SpotightConnection spCon = new SpotightConnection();
//		Vector<String> termsNotSpotlighted = spCon.sendGETRequest("Renault motor Obama");
//		System.out.println(termsNotSpotlighted.toString());
		
		//Example GEXF file generation
//		GEXFStaticGraphExample graphExample = new GEXFStaticGraphExample();
//		graphExample.generateExample();
		
		
	}

}
