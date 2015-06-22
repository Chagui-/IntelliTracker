package com.andrespenaloza.intellitracker.connection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.andrespenaloza.intellitracker.MyApplication;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.ItemManager.TrackingItem;
import com.andrespenaloza.intellitracker.objects.ItemManager.TrackingItem.StatusPair;
import com.andrespenaloza.intellitracker.objects.JavaScriptInterpreter;

public class TrackingManager {
	public interface TrackingListener {
		public void onItemUpdated(TrackingItem item);
	}

	private ArrayList<TrackingListener> mListeners = new ArrayList<TrackingListener>();

	public void addTrackingListener(TrackingListener listener) {
		mListeners.add(listener);
	}

	public void removeTrackingListener(TrackingListener listener) {
		mListeners.remove(listener);
	}

	private void notifyListeners(TrackingItem item) {
		for (int i = 0; i < mListeners.size(); i++) {
			mListeners.get(i).onItemUpdated(item);
		}
	}

	private static TrackingManager sInstance = null;
	private static android.content.Context mContext;
	static {// The time unit for "keep alive" is in seconds
		KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
		sInstance = new TrackingManager();
	}

	static final int DOWNLOAD_FAILED = -1;
	static final int DOWNLOAD_STARTED = 1;
	static final int DOWNLOAD_COMPLETE = 2;
	static final int SEARCHING_COURIER = 3;
	static final int DOWNLOAD_COMPLETE_CACHE = 4;
	// Sets the amount of time an idle thread will wait for a task before
	// terminating
	private static final int KEEP_ALIVE_TIME = 1;
	// Sets the Time Unit to seconds
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
	// Sets the initial threadpool size to 8
	private static final int CORE_POOL_SIZE = 4;
	// Sets the maximum threadpool size to 8
	private static final int MAXIMUM_POOL_SIZE = 4;

	private final BlockingQueue<Runnable> mUpdateWorkQueue;
	private final ThreadPoolExecutor mUpdatePool;
	private final Queue<TrackingTask> mConnectionTaskWorkQueue;

	static class MyHandler extends Handler {
		static TrackingManager sTrackingManager;

		public MyHandler(Looper looper, TrackingManager trackingManager) {
			super(looper);
			sTrackingManager = trackingManager;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message inputMessage) {

			// Gets the image task from the incoming Message object.
			TrackingTask trackingTask = (TrackingTask) inputMessage.obj;

			// Sets an PhotoView that's a weak reference to the
			// input ImageView
			TrackingItem item = trackingTask.getTrackingItem();

			// If this input view isn't null
			if (item != null) {
				switch (inputMessage.what) {
				case DOWNLOAD_STARTED:
					break;
				case DOWNLOAD_COMPLETE_CACHE:
					item.setStatus(TrackingItem.STATUS_IDLE);
					sTrackingManager.notifyListeners(item);
					break;
				case SEARCHING_COURIER:
					item.setStatus(TrackingItem.STATUS_SEARCHING_COURIER);
					sTrackingManager.notifyListeners(item);
					break;
				case DOWNLOAD_COMPLETE:
					// check if it was updated
					if (item.getLastUpdated().compareTo(trackingTask.getLastdateDate()) < 0) {
						ItemManager.popItem(item);
						sTrackingManager.notifyListeners(item);
						Toast.makeText(mContext, "Updated: " + item.getName(), Toast.LENGTH_LONG).show();
					}
					
					int package_status = 0;
					if (trackingTask.getDelivered() == 40)
						package_status = TrackingItem.PACKAGE_STATUS_DELIVERED;
					else if (trackingTask.getDelivered() == 30)
						package_status = TrackingItem.PACKAGE_STATUS_WAITING_FOR_PICKUP;
					else if (trackingTask.getDelivered() == 10)
						package_status = TrackingItem.PACKAGE_STATUS_IN_TRANSIT;
					else package_status = TrackingItem.PACKAGE_STATUS_NO_INFO;
					//TODO: add return to sender status

					item.update(trackingTask.getFirstdateDate(), trackingTask.getLastdateDate(), trackingTask.getLastStatus(), trackingTask.getStatus(),
							package_status);
					
					item.setStatus(TrackingItem.STATUS_IDLE,trackingTask.getStateOrigin(),trackingTask.getStateDestination());
					sTrackingManager.notifyListeners(item);

					sTrackingManager.recycleTask(trackingTask);
					break;
				case DOWNLOAD_FAILED:
					int status = trackingTask.getErrorStatus();
					item.setStatus(status);
					sTrackingManager.notifyListeners(item);
					sTrackingManager.recycleTask(trackingTask);
					break;
				default:
					// Otherwise, calls the super method
					super.handleMessage(inputMessage);
				}

			}
		}
	}

	private MyHandler mHandler;

	private TrackingManager() {
		mUpdateWorkQueue = new LinkedBlockingQueue<Runnable>();
		mUpdatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mUpdateWorkQueue);
		mConnectionTaskWorkQueue = new LinkedBlockingQueue<TrackingTask>();

		mHandler = new MyHandler(Looper.getMainLooper(), this);
	}

	public static void setContext(android.content.Context context) {
		mContext = context;
	}

	public static TrackingManager getInstance() {
		return sInstance;
	}

	static public TrackingTask startDownload(TrackingItem item) {
		if (item.isManualMode() || item.getPackageStatus() == ItemManager.TrackingItem.STATUS_UPDATING ||
				item.getPackageStatus() == ItemManager.TrackingItem.STATUS_SEARCHING_COURIER)
			return null;
		/*
		 * Gets a task from the pool of tasks, returning null if the pool is
		 * empty
		 */
		TrackingTask downloadTask = sInstance.mConnectionTaskWorkQueue.poll();

		// If the queue was empty, create a new task instead.
		if (null == downloadTask) {
			downloadTask = new TrackingTask();
		}

		// Initializes the task
		downloadTask.initializeDownloaderTask(sInstance, item);
		sInstance.mUpdatePool.execute(downloadTask);

		item.setStatus(TrackingItem.STATUS_UPDATING);
		sInstance.notifyListeners(item);

		return downloadTask;
	}

	public void recycleTask(TrackingTask downloadTask) {

		// Frees up memory in the task
		downloadTask.recycle();

		// Puts the task object back into the queue for re-use.
		mConnectionTaskWorkQueue.offer(downloadTask);
	}

	public void handleState(TrackingTask trackingTask, int state) {
		mHandler.obtainMessage(state, trackingTask).sendToTarget();
	}

}
