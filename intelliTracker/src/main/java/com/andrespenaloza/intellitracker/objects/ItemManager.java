package com.andrespenaloza.intellitracker.objects;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.SystemClock;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.factory.LabelFactory;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ItemManager {
	static private HashMap<Integer, TrackingItem> mTrackingItems = new HashMap<Integer, TrackingItem>();
	static private HashMap<Integer, Label> mLabels = new HashMap<Integer, Label>();
	static private ArrayList<TrackingItem> mCurrentTrackingList = new ArrayList<TrackingItem>();
	private static Label mSelectedLabel;

	static public final String LABEL_CATEGORY_NONE = "None";
	static public final String LABEL_CATEGORY_UNFINISHED = "Unfinished";

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
    private String[] TrackingItemColumns = {
            MySQLiteHelper.TRACKINGITEM_ID, MySQLiteHelper.TRACKINGITEM_NAME,
            MySQLiteHelper.TRACKINGITEM_TRACK_NUMBER, MySQLiteHelper.TRACKINGITEM_DATE_CREATED,
            MySQLiteHelper.TRACKINGITEM_DATE_LAST_QUERY, MySQLiteHelper.TRACKINGITEM_LAST_TRACK_RESULT_LIST,
            MySQLiteHelper.TRACKINGITEM_LAST_TRACK_PACKAGE_STATUS, MySQLiteHelper.TRACKINGITEM_PACKAGE_STATUS_MANUAL,
            MySQLiteHelper.TRACKINGITEM_ORIGIN_COUNTRY, MySQLiteHelper.TRACKINGITEM_DESTINATION_COUNTRY,
            MySQLiteHelper.TRACKINGITEM_COURIER_ID
    };
	private String[] LabelColorColumns = { MySQLiteHelper.LABELCOLORS_NAME, MySQLiteHelper.LABELCOLORS_TEXT_COLOR, MySQLiteHelper.LABELCOLORS_BACKGROUND_COLOR	};
	private String[] LabelColumns = { MySQLiteHelper.LABEL_ID, MySQLiteHelper.LABEL_NAME,MySQLiteHelper.LABEL_COLOR_NAME };
	private String[] LabelTrackingColumns = { MySQLiteHelper.LT_ID_TRACKINGITEM, MySQLiteHelper.LT_ID_LABEL};

    private static ItemManager instance;
	private Context mContext;

    public static ItemManager getInstance(Context c){
        if (instance == null){
            instance = new ItemManager(c);
        }
        return instance;
    }

	private ItemManager(Context context) {
		mContext = context;
		dbHelper = new MySQLiteHelper(context);
		open();
		//load database the first time
		loadDatabase();
	}

	private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

	private void loadDatabase() {
		mTrackingItems.putAll(getAllTrackingItems());
		LabelFactory.LABEL_COLORS.addAll(getAllLabelColors());
		setupLabelColors();
		mLabels.putAll(getAllLabels());
		setupLabels();
		getAllTrackingItemLabelMatches();
	}

	private void getAllTrackingItemLabelMatches() {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LABEL_TRACKINGITEM,
				LabelTrackingColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TrackingItem ti = mTrackingItems.get(cursor.getInt(0));
			Label l = mLabels.get(cursor.getInt(1));
			ti.addLabel(l.getId());
			l.addTrackingItem(ti.getId());

			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
	}

	private HashMap<Integer, Label> getAllLabels() {
		HashMap<Integer, Label> items = new HashMap<Integer, Label>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_LABEL,
				LabelColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Label l = cursorToLabel(cursor);
			items.put(l.getId(), l);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return items;
	}

	private ArrayList<LabelColor> getAllLabelColors() {
		ArrayList<LabelColor> items = new ArrayList<LabelColor>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_LABELCOLORS,
				LabelColorColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LabelColor lc = cursorToLabelColor(cursor);
			items.add(lc);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return items;
	}

	public TrackingItem createTrackingItem(String name, String trackingNumber) {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
		Date dateNow = new Date();
        values.put(MySQLiteHelper.TRACKINGITEM_NAME, name);
        values.put(MySQLiteHelper.TRACKINGITEM_TRACK_NUMBER, trackingNumber);
        values.put(MySQLiteHelper.TRACKINGITEM_DATE_CREATED, iso8601Format.format(dateNow) );
        long insertId = database.insert(MySQLiteHelper.TABLE_TRACKINGITEM, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACKINGITEM,
                TrackingItemColumns, MySQLiteHelper.TRACKINGITEM_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        TrackingItem newTrackingItem = cursorToTrackingItem(cursor);
        cursor.close();
        return newTrackingItem;
    }

	private boolean removeTrackingItem(int trackingItemId) {
		int itemsRemoved = database.delete(MySQLiteHelper.TABLE_TRACKINGITEM, MySQLiteHelper.TRACKINGITEM_ID
				+ " = " + trackingItemId, null);
		return itemsRemoved != 0;
	}

	public HashMap<Integer, TrackingItem> getAllTrackingItems() {
		HashMap<Integer, TrackingItem> trackingItems = new HashMap<Integer, TrackingItem>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_TRACKINGITEM,
				TrackingItemColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TrackingItem trackingItem = cursorToTrackingItem(cursor);
			trackingItems.put(trackingItem.getId(),trackingItem);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return trackingItems;
	}

	public boolean saveTrackingItem(TrackingItem trackingItem) {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.TRACKINGITEM_NAME, trackingItem.getName());
		values.put(MySQLiteHelper.TRACKINGITEM_TRACK_NUMBER, trackingItem.getTrackingNumber());
		values.put(MySQLiteHelper.TRACKINGITEM_DATE_CREATED, iso8601Format.format(trackingItem.getDateCreated()) );
		values.put(MySQLiteHelper.TRACKINGITEM_DATE_LAST_QUERY, iso8601Format.format(trackingItem.getLastQuery()));
		values.put(MySQLiteHelper.TRACKINGITEM_LAST_TRACK_RESULT_LIST, TrackingItem.statusListToString(trackingItem.getStatusList()));
		values.put(MySQLiteHelper.TRACKINGITEM_LAST_TRACK_PACKAGE_STATUS, trackingItem.getPackageStatus());
		values.put(MySQLiteHelper.TRACKINGITEM_PACKAGE_STATUS_MANUAL,trackingItem.getPackageStatusOverride());
		values.put(MySQLiteHelper.TRACKINGITEM_ORIGIN_COUNTRY, trackingItem.getOriginCountry());
		values.put(MySQLiteHelper.TRACKINGITEM_DESTINATION_COUNTRY, trackingItem.getDestinationCountry());
		values.put(MySQLiteHelper.TRACKINGITEM_COURIER_ID, trackingItem.getCourier());
		long insertId = database.update(MySQLiteHelper.TABLE_TRACKINGITEM,
				values, MySQLiteHelper.TRACKINGITEM_ID + "=" + trackingItem.getId(),null);
		return insertId != -1;
	}

    private TrackingItem cursorToTrackingItem(Cursor cursor) {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date dateCreated = new Date(0);
        Date lastQuery = new Date(0);
        String statusList = null;
        int packageStatus = 0;
        int packageStatusManual = 0;
        String originCountry = "";
        String destinationCountry = "";
        int courier = 0;

        try {
            dateCreated = iso8601Format.parse(cursor.getString(3));
        } catch (Exception ignored) { }
        try {
            lastQuery = iso8601Format.parse(cursor.getString(4));
        } catch (Exception ignored) { }
        try {
            statusList = cursor.getString(5);
        } catch (Exception ignored) { }
        try {
            packageStatus = cursor.getInt(6);
        } catch (Exception ignored) { }
        try {
            packageStatusManual = cursor.getInt(7);
        } catch (Exception ignored) { }
        try {
            originCountry = cursor.getString(8);
        } catch (Exception ignored) { }
        try {
            destinationCountry = cursor.getString(9);
        } catch (Exception ignored) { }
        try {
            courier = cursor.getInt(10);
        } catch (Exception ignored) { }


        TrackingItem trackingItem = null;
        try {
            trackingItem = new TrackingItem(
                    cursor.getInt(0),cursor.getString(1),
                    cursor.getString(2),dateCreated,
                    lastQuery,TrackingItem.stringToStatusList(statusList),
                    packageStatus,packageStatusManual,
                    originCountry,destinationCountry,
                    courier);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackingItem;
    }

	private boolean createLink(int trackingItemId, int LabelId) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.LT_ID_LABEL, LabelId);
		values.put(MySQLiteHelper.LT_ID_TRACKINGITEM, trackingItemId);
		long insertId = database.insert(MySQLiteHelper.TABLE_LABEL_TRACKINGITEM, null,
				values);
		return insertId != -1;
	}

	private boolean removeLink(int trackingItemId, int LabelId) {
		int itemsRemoved = database.delete(MySQLiteHelper.TABLE_LABEL_TRACKINGITEM, MySQLiteHelper.LT_ID_LABEL
				+ " = " + LabelId + " and " + MySQLiteHelper.LT_ID_TRACKINGITEM + " = " + trackingItemId, null);
		return itemsRemoved != 0;
	}

	private void setupLabelColors() {
		if (LabelFactory.LABEL_COLORS.size() == 0) {
			LabelFactory.LABEL_COLORS.add(addLabelColor("Red 1", mContext.getResources().getColor(R.color.label_red_1), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Green 1", mContext.getResources().getColor(R.color.label_green_1), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Yellow 1", mContext.getResources().getColor(R.color.label_yellow_1), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Purple 1", mContext.getResources().getColor(R.color.label_purple_1), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Blue 1",   mContext.getResources().getColor(R.color.label_blue_1), Color.WHITE));

			LabelFactory.LABEL_COLORS.add(addLabelColor("Red 2",    mContext.getResources().getColor(R.color.label_red_2), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Green 2",  mContext.getResources().getColor(R.color.label_green_2), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Yellow 2", mContext.getResources().getColor(R.color.label_yellow_2), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Purple 2", mContext.getResources().getColor(R.color.label_purple_2), Color.WHITE));
			LabelFactory.LABEL_COLORS.add(addLabelColor("Blue 2", mContext.getResources().getColor(R.color.label_blue_2), Color.WHITE));

			LabelFactory.LABEL_COLORS.add(addLabelColor("Black on White", Color.WHITE, Color.BLACK));
		}
	}

	private LabelColor addLabelColor(String name, int textColor, int backgroundColor){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.LABELCOLORS_NAME, name);
		values.put(MySQLiteHelper.LABELCOLORS_TEXT_COLOR, textColor);
		values.put(MySQLiteHelper.LABELCOLORS_BACKGROUND_COLOR, backgroundColor);
		database.insert(MySQLiteHelper.TABLE_LABELCOLORS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LABELCOLORS,
				LabelColorColumns, MySQLiteHelper.LABELCOLORS_NAME + " = \"" + name + "\"", null,
				null, null, null);
		cursor.moveToFirst();
		LabelColor newLabelColor = cursorToLabelColor(cursor);
		cursor.close();
		return newLabelColor;
	}

	private LabelColor cursorToLabelColor(Cursor cursor) {
		LabelColor labelColor = null;
		try {
			labelColor = new LabelColor(cursor.getString(0),cursor.getInt(1),cursor.getInt(2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labelColor;
	}

    private void setupLabels() {
        if (mLabels.size() == 0) {
			Label l = createLabel(LABEL_CATEGORY_NONE, "Black on White");
            mLabels.put(l.getId(),l);
            l = createLabel(LABEL_CATEGORY_UNFINISHED, "Black on White");
			mLabels.put(l.getId(), l);
        }
        mSelectedLabel = getLabel(LABEL_CATEGORY_UNFINISHED);
    }

	private Label createLabel(String name, String labelColor){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.LABEL_NAME, name);
		values.put(MySQLiteHelper.LABEL_COLOR_NAME, labelColor);
		long insertId = database.insert(MySQLiteHelper.TABLE_LABEL, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LABEL,
				LabelColumns, MySQLiteHelper.LABEL_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		Label newLabel = cursorToLabel(cursor);
		cursor.close();
		return newLabel;
	}

	private boolean removeLabel(int labelId) {
		int itemsRemoved = database.delete(MySQLiteHelper.TABLE_LABEL, MySQLiteHelper.LABEL_ID
				+ " = " + labelId, null);
		return itemsRemoved != 0;
	}

	private Label cursorToLabel(Cursor cursor) {
		Label label = null;
		LabelColor lc = null;
		for (LabelColor i : LabelFactory.LABEL_COLORS){
			if (i.mText.equals(cursor.getString(2)))
				lc = i;
		}
		try {
			label = new Label(cursor.getInt(0),cursor.getString(1),lc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return label;
	}

	public void close() {
		dbHelper.close();
	}

	static public void selectLabel(String name) {
		mSelectedLabel = getLabel(name);
	}

	static public void removeItem(TrackingItem item) {
		for (Label label : item.getLabels()) {
			if (!ItemManager.getInstance(null).removeLink(item.getId(), label.getId()))
				continue;
			item.removeLabel(label.getId());
			label.removeTrackingItem(item.getId());
		}
		if (ItemManager.getInstance(null).removeTrackingItem(item.getId()))
			mTrackingItems.remove(item.getId());
	}

	static public void removeLabel(Label label) {
		for (TrackingItem item : label.getItems()) {
			unlinkLabel(item, label);
		}
		if (ItemManager.getInstance(null).removeLabel(label.getId()))
			mLabels.remove(label.getId());
	}

	static public ArrayList<TrackingItem> getSelectedItems() {
		mCurrentTrackingList = mSelectedLabel.getItems();
		return mCurrentTrackingList;
	}

	static public boolean isEmpty() {
		return mTrackingItems.isEmpty();
	}

	static public Label getLabel(int id) {
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

	public static TrackingItem getTrackingItem(int id) {
		TrackingItem item = mTrackingItems.get(id);
		if (item != null)
			return item;
		return null;
	}

	public static Label addLabel(String name, LabelColor color) {
		if (getLabel(name) != null)
			return null;
		Label label = ItemManager.getInstance(null).createLabel(name, color.mText);
		mLabels.put(label.getId(), label);

		// avoid getting same id
		SystemClock.sleep(1);
		return label;
	}

	public static TrackingItem addTrackingItem(String name, String trackingNumber) {
		//TrackingItem item = new TrackingItem(name, trackingNumber);
		TrackingItem item = ItemManager.getInstance(null).createTrackingItem(name, trackingNumber);
		linkLabel(item, getLabel(LABEL_CATEGORY_NONE));
		linkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
		// add to list
		mTrackingItems.put(item.getId(), item);
		return item;
	}

	public static void archive(TrackingItem item) {
		unlinkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
	}

	public static void unarchive(TrackingItem item) {
		linkLabel(item, getLabel(LABEL_CATEGORY_UNFINISHED));
	}

	static public void unlinkLabel(TrackingItem item, Label label) {
		if (!ItemManager.getInstance(null).removeLink(item.getId(), label.getId()))
			return;
		// remove custom label
		item.removeLabel(label.getId());
		label.removeTrackingItem(item.getId());

		// manage categories
		if (!item.hasCustomLabels()) {
			// dont use linkLabel()!
			// get in none category
			Label label_none = getLabel(LABEL_CATEGORY_NONE);
			Label label_unfinished = getLabel(LABEL_CATEGORY_UNFINISHED);
			if (label.getId() != label_none.getId() && label.getId() != label_unfinished.getId()) {
				if (!ItemManager.getInstance(null).createLink(item.getId(), label_none.getId()))
					return;
				item.addLabel(label_none.getId());
				label_none.addTrackingItem(item.getId());
			}
		}
	}


	static public void linkLabel(TrackingItem item, Label label) {
		if (!ItemManager.getInstance(null).createLink(item.getId(), label.getId()))
			return;
		// manage categories
		if (!item.hasCustomLabels()) {
			// dont use unlinkLabel()!
			// get out of none category
			Label label_none = getLabel(LABEL_CATEGORY_NONE);
			Label label_unfinished = getLabel(LABEL_CATEGORY_UNFINISHED);
			if (label.getId() != label_none.getId() && label.getId() != label_unfinished.getId()){
				if (!ItemManager.getInstance(null).removeLink(item.getId(), label_none.getId()))
					;
				item.removeLabel(label_none.getId());
				label_none.removeTrackingItem(item.getId());
			}
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
