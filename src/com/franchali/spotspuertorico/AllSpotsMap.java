package com.franchali.spotspuertorico;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;

public class AllSpotsMap extends SherlockFragmentActivity {

	private ProgressDialog progressDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_spots_map);
		

		getSupportActionBar().setSubtitle("Spots por el Area");
		getSupportActionBar().setTitle("Spots de PR"); 
		
		new ReadSpotJSONFeedTask().execute("http://api.franchali.com/Spotiao/api/Spots");
		
	}

	public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }
    
	private class ReadSpotJSONFeedTask extends AsyncTask
    <String, Void, String> {
        

		
		protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }
 
        protected void onPostExecute(String result) {
            try {
                
            	GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            	map.setMyLocationEnabled(true);
            	
            	//JSONObject jsonObject = new JSONObject(result)
                JSONArray spotItems = new JSONArray(result);
 
                //---print out the content of the json feed---
                for (int i = 0; i < spotItems.length(); i++) {
                    JSONObject spotDetail = spotItems.getJSONObject(i); 
                    
                    LatLng spotLatLng = new LatLng(Double.parseDouble(spotDetail.getString("MapLatitude")), Double.parseDouble(spotDetail.getString("MapLongitude")));
               	 
                	map.addMarker(new MarkerOptions().position(spotLatLng)
                	        .title(spotDetail.getString("Name")));
                	
                }
            		
            	    

            	// Move the camera instantly to  gps location
                LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria crit = new Criteria();
                Location loc = locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));

                if (loc != null)
                {
                    LatLng gpsLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(gpsLatLng, 15));
                    map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                }
                
                progressDialog.dismiss();
                
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }          
        }
        
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(AllSpotsMap.this, "Spots Puerto Rico", "Buscando los mejores Spots");
        }
        
    }

}
