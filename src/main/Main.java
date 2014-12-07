package main;

import java.util.Vector;

import services.SpotightConnection;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		SpotightConnection spCon = new SpotightConnection();
		
		Vector<String> termsNotSpotlighted = spCon.sendGETRequest("motor");
		System.out.println(termsNotSpotlighted.toString());
	}

}
