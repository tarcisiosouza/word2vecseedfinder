package de.l3s.souza.search;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import com.google.gson.Gson;

public class GoogleSearch {
	
 public static void main (String[]args) throws IOException {	
		String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		String query = "programcreek";
		String charset = "UTF-8";
 
		URL url = new URL(address + URLEncoder.encode(query, charset));
		Reader reader = new InputStreamReader(url.openStream(), charset);
		GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
 
		int total = results.getResponseData().getResults().size();
		System.out.println("total: "+total);
 
		// Show title and URL of each results
		for(int i=0; i<=total-1; i++){
			System.out.println("Title: " + results.getResponseData().getResults().get(i).toString());
			System.out.println("URL: " + results.getResponseData().getResults().get(i).getUrl() + "\n");
 
		}
 }
}

