/*
 * Preferences.java
 *
 * Copyright (C) 2011 Francesco Brisa
 *
 * This file is part of Comete for Comete.
 *
 * AirportPainter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AirportPainter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License V.3 for more details.
 *
 * You should have received a copy of the GNU General Public License V.3
 * along with AirportPainter.  If not, see <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/gpl-3.0.html
 *
 * email at:
 * fbrisa@gmail.com  or  fbrisa@yahoo.it
 *
 */
package info.alni.comete.android;
import info.alni.comete.android.R;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.PreferenceActivity.Header;
import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;


public class Preferences extends PreferenceActivity {
	
	

	@Override
	public void onBuildHeaders(List<Header> target) {
		//if (getIntent().getAction()==null) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		//}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getIntent().getAction()==null) {
			//addPreferencesFromResource(R.xml.preferences);
		}else {
			if (getIntent().getAction().equals("reset")) {
				loadDefaults();
				finish();
			}			
		}						
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	private void loadDefaults() {
		 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 	
		 SharedPreferences.Editor editor = sharedPreferences.edit();
		 
		 Map<String, ?> map = sharedPreferences.getAll();
		 for(String key : map.keySet()) {
			 editor.remove(key);        	   
		 }
		 editor.clear();
		 editor.commit();
	}
	
	public static class SettingsFragment extends PreferenceFragment {
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);


	        //sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
	        String settings = getArguments().getString("settings");
	        if ("prop.bindings".equals(settings)) {
	            addPreferencesFromResource(R.xml.preferences_prop);
	        } else if ("sensibility".equals(settings)) {
	            addPreferencesFromResource(R.xml.preferences_sensibility);
	        } else if ("Screen".equals(settings)) {
	            addPreferencesFromResource(R.xml.preferences_screen);
	        }
		}
	}

}
