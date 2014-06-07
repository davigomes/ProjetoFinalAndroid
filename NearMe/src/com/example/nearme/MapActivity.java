package com.example.nearme;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

public class MapActivity extends FragmentActivity implements LocationSource, LocationListener {

	private static final String ACTION = "com.example.nearme.MapActivity";
	private static final String url = "http://incidenteapi.apphb.com/api/messages";
	private ProgressDialog progress;
	
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
        
        map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
				
			}
		});
        
        myCriteria = new Criteria();
        myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        myLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
        try
		{
			HttpGet searchRequest = new HttpGet(new URI(url));
			RestTask task = new RestTask(this, ACTION, true);
			task.execute(searchRequest);
			progress = ProgressDialog.show(this, "Searching", "Waiting...", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
	  registerReceiver(receiver, new IntentFilter(ACTION));
	  
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
	  unregisterReceiver(receiver);
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
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			/*prefs = getSharedPreferences(prefName, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(TEXT_USERNAME);
			editor.commit();*/
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(progress != null)
			{
				progress.dismiss();
			}
			String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
			
			try
			{
				JSONArray messages = new JSONArray(response);
				JSONObject message;
				for (int i = 0; i < messages.length(); i++)
				{
					message = new JSONObject(messages.getString(i));
					String formattedDate = "";
					
					try {
						Date parsedDateInstance = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(message.getString("SendDate"));
						formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(parsedDateInstance);
						
					} catch (ParseException e) {						
						e.printStackTrace();
					}
					
					map.addMarker(new MarkerOptions()
					.position(new LatLng(message.getDouble("Latitude"), message.getDouble("Longitude")))
					.title(message.getString("TextBody"))
					.snippet("Autor: " + message.getString("Author") +"\nData: " + formattedDate));
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	};
}
