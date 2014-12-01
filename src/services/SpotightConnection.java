package services;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Vector;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.json.*;


public class SpotightConnection {
	private final String SPOTLIGHT_URL = "http://spotlight.dbpedia.org/rest/annotate";
	private double confidence = 0.0;
	private int support = 0;
	
	public Vector<String> sendGETRequest(String text) throws Exception
	{
		Vector<String> returnVector = new Vector<String>();
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
				
				public void process(final HttpRequest request, final HttpContext context) {
					if(!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept", "application/json");
						request.addHeader("Accept", "text/xml");
					}
				}
			});
			
			HttpGet httpGet = new HttpGet(SPOTLIGHT_URL+"?text="+URLEncoder.encode(text)+"&confidence="+confidence+"&support="+support);
			
			System.out.println("Executing request : " + httpGet.getURI());
			// Execute HTTP request
			HttpResponse response = httpClient.execute(httpGet);
			
			System.out.println(response.getStatusLine());
			
			HttpEntity entity = response.getEntity();
			
			if (entity !=null && response.getStatusLine().getStatusCode() == 200 ) {
				String content = EntityUtils.toString(entity);
				System.out.println(content);
				
				//Serialize JSON
				JSONObject jsonObj = new JSONObject(content);
				
				//String textJSON = jsonObj.getString("@text");
				
				Vector<String> surfaceForms = getAllSurfaceForms(jsonObj.getJSONArray("Resources"));
				String[] terms = text.split(" ");
				
				returnVector =  termsNotSpotlight(terms, surfaceForms.toArray(new String[surfaceForms.size()]));
			}

		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return returnVector;
	}
	
	public Vector<String> getAllSurfaceForms(JSONArray ressources) throws JSONException
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
	
	public Vector<String> termsNotSpotlight(String[] allTerms, String[] termsSpotlighted)
	{
		Vector<String> termsNotSpotlighted = new Vector<String>();
		
		for(int i=0;i<allTerms.length;i++) {
			if(!Arrays.asList(termsSpotlighted).contains(allTerms[i])) {
				termsNotSpotlighted.add(allTerms[i]);
			}
		}
		return termsNotSpotlighted;
	}
}
