package com.andrespenaloza.intellitracker.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.adapter.DetailTrakingAdapter;
import com.andrespenaloza.intellitracker.connection.TrackingManager;
import com.andrespenaloza.intellitracker.connection.TrackingManager.TrackingListener;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.TrackingItem;

public class ItemDetailFragment extends Fragment implements TrackingListener {
	public static final String ARG_ITEM_ID = "item_id";
	private static TrackingItem mItem;
	private ListView mListPackageTracking;
	private TextView mStatusTv, mStatusServerTv;
	private static DetailTrakingAdapter mAdapter;

	public ItemDetailFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		TrackingManager.getInstance().addTrackingListener(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		TrackingManager.getInstance().removeTrackingListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new DetailTrakingAdapter(getActivity());
		setHasOptionsMenu(true);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			try {
				mItem = ItemManager.getTrackingItem(getArguments().getInt(ARG_ITEM_ID));
			} catch (Exception e) {
				// No item to display
				mItem = null;
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

		TextView name_tv = (TextView) rootView.findViewById(R.id.name);
		TextView tracking_tv = (TextView) rootView.findViewById(R.id.tracking);
		mStatusTv = (TextView) rootView.findViewById(R.id.status);
		mStatusServerTv = (TextView) rootView.findViewById(R.id.statusServer);

		if (mItem != null) {
			mAdapter.setItems(mItem.getStatusList(), mItem);

			name_tv.setText(mItem.getName());
			tracking_tv.setText(mItem.getTrackingNumber());

			if (mItem.isPackageStatusOverride())
				mStatusTv.setText(mItem.getPackageStatusOverrideText());
			else
				mStatusTv.setText(mItem.getPackageStatusText());

			mStatusServerTv.setText(mItem.getStatusServer());
			if (mItem.getStatusServer().equals(TrackingItem.STATUS_SERVER_STATE_STRING_NORMAL_TRACKING)){
				mStatusServerTv.setVisibility(View.GONE);
			}else{
				mStatusServerTv.setVisibility(View.VISIBLE);
			}
			
			mListPackageTracking = (ListView) rootView.findViewById(R.id.detailTrakingList);
			mListPackageTracking.setAdapter(mAdapter);
		} else {
			name_tv.setText("");
			tracking_tv.setText("");
			mStatusTv.setText("");
		}

		return rootView;
	}

	@Override
	public void onItemUpdated(TrackingItem item) {
		if (item.getId() != mItem.getId()) // another item is updated
			return;
		
		mStatusServerTv.setText(mItem.getStatusServer());
		if (mItem.getStatusServer().equals(TrackingItem.STATUS_SERVER_STATE_STRING_NORMAL_TRACKING)){
			mStatusServerTv.setVisibility(View.GONE);
		}else{
			mStatusServerTv.setVisibility(View.VISIBLE);
		}
		if (mItem.isPackageStatusOverride())
			mStatusTv.setText(mItem.getPackageStatusOverrideText());
		else
			mStatusTv.setText(mItem.getPackageStatusText());

		mAdapter.notifyDataSetChanged();
	}

	public static void updateTrackOnline() {
		TrackingManager.startDownload(mItem);
	}
}
