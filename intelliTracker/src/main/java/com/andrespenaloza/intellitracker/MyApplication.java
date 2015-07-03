package com.andrespenaloza.intellitracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MyApplication extends Application {

	private static String hs = "";

	@Override
	public void onCreate() {
		loadEntries();

		super.onCreate();
	}

	public MyApplication() {
	}

	public static String getScript() {
		return hs;
	}

	static public void saveEntries(android.content.Context context) {
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.commit();
	}

	protected void loadEntries() {
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	}

}
