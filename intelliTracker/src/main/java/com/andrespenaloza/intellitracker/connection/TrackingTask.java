package com.andrespenaloza.intellitracker.connection;

import android.net.Uri;
import android.text.TextUtils;

import com.andrespenaloza.intellitracker.objects.Courier.Courier;
import com.andrespenaloza.intellitracker.objects.Courier.GlobalPostal;
import com.andrespenaloza.intellitracker.objects.TrackingItem;
import com.andrespenaloza.intellitracker.objects.TrackingItem.StatusPair;

import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrackingTask implements Runnable {

	private static TrackingManager sTrackingManager;
	private String mTrackingURL;
	private String mUnlockURL;
	private JSONObject mResponse;

	private WeakReference<TrackingItem> mItemWeakReference;
	private Thread mCurrentThread;
	private HttpGet mHttpGet;
	private boolean mUnlock;

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

	void initializeDownloaderTask(TrackingManager trackingManager, TrackingItem item, boolean runUnlock) {
		// Sets this object's ThreadPool field to be the input argument
		sTrackingManager = trackingManager;
		mUnlock = runUnlock;

		stateOrigin = 1;
		stateDestination = 1;

		// Gets the URL for the View
		mTrackingURL = "http://www.17track.net";
		mUnlockURL = "http://www.17track.net/en/result/post-details.shtml?nums=";
		// mTrackingURL = "http://s1.17track.net";

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

			if (!cachedUpdate) {
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
		if (mItemWeakReference.get().getCourierIds().size() > 1) {
			// try to find courier
			handleState(TrackingManager.SEARCHING_COURIER);
			findCourier();
		} else if (mItemWeakReference.get().getCourierIds().size() == 1){
			connectCourier(mItemWeakReference.get().getCourierIds().get(0),true);
		}

	}

	private void findCourier() throws NullPointerException {
		String tracking_number = mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US);
		
		//build search preferences based on tracking number
		ArrayList<Courier> couriers = Courier.getCouriersMatchingTracking(tracking_number);

		for (int c : mItemWeakReference.get().getCourierIds()) {

			if (connectCourier(c,true)) {
				mItemWeakReference.get().setCourierId(c);
				return;
			}
		}
		downloadFailed(TrackingItem.STATUS_ERROR_TRACKING_NUMBER);
	}

	private boolean connectCourier(int courier, boolean retry) throws NullPointerException {
		String url = mTrackingURL;
		long random_number = (long)(Math.random()*99999999999999999999.);
		long timestamp = new Date().getTime();

		if (courier == new GlobalPostal().getCourierId()) {
			url += "/r/HandlerTrack.ashx?callback=jQuery"+ random_number + "_" + timestamp;
			url += "&num=" + mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US) + "&pt=0&cm=0&cc=0&_="+ (timestamp + 1);
		} else {
			url += "/r/HandlerTrack.ashx?callback=jQuery"+ random_number + "_" + timestamp +"&et=" + (courier);
			url += "&num=" + mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US) + "&_="+ (timestamp + 1);
		}
		if (mUnlock)
			unLockServer(mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US));

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
//			mHttpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//			mHttpGet.setHeader("Host", "www.17track.net");
//			mHttpGet.setHeader("Referer", "http://www.17track.net/en/result/post-details.shtml?nums=" + mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US));
			HttpResponse response = mHttpclient.execute(mHttpGet);
			content = EntityUtils.toString(response.getEntity());
			content = content.substring(content.indexOf('(') + 1, content.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			mResponse = null;
			if (e instanceof ConnectTimeoutException) {
				downloadFailed(TrackingItem.STATUS_ERROR_CONNECTION_TIMEOUT);
			} else if (e instanceof NoHttpResponseException) {
				downloadFailed(TrackingItem.STATUS_ERROR_NO_HTTP_RESPONSE);
			} else if (e instanceof IOException) {
				downloadFailed(TrackingItem.STATUS_ERROR_INTERNET);
			} else {
				downloadFailed(TrackingItem.STATUS_ERROR_INTERNET);
			}
			return false;
		}
		mItemWeakReference.get().setLastQuery(new Date());

		try {
			mResponse = new JSONObject(content);
			int ret = mResponse.getInt("ret");
			switch (ret) {
			case 1:
				// Ok
				break;
			case -1:
				downloadFailed(TrackingItem.STATUS_ERROR_ILLEGAL_1);
				return false;
			case -2:
				downloadFailed(TrackingItem.STATUS_ERROR_ILLEGAL_2);
				return false;
			case -3:
				downloadFailed(TrackingItem.STATUS_ERROR_SYSTEM_UPDATING);
				return false;
			case -4:
				downloadFailed(TrackingItem.STATUS_ERROR_ILLEGAL_4);
				return false;
			case -5:
				downloadFailed(TrackingItem.STATUS_ERROR_TRACKING_TOO_OFTEN);
				return false;
			case -6:
				downloadFailed(TrackingItem.STATUS_ERROR_WEBSITE);
				return false;
			case -7:
				downloadFailed(TrackingItem.STATUS_ERROR_TRACKING_NUMBER);
				return false;
			case -8:
				if (retry){
					unLockServer(mItemWeakReference.get().getTrackingNumber().toUpperCase(Locale.US));
					return connectCourier(courier,false);
				}
				downloadFailed(TrackingItem.STATUS_ERROR_CAPTCHA);
				return false;
			case -9:
				downloadFailed(TrackingItem.STATUS_ERROR_WEB_PROXY);
				return false;
			default:
				downloadFailed(TrackingItem.STATUS_ERROR_UNKNOWN);
				return false;
			}

			JSONObject data = mResponse.getJSONObject("dat");
			int e = data.getInt("e");
			//int f = data.getInt("f");

			if (e == 0) {
				// "Not Found"
				mResponse = null;
				if (mItemWeakReference.get().getCourierIds().size() == 1) {
					// Has courier
					mLastErrorStatus = TrackingItem.STATUS_ERROR_TRACKING_NUMBER;
				}
				return false;
			}else if (e == 20){
                // "Expired"
                // This might not be the correct courier
                mResponse = null;
                mItemWeakReference.get().removeCourierId(courier);
                mLastErrorStatus = TrackingItem.STATUS_ERROR_TRACKING_NUMBER;
                return false;
            }

			delivered = e;
			stateOrigin = data.getInt("is1");
			timeOrigin = data.getInt("ygt1");
			if (courier == new GlobalPostal().getCourierId()) {// global postal
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
					}catch (Exception ignored){}
					try{
						array.add(pair.getString("c"));
					}catch (Exception ignored){}
					try{			
						array.add(pair.getString("d"));
					}catch (Exception ignored){}
					try{			
						array.add(pair.getString("z"));
					}catch (Exception ignored){}
					
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
				}catch (Exception ignored){}
				try{			
					array.add(pair.getString("c"));
				}catch (Exception ignored){}
				try{			
					array.add(pair.getString("d"));
				}catch (Exception ignored){}
				try{			
					array.add(pair.getString("z"));
				}catch (Exception ignored){}
				
				for (int j = 0; j < array.size(); j++) {
					if (array.get(j) == null || array.get(j).compareTo("null") == 0 || array.get(j).length() == 0){
						array.remove(j);
						j --;
					}
				}

				entry.mTime = pair.getString("a");
				entry.mStatus = TextUtils.join(", ",array.toArray());
				
				status.add(entry);
			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
			lastdateDate = new Date(0);
			
			try{
				JSONObject lastStatus_json = data.getJSONObject("z0");
				ArrayList<String> array = new ArrayList<String>();
				
				try{		
					array.add(lastStatus_json.getString("b"));
				}catch (Exception ignored){}
				try{			
					array.add(lastStatus_json.getString("c"));
				}catch (Exception ignored){}
				try{			
					array.add(lastStatus_json.getString("d"));
				}catch (Exception ignored){}
				try{			
					array.add(lastStatus_json.getString("z"));
				}catch (Exception ignored){}
				
				for (int j = 0; j < array.size(); j++) {
					if (array.get(j) == null || array.get(j).compareTo("null") == 0 || array.get(j).length() == 0){
						array.remove(j);
						j --;
					}
				}

				lastStatus = TextUtils.join(", ",array.toArray());
				
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

		// done
		return true;
	}

	private void unLockServer(String trackingNumber) {
		try {
			final HttpParams httpParams = new BasicHttpParams();

			// 100 seconds timeout
			HttpConnectionParams.setConnectionTimeout(httpParams, 100 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 100 * 1000);

			HttpClient mHttpclient = new DefaultHttpClient(httpParams);
			mHttpGet = new HttpGet(mUnlockURL + trackingNumber);
			HttpResponse response = mHttpclient.execute(mHttpGet);
		} catch (Exception e) {
			e.printStackTrace();
			mResponse = null;
			if (e instanceof ConnectTimeoutException) {
				downloadFailed(TrackingItem.STATUS_ERROR_CONNECTION_TIMEOUT);
			} else if (e instanceof NoHttpResponseException) {
				downloadFailed(TrackingItem.STATUS_ERROR_NO_HTTP_RESPONSE);
			} else if (e instanceof IOException) {
				downloadFailed(TrackingItem.STATUS_ERROR_INTERNET);
			} else {
				downloadFailed(TrackingItem.STATUS_ERROR_INTERNET);
			}
		}
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
