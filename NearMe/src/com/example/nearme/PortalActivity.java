package com.example.nearme;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PortalActivity extends Activity {
	private EditText txtName;
	private SharedPreferences prefs;
	private String prefName = "NearMePref";
	
	private static final String TEXT_USERNAME = "textusername";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portal);
		
		prefs = getSharedPreferences(prefName, MODE_PRIVATE);
		String username = prefs.getString(TEXT_USERNAME, "");
		if (username != "")
		{
			Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
			startActivity(mapIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.portal, menu);
		return true;
	}
	
	public void enter(View view) {
		txtName = (EditText) findViewById(R.id.txtName);
		if (txtName.length() == 0)
		{
			Toast.makeText(getBaseContext(), "Digite seu nome antes de entrar!", Toast.LENGTH_SHORT).show();
		}
		else
		{
			prefs = getSharedPreferences(prefName, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(TEXT_USERNAME, txtName.getText().toString());
			editor.commit();
			
			try
			{
				Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(mapIntent);
			}
			catch (Exception e)
			{
				Log.e("e", e.getMessage());
			}
		}
	}

}
