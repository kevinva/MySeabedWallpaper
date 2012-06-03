package cn.kevin.seabedwallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SeabedWallSettingsActivity extends PreferenceActivity{

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.settings);
		this.setContentView(R.layout.preference_main);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


}
