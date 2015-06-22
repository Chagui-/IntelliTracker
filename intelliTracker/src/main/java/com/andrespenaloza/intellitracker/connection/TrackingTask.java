package com.andrespenaloza.intellitracker.connection;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import android.text.TextUtils;

import com.andrespenaloza.intellitracker.MyApplication;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.ItemManager.TrackingItem;
import com.andrespenaloza.intellitracker.objects.ItemManager.TrackingItem.StatusPair;
import com.andrespenaloza.intellitracker.objects.JavaScriptInterpreter;

public class TrackingTask implements Runnable {

	private static TrackingManager sTrackingManager;
	private String mTrackingURL;
	private JSONObject mResponse;

	private WeakReference<TrackingItem> mItemWeakReference;
	private Thread mCurrentThread;
	private HttpGet mHttpGet;

	// The Thread on which this task is currently running.

	// Item status
	private String lastStatus;
	private Date lastdateDate;
	private Date firstdateDate;
	private int delivered;
	private ArrayList<StatusPair> status;

	private int stateOrigin;
	private int stateDestination;

	private long timeOrigin;
	private long timeDestination;

	// last state
	int mLastErrorStatus;

	public int getStateOrigin() {
		return stateOrigin;
	}

	public int getStateDestination() {
		return stateDestination;
	}

	public long getTimeOrigin() {
		return timeOrigin;
	}

	public long getTimeDestination() {
		return timeDestination;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public Date getLastdateDate() {
		return lastdateDate;
	}

	public Date getFirstdateDate() {
		return firstdateDate;
	}

	public int getDelivered() {
		return delivered;
	}

	public ArrayList<StatusPair> getStatus() {
		return status;
	}

	public TrackingTask() {
	}

	void initializeDownloaderTask(TrackingManager trackingManager, TrackingItem item) {
		// Sets this object's ThreadPool field to be the input argument
		sTrackingManager = trackingManager;

		stateOrigin = 1;
		stateDestination = 1;

		// Gets the URL for the View
		mTrackingURL = "http://www.17track.net";
		// mTrackingURL = "http://s1.17track.net";

		// parse tracking number (compatibility with last versions)
		if (item.getTrackingNumber().matches(TrackingItem.COURIER_GLOBAL_POSTAL_CODE))
			item.setCourier(ItemManager.TrackingItem.COURIER_GLOBAL_POSTAL);

		// Instantiates the weak reference to the incoming view
		mItemWeakReference = new WeakReference<TrackingItem>(item);
	}

	// Returns the ImageView that's being constructed.
	public TrackingItem getTrackingItem() {
		if (null != mItemWeakReference) {
			return mItemWeakReference.get();
		}
		return null;
	}

	@Override
	public void run() {

		synchronized (sTrackingManager) {
			mCurrentThread = Thread.currentThread();
		}
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

		boolean cachedUpdate = !mItemWeakReference.get().canUpdate();

		try {
			// Before continuing, checks to see that the Thread hasn't been
			// interrupted
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}

			if (cachedUpdate) {
				handleState(TrackingManager.DOWNLOAD_COMPLETE_CACHE);
				throw new InterruptedException();
			}

			connect();

		} catch (InterruptedException e1) {
			// Does nothing
		} catch (NullPointerException e1) {
			// Item deleted while updating
		} finally {

			if (cachedUpdate == false) {
				if (mResponse != null) {
					handleState(TrackingManager.DOWNLOAD_COMPLETE);
				}
				handleState(TrackingManager.DOWNLOAD_FAILED);
			}

			synchronized (sTrackingManager) {
				mCurrentThread = null;
			}

			// Clears the Thread's interrupt flag
			Thread.interrupted();
		}
	}

	private void connect() throws NullPointerException {
		if (mItemWeakReference.get().getCourier() == ItemManager.TrackingItem.COURIER_UNKNOWN) {
			// get courier, get hash
			handleState(TrackingManager.SEARCHING_COURIER);
			findCourier();
		} else {
			connectCourier(mItemWeakReference.get().getCourier(), mItemWeakReference.get().getHash());
		}

	}
	
