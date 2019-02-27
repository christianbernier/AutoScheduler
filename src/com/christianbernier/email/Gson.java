package com.christianbernier.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Gson {

    public static void main(String[] args) throws JSONException {
    }

    public static HashMap<String, String> fetchPeopleForDate(String url, String body, String date) throws JSONException {
    	HashMap<String, String> toReturn = new HashMap<String, String>();
    	
    	
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            json = "{ data:[" + json.substring(1, json.length() - 2) + "]}";

            final JSONObject obj = new JSONObject(json);
            final JSONArray geodata = obj.getJSONArray("data");
            final int n = geodata.length();
            for (int i = 0; i < n; ++i) {
              final JSONObject person = geodata.getJSONObject(i);
              if(person.get("name") instanceof String) {
	              String name = person.getString("name");
	              if(!name.equals("Scheduled") && !name.equals("Sent By")) {
	            	  Iterator<String> keys = person.keys();
	            	  
	            	  while(keys.hasNext()) {
	            		  String key = keys.next();
	            		  if(key.equals(date)) {
	            			  if(!person.get(key).equals("NS"))
	            			  toReturn.put(person.get(key).toString(), name);
	            		  }
	            	  }
	              }
              }
            }
        } catch (IOException ex) {
        }
        
        if(toReturn == null || toReturn.isEmpty()) {
        	throw new Error("Cannot find date on spreadsheet.");
        }
        return toReturn;
    }
}