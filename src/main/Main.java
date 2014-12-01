package main;

import services.SpotightConnection;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		SpotightConnection spCon = new SpotightConnection();
		
		spCon.sendGETRequest("Jaguar car motor");
	}

}
