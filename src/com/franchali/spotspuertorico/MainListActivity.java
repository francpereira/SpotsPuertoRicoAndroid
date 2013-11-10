package com.franchali.spotspuertorico;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainListActivity extends SherlockActivity {

	private ArrayList<String> activities;
	private ArrayList<String> activitiesIds;
	private ProgressDialog progressDialog;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        
        activities = new ArrayList<String>();
        activitiesIds = new ArrayList<String>();
        
        getSupportActionBar().setSubtitle("Actividades");
		getSupportActionBar().setTitle("Spots de PR"); 
        
        //http://api.franchali.com/Spotiao/api/Activity
        new ReadActivitiesJSONFeedTask().execute("http://api.franchali.com/Spotiao/api/Activity");
    
        
        populateList();
        
        final ListView listview = (ListView) findViewById(R.id.listViewActivities);
        listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(arg1.getContext(), SpotsListActivity.class);
				intent.putExtra("ActivityId", activitiesIds.get(arg2));
				startActivity(intent);
				
			}
        });
        
    }

    public boolean showAllSpots(MenuItem item) { 
        // actions
    	//Toast.makeText(this, "Show all Spots", Toast.LENGTH_SHORT).show(); 
    	Intent intent = new Intent(this, AllSpotsMap.class);
		startActivity(intent);
    	
    	return true;
    }
    
    public boolean sendSpotMessage(MenuItem item) { 
        
    	//Intent intent = new Intent(this, NewSpotSend.class);
		//startActivity(intent);
    	
    	Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"franciscochali@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Spots de PR | Nueva Sugerencia");
		//i.putExtra(Intent.EXTRA_TEXT   , "body of email");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		try {
		    startActivity(Intent.createChooser(i, "Enviando Sugerencia..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainListActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
    	
    	return true;
    }

    
	private void populateList() {
		final ListView listview = (ListView) findViewById(R.id.listViewActivities);

        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activities);
       
        listview.setAdapter(a);
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main_list, menu);
        
        
        return true;
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
    
    
    
    private class ReadActivitiesJSONFeedTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }
 
        protected void onPostExecute(String result) {
            try {
                //JSONObject jsonObject = new JSONObject(result)
                JSONArray activitiesItems = new JSONArray(result);
 
                //---print out the content of the json feed---
                for (int i = 0; i < activitiesItems.length(); i++) {
                    JSONObject activityItem = 
                    		activitiesItems.getJSONObject(i);        
                    
                    activities.add(activityItem.getString("Name"));
                    activitiesIds.add(activityItem.getString("Id"));
                    //Toast.makeText(getBaseContext(), 
                    //       postalCodesItem.getString("Name") + " - " + 
                    //    postalCodesItem.getString("Description"), 
                    //    Toast.LENGTH_SHORT).show();                                    
                }
                
                populateList();
                
                progressDialog.dismiss();
                
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }          
        }
        
        @Override
        protected void onPreExecute() {
        	progressDialog = ProgressDialog.show(MainListActivity.this, "Spots Puerto Rico", "Buscando los mejores Spots");
        }
        
    }
    
}
