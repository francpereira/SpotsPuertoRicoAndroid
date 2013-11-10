package com.franchali.spotspuertorico;

import android.graphics.Bitmap;

public class SpotListItem {

	public Bitmap icon;
	public String title;
	public String description;

	public SpotListItem() {
		super();
	}

	public SpotListItem(Bitmap icon, String title, String descripcion) {
		super();
		this.icon = icon;
		this.title = title;
		this.description = descripcion;
	}

}
