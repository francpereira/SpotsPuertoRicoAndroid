package com.franchali.spotspuertorico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SpotsListActivity extends SherlockActivity {

	private String activityId;
	private ArrayList<String> spots;
	private ArrayList<String> spotsIds;
	private ArrayList<SpotListItem> spotObjects;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spots_list);

		getSupportActionBar().setSubtitle("Lugares");
		getSupportActionBar().setTitle("Spots de PR");

		Intent i = getIntent();
		activityId = (String) i.getSerializableExtra("ActivityId");

		spots = new ArrayList<String>();
		spotsIds = new ArrayList<String>();
		spotObjects = new ArrayList<SpotListItem>();

		new ReadSpotsJSONFeedTask()
				.execute("http://api.franchali.com/Spotiao/api/Spots?actividad="
						+ activityId + "&top=100");

		final ListView listview = (ListView) findViewById(R.id.spotsListView);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent(arg1.getContext(), SpotDetail.class);
				intent.putExtra("SpotId", spotsIds.get(arg2));
				startActivity(intent);

			}
		});
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

	private class ReadSpotsJSONFeedTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {

			String jsonResult = readJSONFeed(urls[0]);

			try {

				JSONArray spotsItems = new JSONArray(jsonResult);

				for (int i = 0; i < spotsItems.length(); i++) {

					JSONObject activityItem = spotsItems.getJSONObject(i);

					String spotName = activityItem.getString("Name");
					String spotDescription = activityItem.getString("Description");
					String spotId = activityItem.getString("Id");
					spots.add(spotName);
					spotsIds.add(spotId);
					String urlImages = "http://api.franchali.com/Images/@spotId.jpg";
					urlImages = urlImages.replace("@spotId", spotId);

					URL newurl = null;
					Bitmap icon_val = null;

					try {
						newurl = new URL(urlImages);

						icon_val = BitmapFactory.decodeStream(newurl
								.openConnection().getInputStream());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					spotObjects.add(new SpotListItem(icon_val, spotName, spotDescription));
				}

			} catch (Exception e) {
				Log.d("doInBackground", e.getLocalizedMessage());
			}
			return jsonResult;

		}

		protected void onPostExecute(String result) {
			try {

				populateList();

				progressDialog.dismiss();

			} catch (Exception e) {
				Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SpotsListActivity.this,
					"Spots Puerto Rico", "Buscando los mejores Spots");
		}

	}

	private void populateList() {

		final ListView listview = (ListView) findViewById(R.id.spotsListView);

		// ArrayAdapter<String> a = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, spots);
		SpotItemAdapter a = new SpotItemAdapter(this,
				R.layout.spotlist_item_row,
				spotObjects.toArray(new SpotListItem[spotObjects.size()]));

		listview.setAdapter(a);
	}

}