	private void bringToTop(ArrayList<Integer> list, Integer number){
		if (list.indexOf(number) > 0){
			//number exists
			list.remove(number);
			list.add(0,number);
		}
	}

	private void findCourier() throws NullPointerException {
		String tracking_number = mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US);
		
		//build search preferences based on tracking number
		ArrayList<Integer> courier_list = new ArrayList<Integer>();
		courier_list.add(TrackingItem.COURIER_GLOBAL_POSTAL);
		courier_list.add(TrackingItem.COURIER_DHL);
		courier_list.add(TrackingItem.COURIER_UPS);
		courier_list.add(TrackingItem.COURIER_FEDEX);
		courier_list.add(TrackingItem.COURIER_TNT);
		courier_list.add(TrackingItem.COURIER_GLS);
		courier_list.add(TrackingItem.COURIER_ARAMEX);
		courier_list.add(TrackingItem.COURIER_DPD);
		courier_list.add(TrackingItem.COURIER_ESHIPPER);

		//bring courier to front of the list, if number matches
		if (tracking_number.matches(TrackingItem.COURIER_GLOBAL_POSTAL_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_GLOBAL_POSTAL);
			courier_list.remove((Integer)TrackingItem.COURIER_DHL);
			courier_list.remove((Integer)TrackingItem.COURIER_UPS);      
			courier_list.remove((Integer)TrackingItem.COURIER_FEDEX);     
			courier_list.remove((Integer)TrackingItem.COURIER_ARAMEX);   
			courier_list.remove((Integer)TrackingItem.COURIER_DPD);      
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_DHL_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_DHL);
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_UPS_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_UPS);
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_FEDEX_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_FEDEX); 
			courier_list.remove((Integer)TrackingItem.COURIER_TNT);  
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_TNT_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_TNT);
			courier_list.remove((Integer)TrackingItem.COURIER_GLS);  
			courier_list.remove((Integer)TrackingItem.COURIER_ARAMEX);   
			courier_list.remove((Integer)TrackingItem.COURIER_DPD);      
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_GLS_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_GLS);
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_ARAMEX_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_ARAMEX);
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_DPD_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_DPD);
			courier_list.remove((Integer)TrackingItem.COURIER_ESHIPPER); 
		}
		if (tracking_number.matches(TrackingItem.COURIER_ESHIPPER_CODE)){
			bringToTop(courier_list, TrackingItem.COURIER_ESHIPPER);
		}
		//iterate in sorted list
		for (Integer i : courier_list/*int i = 1 + 100000; i <= ItemManager.TrackingItem.COURIER_NUMBER + 100000; i++*/) {
//			switch (i) {
//			case 100001:
//				if (tracking_number.matches(TrackingItem.COURIER_GLOBAL_POSTAL_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_DHL:
//				if (tracking_number.matches(TrackingItem.COURIER_DHL_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_UPS:
//				if (tracking_number.matches(TrackingItem.COURIER_UPS_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_FEDEX:
//				if (tracking_number.matches(TrackingItem.COURIER_FEDEX_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_TNT:
//				if (tracking_number.matches(TrackingItem.COURIER_TNT_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_GLS:
//				if (tracking_number.matches(TrackingItem.COURIER_GLS_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_ARAMEX:
//				if (tracking_number.matches(TrackingItem.COURIER_ARAMEX_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_DPD:
//				if (tracking_number.matches(TrackingItem.COURIER_DPD_CODE) == false)
//					continue;
//				break;
//			case TrackingItem.COURIER_ESHIPPER:
//				if (tracking_number.matches(TrackingItem.COURIER_ESHIPPER_CODE) == false)
//					continue;
//				break;
//			default:
//				break;
//			}

			if (connectCourier(i, "") == true) {
				mItemWeakReference.get().setCourier(i);
				return;
			}
		}
		downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_TRACKING_NUMBER);
	}

	private boolean connectCourier(int courier, String hash) throws NullPointerException {
		String url = mTrackingURL;
		hash = "deprecated";
		int random_number = (int)(Math.random()*999999);
		long timestamp = new Date().getTime();
		if (courier == 100001 || courier == TrackingItem.COURIER_GLOBAL_POSTAL) {
			url += "/r/HandlerTrack.ashx?callback=jQuery"+ random_number + "_" + timestamp;
			url += "&num=" + mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US) + "&pt=0&cm=0&cc=0&_="+ (timestamp + 1);
		} else {
			url += "/r/HandlerTrack.ashx?callback=jQuery11"+ random_number + "_" + timestamp +"&et=" + (courier - 1);
			url += "&num=" + mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US) + "&_="+ (timestamp + 1);
		}

		String content = "";
		try {
			final HttpParams httpParams = new BasicHttpParams();

			// 100 seconds timeout
			HttpConnectionParams.setConnectionTimeout(httpParams, 100 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 100 * 1000);

			HttpClient mHttpclient = new DefaultHttpClient(httpParams);

			//HttpProtocolParams.setUserAgent(mHttpclient.getParams(), "Mozilla/5.0 Firefox/26.0");
			
			//mHttpclient.getParams().setParameter("http.useragent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			mHttpGet = new HttpGet(url);
			//mHttpGet.setHeader("User-Agent", "MySuperUserAgent");
			HttpResponse response = mHttpclient.execute(mHttpGet);
			content = EntityUtils.toString(response.getEntity());
			content = content.substring(content.indexOf('(') + 1, content.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			mResponse = null;
			if (e instanceof ConnectTimeoutException) {
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_CONNECTION_TIMEOUT);
			} else if (e instanceof NoHttpResponseException) {
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_NO_HTTP_RESPONSE);
			} else if (e instanceof IOException) {
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_INTERNET);
			} else {
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_INTERNET);
			}
			return false;
		}
		mItemWeakReference.get().setlastQuery(new Date());

		try {
			mResponse = new JSONObject(content);
			int ret = mResponse.getInt("ret");
			switch (ret) {
			case 1:
				// Ok
				break;
			case -1:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_ILLEGAL_1);
				return false;
			case -2:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_ILLEGAL_2);
				return false;
			case -3:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_SYSTEM_UPDATING);
				return false;
			case -4:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_ILLEGAL_4);
				return false;
			case -5:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_TRACKING_TOO_OFTEN);
				return false;
			case -6:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_WEBSITE);
				return false;
			case -7:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_TRACKING_NUMBER);
				return false;
			case -8:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_CAPTCHA);
				return false;
			case -9:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_WEB_PROXY);
				return false;
			default:
				downloadFailed(ItemManager.TrackingItem.STATUS_ERROR_UNKNOWN);
				return false;
			}

			JSONObject data = mResponse.getJSONObject("dat");
			int e = data.getInt("e");
			//int f = data.getInt("f");

			if (e == 0) {
				// "Not Found"
				mResponse = null;
				if (mItemWeakReference.get().getCourier() != ItemManager.TrackingItem.COURIER_UNKNOWN) {
					// has courier
					mLastErrorStatus = ItemManager.TrackingItem.STATUS_ERROR_TRACKING_NUMBER;
				}
				return false;
			}

			delivered = e;
			stateOrigin = data.getInt("is1");
			timeOrigin = data.getInt("ygt1");
			if (courier == 100001 || courier == TrackingItem.COURIER_GLOBAL_POSTAL) {// global postal
				// get more info
				stateDestination = data.getInt("is2");
				timeDestination = data.getInt("ygt2");
			}

			status = new ArrayList<StatusPair>();
			
			try {
				JSONArray status_origin_json = data.getJSONArray("z2");
				for (int i = 0; i < status_origin_json.length(); i++) {
					JSONObject pair = status_origin_json.getJSONObject(i);
					StatusPair entry = new StatusPair();
					
					ArrayList<String> array = new ArrayList<String>();
					
					try{		
						array.add(pair.getString("b"));
					}catch (Exception e2){}
					try{			
						array.add(pair.getString("c"));
					}catch (Exception e2){}
					try{			
						array.add(pair.getString("d"));
					}catch (Exception e2){}
					try{			
						array.add(pair.getString("z"));
					}catch (Exception e2){};
					
					for (int j = 0; j < array.size(); j++) {
						if (array.get(j) == null || array.get(j).compareTo("null") == 0 || array.get(j).length() == 0){
							array.remove(j);
							j --;
						}
					}

					entry.mTime = pair.getString("a");
					entry.mStatus = TextUtils.join(",  ",array.toArray());
					status.add(entry);
				}
			} catch (Exception e1) {
				// no info
			}
			JSONArray status_destination_json = data.getJSONArray("z1");
			for (int i = 0; i < status_destination_json.length(); i++) {
				JSONObject pair = status_destination_json.getJSONObject(i);
				StatusPair entry = new StatusPair();
				
				ArrayList<String> array = new ArrayList<String>();
				
				try{		
					array.add(pair.getString("b"));
				}catch (Exception e2){}
				try{			
					array.add(pair.getString("c"));
				}catch (Exception e2){}
				try{			
					array.add(pair.getString("d"));
				}catch (Exception e2){}
				try{			
					array.add(pair.getString("z"));
				}catch (Exception e2){};
				
				for (int j = 0; j < array.size(); j++) {
					if (array.get(j) == null || array.get(j).compareTo("null") == 0 || array.get(j).length() == 0){
						array.remove(j);
						j --;
					}
				}

				entry.mTime = pair.getString("a");
				entry.mStatus = TextUtils.join(",  ",array.toArray());
				
				status.add(entry);
			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
			lastdateDate = new Date(0);
			
			try{
				JSONObject lastStatus_json = data.getJSONObject("z0");
				ArrayList<String> array = new ArrayList<String>();
				
				try{		
					array.add(lastStatus_json.getString("b"));
				}catch (Exception e2){}
				try{			
					array.add(lastStatus_json.getString("c"));
				}catch (Exception e2){}
				try{			
					array.add(lastStatus_json.getString("d"));
				}catch (Exception e2){}
				try{			
					array.add(lastStatus_json.getString("z"));
				}catch (Exception e2){};
				
				for (int j = 0; j < array.size(); j++) {
					if (array.get(j) == null || array.get(j).compareTo("null") == 0 || array.get(j).length() == 0){
						array.remove(j);
						j --;
					}
				}

				lastStatus = TextUtils.join(",  ",array.toArray());
				
				String lastDate = lastStatus_json.getString("a");	
				lastdateDate = format.parse(lastDate);			
			}catch (Exception e2){
				//no last status
			}
			firstdateDate = new Date(0);
			try {
				firstdateDate = format.parse(status.get(status.size() - 1).mTime);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

		} catch (JSONException e) {
			// server closed connection
			mResponse = null;
			return false;
		}

		mItemWeakReference.get().setHash(hash);

		// done
		return true;
	}

	private String getItemHash(int courierType) {
		if (mItemWeakReference.get().hasHash() == false) {
			// **Code**
			Context cx = JavaScriptInterpreter.getContext();
			try {
				Scriptable scope = cx.initStandardObjects();

				cx.evaluateString(scope, MyApplication.getScript(), "code", 1, null);

				Function fct = (Function) scope.get("hs", scope);
				Object result = fct.call(cx, scope, scope, new Object[] { mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US), courierType });
				return Context.jsToJava(result, String.class).toString();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Context.exit();
			}
		}
		return "";
	}

	public void abortConnection() {
		if (mHttpGet != null) {
			mHttpGet.abort();
		}
	}

	// Delegates handling the current state of the task to the PhotoManager
	// object
	private void handleState(int state) {
		sTrackingManager.handleState(this, state);
	}

	public void recycle() {
		// Deletes the weak reference
		if (null != mItemWeakReference) {
			mItemWeakReference.clear();
			mItemWeakReference = null;
		}
		mHttpGet = null;
	}

	private void downloadFailed(int error) {
		mLastErrorStatus = error;
		handleState(TrackingManager.DOWNLOAD_FAILED);
	}

	public JSONObject getResponse() {
		return mResponse;
	}

	public int getErrorStatus() {
		return mLastErrorStatus;
	}
}
