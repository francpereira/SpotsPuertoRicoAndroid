package com.franchali.spotspuertorico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class SpotDetail extends SherlockFragmentActivity {

	private String spotId;
	private ProgressDialog progressDialog;
	private Bitmap spotImage = null;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spot_detail);
		
		getSupportActionBar().setSubtitle("Spot");
		getSupportActionBar().setTitle("Spots de PR"); 
		
		Intent i = getIntent();
		spotId = (String)i.getSerializableExtra("SpotId");
		
		new ReadSpotJSONFeedTask().execute("http://api.franchali.com/Spotiao/api/Spots/" + spotId);
		
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
            
			String urlImages = "http://api.franchali.com/Images/@spotId.jpg";
			urlImages = urlImages.replace("@spotId", spotId);

			URL newurl = null;
			
			try {
				newurl = new URL(urlImages);

				spotImage = BitmapFactory.decodeStream(newurl
						.openConnection().getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return readJSONFeed(urls[0]);
        }
 
        protected void onPostExecute(String result) {
            try {
                
            	JSONObject spotDetail = new JSONObject(result);
 
            	TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
            	TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
                ImageView spotImageCtl = (ImageView) findViewById(R.id.spotImage);
                
                
            	nameTextView.setText(spotDetail.getString("Name"));
            	descriptionTextView.setText(spotDetail.getString("Description"));
            	spotImageCtl.setImageBitmap(spotImage);
            	
            	GoogleMap map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            	    
            	LatLng spotLatLng = new LatLng(Double.parseDouble(spotDetail.getString("MapLatitude")), Double.parseDouble(spotDetail.getString("MapLongitude")));
            	 
            	Marker spotMarker = map.addMarker(new MarkerOptions().position(spotLatLng)
            	        .title(spotDetail.getString("Name")));
            	       // .visible(true)
            	       // .snippet(spotDetail.getString("Description")));
            	
            	// Move the camera instantly to  with a zoom of 15.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(spotLatLng, 15));

                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            	
                
                progressDialog.dismiss();
                
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }          
        }
        
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(SpotDetail.this, "Spots Puerto Rico", "Buscando los mejores Spots");
        }
        
    }
}
