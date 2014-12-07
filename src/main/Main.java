package main;

import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TestGenerator;

import model.GEXFStaticGraphExample;
import services.SpotightConnection;
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//Example DBPedia Spotlight
		SpotightConnection spCon = new SpotightConnection();
		Vector<String> termsNotSpotlighted = spCon.sendGETRequest("Renault motor");
		System.out.println(termsNotSpotlighted.toString());
		
		//Example GEXF file generation
		GEXFStaticGraphExample graphExample = new GEXFStaticGraphExample();
		graphExample.generateExample();
		
		
	}

}
