package main;

import java.util.Vector;

import services.SpotightConnection;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		SpotightConnection spCon = new SpotightConnection();
		
		Vector<String> termsNotSpotlighted = spCon.sendGETRequest("Jaguar yolo car motor");
		System.out.println(termsNotSpotlighted.toString());
	}

}
