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
import android.widget.PopupMenu;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.ItemManager.Label;

public class DrawerMenuAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Label> mLabels;
	LayoutInflater mInflater;

	static class ViewHolder {
		public TextView text;
		public ImageButton more;
	}

	public interface onMoreItemClickListener {
		public void onMoreItemClick(View view, Label label, int id);
	}

	public interface onCustomItemClickListener {
		public void onCustomItemClick(View view, int position, Label label);
	}

	public void setOnMoreItemClickListener(onMoreItemClickListener listener) {
		mListenerMore = listener;
	}

	public void setOnCustomItemClickListener(onCustomItemClickListener listener) {
		mListener = listener;
	}

	private onCustomItemClickListener mListener;
	private onMoreItemClickListener mListenerMore;

	public DrawerMenuAdapter(Context context, ArrayList<Label> labels) {
		mContext = context;
		mLabels = labels;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setLabels(ArrayList<Label> labels) {
		mLabels = labels;
	}

	@Override
	public int getCount() {
		return mLabels.size();
	}

	@Override
	public Object getItem(int position) {
		return mLabels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			rowView = mInflater.inflate(R.layout.drawer_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.text);
			viewHolder.more = (ImageButton) rowView.findViewById(R.id.more);
			rowView.setTag(viewHolder);
		}
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		rowView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.onCustomItemClick(v, position, mLabels.get(position));
			}
		});
		rowView.setBackgroundResource(R.drawable.selector_odd_list);
		
		final Label label = mLabels.get(position);
		
		if (label.getName().compareTo(ItemManager.LABEL_CATEGORY_NONE) == 0 || label.getName().compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) == 0){
			holder.more.setVisibility(View.GONE);
		}	
		
		holder.text.setText(label.getName());
		LabelColor labelColor = label.getColor();
		holder.text.setTextColor(labelColor.mTextColor);
		rowView.setBackgroundColor(labelColor.mBackgroundColor);
		holder.more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				// Creating the instance of PopupMenu
				PopupMenu popup = new PopupMenu(mContext, v);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.list_dropdown_more_drawer, popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem menuItem) {
						if (mListenerMore == null)
							return false;
						mListenerMore.onMoreItemClick(v, label, menuItem.getItemId());
						return true;
					}
				});

				popup.show();// showing popup menu
			}
		});

		return rowView;
	}

}
