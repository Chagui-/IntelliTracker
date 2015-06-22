package com.andrespenaloza.intellitracker.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;

import android.util.Log;

public class JavaScriptInterpreter {
	
	static public Context getContext() {
		
		// Create an execution environment.
		Context cx = Context.enter();

		// Turn compilation off.
		cx.setOptimizationLevel(-1);

		return cx;
	}
	
	static public String getStringFromFile(String path){
		FileInputStream fin;
		File f = new File(path);
		try {
			fin = new FileInputStream(f);
			String ret = convertStreamToString(fin);
			// Make sure you close all streams.
			fin.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("JavaScriptInterpreter", "File not Found: " + path);
		return "";
	}

	private static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getStringFromFile(String filePath,android.content.Context context){
		InputStream fin;
		try {
			fin = context.getAssets().open(filePath);;
			String ret = convertStreamToString(fin);
			// Make sure you close all streams.
			fin.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("JavaScriptInterpreter", "File not Found: " + filePath);
		return "";
	}

}