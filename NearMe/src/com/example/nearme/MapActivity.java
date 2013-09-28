package com.example.nearme;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MapActivity extends FragmentActivity implements LocationSource, LocationListener {

	private SharedPreferences prefs;
	private String prefName = "NearMePref";
	private GoogleMap map;
	Criteria myCriteria;
	LocationManager myLocationManager = null;
	OnLocationChangedListener myLocationListener = null;
	
	final int RQS_GooglePlayServices = 1;
	private static final String TEXT_USERNAME = "textusername";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setMyLocationEnabled(true);
        
        myCriteria = new Criteria();
        myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_msg:
	        	Intent mapIntent = new Intent(getApplicationContext(), MessageActivity.class);
				startActivity(mapIntent);
	            return true;
	        case R.id.menu_exit:
	        	prefs = getSharedPreferences(prefName, MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.remove(TEXT_USERNAME);
				editor.commit();
				finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	 protected void onResume() {
	  super.onResume();
	  
	  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	  
	  if (resultCode == ConnectionResult.SUCCESS){
		  //Register for location updates using a Criteria, and a callback on the specified looper thread.
		  myLocationManager.requestLocationUpdates(
			  0L,    //minTime
			  0.0f,    //minDistance
			  myCriteria,  //criteria
			  this,    //listener
			  null);   //looper
		   
			  //Replaces the location source of the my-location layer.
			  map.setLocationSource(this);
	  }
	  else{
	   GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices); 
	  }
	}
	
	@Override
	 protected void onPause() {
	  map.setLocationSource(null);
	  myLocationManager.removeUpdates(this);
	     
	  super.onPause();
	 }

	@Override
	public void activate(OnLocationChangedListener listener) {
		myLocationListener = listener;
	}

	@Override
	public void deactivate() {
		myLocationListener = null;		
	}

	@Override
	public void onLocationChanged(Location location) {
		if (myLocationListener != null) {
		   myLocationListener.onLocationChanged(location);
		   
		   LatLng latlng= new LatLng(location.getLatitude(), location.getLongitude());
		   map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15.0f));		   
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			prefs = getSharedPreferences(prefName, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(TEXT_USERNAME);
			editor.commit();
			finish();
			break;
		}
		return false;
	}
}
