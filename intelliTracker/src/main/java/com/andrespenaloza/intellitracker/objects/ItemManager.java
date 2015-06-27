package com.andrespenaloza.intellitracker.objects;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.SystemClock;

import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ItemManager {
	public static class YearTrackingList {
		private HashMap<Integer, HashMap<Long, TrackingItem>> mYears = new HashMap<Integer, HashMap<Long, TrackingItem>>();

		public YearTrackingList() {

		}

		public HashMap<Long, TrackingItem> getItemsOfYear(int year) {
			return mYears.get(year);
		}

		public void addTrackingItem(TrackingItem item) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(item.getId()));
			int year = cal.get(Calendar.YEAR);
			HashMap<Long, TrackingItem> items = mYears.get(year);
			if (items == null) {
				items = new HashMap<Long, TrackingItem>();
				mYears.put(year, items);
			}
			items.put(item.getId(), item);
		}

		public boolean isEmpty() {
			return mYears.isEmpty();
		}

		public void removeTrackingItem(TrackingItem item) {
			for (HashMap<Long, TrackingItem> map : mYears.values()) {
				map.remove(item.getId());
			}
		}
	}

	static private YearTrackingList mYearTrackingList = new YearTrackingList();
	static private HashMap<Long, Label> mLabels = new HashMap<Long, Label>();
	static private ArrayList<TrackingItem> mCurrentTrackingList = new ArrayList<TrackingItem>();
	private static Label mSelectedLabel;

	static public final String LABEL_CATEGORY_NONE = "None";
	static public final String LABEL_CATEGORY_UNFINISHED = "Unfinished";

	static {
	}

	public static void setupLabels() {
		if (mLabels.size() == 0) {
			addLabel(LABEL_CATEGORY_NONE, new LabelColor("None", Color.WHITE, Color.BLACK));
			addLabel(LABEL_CATEGORY_UNFINISHED, new LabelColor("Unfinished", Color.WHITE, Color.BLACK));
		}
		mSelectedLabel = getLabel(LABEL_CATEGORY_UNFINISHED);
	}

	private ItemManager() {
	}

	static public void saveAsJson(Editor prefsEditor) {
		Gson gson = new Gson();
		String jsonYearTrackingList = "";
		String jsonLabels = "";
		String jsonCurrentTrackingList = "";

		try {
			jsonYearTrackingList = gson.toJson(mYearTrackingList);
			jsonLabels = gson.toJson(mLabels);
			jsonCurrentTrackingList = gson.toJson(mCurrentTrackingList);
		} catch (Exception e) {

		}

		prefsEditor.putString("YearTrackingList", jsonYearTrackingList);
		prefsEditor.putString("Labels", jsonLabels);
		prefsEditor.putString("CurrentTrackingList", jsonCurrentTrackingList);
	}

	static public void loadFromJson(SharedPreferences appSharedPrefs) {
		String jsonYearTrackingList = appSharedPrefs.getString("YearTrackingList", "");
		String jsonLabels = appSharedPrefs.getString("Labels", "");
		String jsonCurrentTrackingList = appSharedPrefs.getString("CurrentTrackingList", "");
		try {
			Gson gson = new Gson();

			mYearTrackingList = gson.fromJson(jsonYearTrackingList, YearTrackingList.class);

			Type typeLabels = new TypeToken<HashMap<Long, Label>>() {
			}.getType();
			mLabels = gson.fromJson(jsonLabels, typeLabels);

			Type typeCurrentTrackingList = new TypeToken<ArrayList<TrackingItem>>() {
			}.getType();
			mCurrentTrackingList = gson.fromJson(jsonCurrentTrackingList, typeCurrentTrackingList);

			if (mYearTrackingList == null) {
				mYearTrackingList = new YearTrackingList();
			}
			if (mLabels == null) {
				mLabels = new HashMap<Long, Label>();
			}
			setupLabels();

			if (mCurrentTrackingList == null) {
				mCurrentTrackingList = new ArrayList<TrackingItem>();
			}
		} catch (JsonSyntaxException e) {

		}
	}

	static public void selectLabel(String name) {
		mSelectedLabel = getLabel(name);
	}

	static public void removeItem(TrackingItem item) {
		for (Label label : item.getLabels()) {
			unlinkLabel(item, label);
		}
		mYearTrackingList.removeTrackingItem(item);
	}

	static public void removeLabel(Label label) {
		for (TrackingItem item : label.getItems()) {
			unlinkLabel(item, label);
		}
		mLabels.remove(label.getId());
	}

	static public ArrayList<TrackingItem> getSelectedItems() {
		mCurrentTrackingList = mSelectedLabel.getItems();
		return mCurrentTrackingList;
	}

	static public boolean isEmpty() {
		return mYearTrackingList.isEmpty();
	}

	static public Label getLabel(long id) {
		return mLabels.get(id);
	}

	static public ArrayList<Label> getLabels() {
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.addAll(mLabels.values());
		Label none = null, unfinished = null;
		for (Label label : labels) {
			if (label.getName().compareTo(LABEL_CATEGORY_NONE) == 0)
				none = label;
			else if (label.getName().compareTo(LABEL_CATEGORY_UNFINISHED) == 0)
				unfinished = label;
		}
		labels.remove(none);
		labels.remove(unfinished);
		labels.add(0, none);
		labels.add(0, unfinished);
		return labels;
	}

	public static Label getLabel(String name) {
		for (Label label : mLabels.values()) {
			if (label.getName().equals(name)) {
				return label;
			}
		}
		return null;
	}

	public static TrackingItem getTrackingItem(long id) {
		for (HashMap<Long, TrackingItem> items : mYearTrackingList.mYears.values()) {
			TrackingItem item = items.get(id);
			if (item != null)
				return item;
		}
		return null;
	}

	public static Label addLabel(String name, LabelColor color) {
		if (getLabel(name) != null)
			return null;
		Label label = new Label(name, color);
		mLabels.put(label.getId(), label);

		// avoid getting same id
		SystemClock.sleep(1);
		return label;
	}

	public static TrackingItem addTrackingItem(String name, String trackingNumber) {
		TrackingItem item = new TrackingItem(name, trackingNumber);
		linkLabel(item, getLabel(LABEL_CATEGORY_NONE));
		linkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
		// add to years
		mYearTrackingList.addTrackingItem(item);
		// avoid getting same id
		SystemClock.sleep(1);
		return item;
	}

	public static void archive(TrackingItem item) {
		unlinkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
	}

	public static void unarchive(TrackingItem item) {
		linkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
	}

	static public void unlinkLabel(TrackingItem item, Label label) {
		// remove custom label
		item.removeLabel(label.getId());
		label.removeTrackingItem(item.getId());

		// manage categories
		if (item.hasCustomLabels() == false) {
			// dont use linkLabel()!
			// get in none category
			Label label_none = getLabel(LABEL_CATEGORY_NONE);
			item.addLabel(label_none.getId());
			label_none.addTrackingItem(item.getId());
		}
		return;
	}

	static public void linkLabel(TrackingItem item, Label label) {
		// manage categories
		if (item.hasCustomLabels() == false) {
			// dont use unlinkLabel()!
			// get out of none category
			Label label_none = getLabel(LABEL_CATEGORY_NONE);
			try {
				item.removeLabel(label_none.getId());
			} catch (Exception e) {
				// Log.w("ItemManager", "linkLabel none " + mLabels.toString());
				e.printStackTrace();
			}
			label_none.removeTrackingItem(item.getId());
		}
		// add custom label
		item.addLabel(label.getId());
		label.addTrackingItem(item.getId());
	}

	public static void popItem(TrackingItem item) {
		mCurrentTrackingList.remove(item);
		mCurrentTrackingList.add(0, item);
	}

}
