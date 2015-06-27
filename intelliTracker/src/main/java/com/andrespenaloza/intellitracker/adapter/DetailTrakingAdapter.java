package com.andrespenaloza.intellitracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.objects.TrackingItem;
import com.andrespenaloza.intellitracker.objects.TrackingItem.StatusPair;

import java.util.ArrayList;

public class DetailTrakingAdapter extends BaseAdapter {
	static class ViewHolder {
		public TextView time;
		public TextView status;
		public TextView days;
	}

	ArrayList<StatusPair> mPair;
	TrackingItem mItem;
	LayoutInflater mInflater;

	public DetailTrakingAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPair = new ArrayList<StatusPair>();
	}

	public void setItems(ArrayList<StatusPair> pairs, TrackingItem item) {
		mPair = pairs;
		mItem = item;
	}

	@Override
	public int getCount() {
		return mPair.size();
	}

	@Override
	public Object getItem(int position) {
		return mPair.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			rowView = mInflater.inflate(R.layout.listview_item_package_detail, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);
			viewHolder.status = (TextView) rowView.findViewById(R.id.status);
			viewHolder.days = (TextView) rowView.findViewById(R.id.days);
			rowView.setTag(viewHolder);
		}
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();		
		StatusPair pair = mPair.get(position);
		holder.time.setText(pair.mTime);
		holder.status.setText(pair.mStatus);
		holder.days.setText("" + mItem.getDaysSince(pair.mTime));

		if (position % 2 == 0) {
			rowView.setBackgroundResource(R.drawable.selector_even_list);
		} else {
			rowView.setBackgroundResource(R.drawable.selector_odd_list);
		}

		return rowView;
	}

}
