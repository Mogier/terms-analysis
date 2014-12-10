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


public class SpotlightConnection {
	private final String SPOTLIGHT_URL = "http://spotlight.dbpedia.org/rest/annotate";
	private double confidence = 0.0;
	private int support = 0;
	
	public JSONObject sendGETRequest(String text) throws Exception
	{
		JSONObject returnJSON = new JSONObject();
		
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
				returnJSON = new JSONObject(content);
			}

		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return returnJSON;
	}
	
}
