package com.andrespenaloza.intellitracker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.factory.LabelFactory;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelView;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.ItemManager.Label;
import com.andrespenaloza.intellitracker.objects.ItemManager.TrackingItem;

public class PackageTrakingAdapter extends BaseAdapter {
	static class ViewHolder {
		public TextView name;
		public TextView trackingNumber;
		public TextView status;
		public TextView statusServer;
		public TextView packageStatus;
		public TextView time;
		public View background;
		public ImageButton more;
		public LinearLayout labelsHolder;
	}

	public interface onMoreItemClickListener {
		public void onMoreItemClick(View view, TrackingItem item, int id);
	}

	public void setOnMoreItemClickListener(onMoreItemClickListener listener) {
		mListenerMore = listener;
	}

	private onMoreItemClickListener mListenerMore;

	public interface onCustomItemClickListener {
		public void onCustomItemClick(View view, int position, TrackingItem item);
	}

	public void setOnCustomItemClickListener(onCustomItemClickListener listener) {
		mListener = listener;
	}

	private onCustomItemClickListener mListener;

	ArrayList<TrackingItem> mItems;
	LayoutInflater mInflater;

	private Context mContext;

	public PackageTrakingAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems = new ArrayList<TrackingItem>();
	}

	public void setItems(ArrayList<TrackingItem> items) {
		mItems = items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		final TrackingItem item = mItems.get(position);
		// reuse views
		final int pos = position;
		if (rowView == null) {
			rowView = mInflater.inflate(R.layout.listview_item_package, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.trackingNumber = (TextView) rowView.findViewById(R.id.number);
			viewHolder.status = (TextView) rowView.findViewById(R.id.status);
			viewHolder.statusServer = (TextView) rowView.findViewById(R.id.statusServer);
			viewHolder.packageStatus = (TextView) rowView.findViewById(R.id.packageStatus);
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);
			viewHolder.more = (ImageButton) rowView.findViewById(R.id.more);
			viewHolder.background = (View) rowView.findViewById(R.id.background);
			viewHolder.labelsHolder = (LinearLayout) rowView.findViewById(R.id.labelHolder);

			rowView.setClickable(true);
			rowView.setFocusable(true);

			rowView.setTag(viewHolder);
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.labelsHolder.removeAllViews();
		for (int i = 0; i < item.getLabels().size(); i++) {
			Label label = item.getLabels().get(i);
			if (label.getName().compareTo(ItemManager.LABEL_CATEGORY_NONE) == 0 || label.getName().compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) == 0)
				continue;

			LabelView labelView = LabelFactory
					.createLabelView(mContext, label.getName(), label.getColor().mBackgroundColor, label.getColor().mTextColor, false);

			holder.labelsHolder.addView(labelView, 0);
		}

		// fill data
		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.onCustomItemClick(v, pos, item);
			}

		});
		holder.more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Creating the instance of PopupMenu
				PopupMenu popup = new PopupMenu(mContext, v);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.list_dropdown_more, popup.getMenu());
				if (item.isArchive()) {
					popup.getMenu().removeItem(R.id.archive);
				} else {
					popup.getMenu().removeItem(R.id.unArchive);
				}

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem menuItem) {
						if (mListenerMore == null)
							return false;
						mListenerMore.onMoreItemClick(v, item, menuItem.getItemId());
						return true;
					}
				});

				popup.show();// showing popup menu
			}
		});
		holder.time.setText("" + item.getDaysInTransit() + " days");
		holder.name.setText(item.getName());
		holder.trackingNumber.setText(item.getTrackingNumber());
		holder.status.setText(item.getStatus());
		if (item.getStatusServer().equals(TrackingItem.STATUS_SERVER_STATE_STRING_NORMAL_TRACKING)){
			holder.statusServer.setVisibility(View.GONE);
		}else{
			holder.statusServer.setVisibility(View.VISIBLE);
		}
		holder.statusServer.setText(item.getStatusServer());
		int packageStatusOverride = item.getPackageStatusOverride();
		if (item.isPackageStatusOverride()) {
			switch (packageStatusOverride) {
			case TrackingItem.PACKAGE_STATUS_DELIVERED:
				holder.background.setBackgroundResource(R.drawable.selector_delivered_list);
				break;
			case TrackingItem.PACKAGE_STATUS_CLAIMED:
				holder.background.setBackgroundResource(R.drawable.selector_delivered_list);
				break;
			case TrackingItem.PACKAGE_STATUS_IN_CUSTOMS:
				holder.background.setBackgroundResource(R.drawable.selector_other_list);
				break;
			case TrackingItem.PACKAGE_STATUS_IN_TRANSIT:
				if (position % 2 == 0) {
					holder.background.setBackgroundResource(R.drawable.selector_even_list);
				} else {
					holder.background.setBackgroundResource(R.drawable.selector_odd_list);
				}
				break;
			case TrackingItem.PACKAGE_STATUS_LOST:
				holder.background.setBackgroundResource(R.drawable.selector_lost_list);
				break;
			case TrackingItem.PACKAGE_STATUS_WAITING_FOR_PICKUP:
				holder.background.setBackgroundResource(R.drawable.selector_other_list);
				break;
			default:
				break;
			}
			holder.packageStatus.setText(item.getPackageStatusOverrideText());
		} else {
			if (item.getPackageStatus() == TrackingItem.PACKAGE_STATUS_DELIVERED) {
				holder.background.setBackgroundResource(R.drawable.selector_delivered_list);
			} else {
				if (position % 2 == 0) {
					holder.background.setBackgroundResource(R.drawable.selector_even_list);
				} else {
					holder.background.setBackgroundResource(R.drawable.selector_odd_list);
				}
			}
			holder.packageStatus.setText(item.getPackageStatusText());
		}
		if (position % 2 == 0) {
			rowView.setBackgroundResource(R.drawable.selector_even_list);
		} else {
			rowView.setBackgroundResource(R.drawable.selector_odd_list);
		}
		return rowView;
	}

}
