package com.franchali.spotspuertorico;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpotItemAdapter extends ArrayAdapter<SpotListItem> {

	Context context;
	int layoutResourceId;
	SpotListItem data[] = null;

	public SpotItemAdapter(Context context, int layoutResourceId,
			SpotListItem[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SpotHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new SpotHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtDescSpot = (TextView) row.findViewById(R.id.txtDescSpot);
			
			row.setTag(holder);
		} else {
			holder = (SpotHolder) row.getTag();
		}

		SpotListItem spot = data[position];
		holder.txtTitle.setText(spot.title);
		holder.imgIcon.setImageBitmap(spot.icon);
		holder.txtDescSpot.setText(spot.description);

		return row;
	}

	static class SpotHolder {
		ImageView imgIcon;
		TextView txtTitle;
		TextView txtDescSpot;
	}
}
