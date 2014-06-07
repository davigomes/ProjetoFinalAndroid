package com.example.nearme;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PopupAdapter implements InfoWindowAdapter 
{
	LayoutInflater inflater = null;
	
	PopupAdapter(LayoutInflater inflater)
	{
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View popup = inflater.inflate(R.layout.popup, null);
		
		TextView text = (TextView)popup.findViewById(R.id.title);
		text.setText(marker.getTitle());
		text = (TextView)popup.findViewById(R.id.snippet);
		text.setText(marker.getSnippet());
		
		return popup;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
}
