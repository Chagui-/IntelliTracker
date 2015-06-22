package com.andrespenaloza.intellitracker;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.andrespenaloza.intellitracker.factory.LabelFactory;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.JavaScriptInterpreter;

public class MyApplication extends Application {

	private static String hs = "";

	@Override
	public void onCreate() {
		hs = JavaScriptInterpreter.getStringFromFile("hs.js", getApplicationContext());

		loadEntries();

		LabelFactory.LABEL_COLORS.add(new LabelColor("Red 1", getApplicationContext().getResources().getColor(R.color.label_red_1), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Green 1", getApplicationContext().getResources().getColor(R.color.label_green_1), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Yellow 1", getApplicationContext().getResources().getColor(R.color.label_yellow_1), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Purple 1", getApplicationContext().getResources().getColor(R.color.label_purple_1), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Blue 1", getApplicationContext().getResources().getColor(R.color.label_blue_1), Color.WHITE));

		LabelFactory.LABEL_COLORS.add(new LabelColor("Red 2", getApplicationContext().getResources().getColor(R.color.label_red_2), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Green 2", getApplicationContext().getResources().getColor(R.color.label_green_2), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Yellow 2", getApplicationContext().getResources().getColor(R.color.label_yellow_2), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Purple 2", getApplicationContext().getResources().getColor(R.color.label_purple_2), Color.WHITE));
		LabelFactory.LABEL_COLORS.add(new LabelColor("Blue 2", getApplicationContext().getResources().getColor(R.color.label_blue_2), Color.WHITE));
		// if (ItemManager.isEmpty()) {
		// ItemManager.addTrackingItem("Cables" , "RF252387055SG");
		// ItemManager.addTrackingItem("Tarjeta de Sonido" , "RJ141559891CN");
		// ItemManager.addTrackingItem("Antenas RF" , "RJ052553375CN");
		// ItemManager.addTrackingItem("HDD Case" , "RN602518315CN");
		// ItemManager.addTrackingItem("Repisas" , "RC591353321CN");
		// ItemManager.addTrackingItem("Cajas" , "RJ102564775CN");
		// ItemManager.addTrackingItem("Rectificador" , "RW534045289CN");
		// ItemManager.addTrackingItem("EEprom" , "RC126419132HK");
		// ItemManager.addTrackingItem("Zener" , "RI055963744CN");
		// ItemManager.addTrackingItem("Conectores" , "RT224869595HK");
		// ItemManager.addTrackingItem("Mosfet" , "RI055963744CN");
		// ItemManager.addTrackingItem("Alicate" , "RI109315469CN");
		// ItemManager.addTrackingItem("Pesa" , "RL020275327CN");
		// ItemManager.addTrackingItem("Multimetro" , "RJ107216874CN");
		// ItemManager.addTrackingItem("Pin Header H" , "RC803583982CN");
		// ItemManager.addTrackingItem("PCB" , "RT211219367HK");
		//
		// ItemManager.addLabel("Grupo 1", LabelFactory.LABEL_COLORS.get(0));
		// ItemManager.addLabel("Grupo 2", LabelFactory.LABEL_COLORS.get(1));
		// ItemManager.addLabel("Grupo 3", LabelFactory.LABEL_COLORS.get(2));
		// }

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
		ItemManager.saveAsJson(prefsEditor);

		prefsEditor.commit();
	}

	protected void loadEntries() {
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		ItemManager.loadFromJson(appSharedPrefs);
	}

}
