package com.andrespenaloza.intellitracker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;

public class ColorPickerAdapter extends BaseAdapter {
	static class ViewHolder {
		public TextView text;
	}
	ArrayList<LabelColor> mLabelColors;
	LayoutInflater mInflater;
	
	public ColorPickerAdapter(Context context, ArrayList<LabelColor> labelColors){
		mLabelColors = labelColors;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mLabelColors.size();
	}

	@Override
	public Object getItem(int index) {
		return mLabelColors.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup paren) {
		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			rowView = mInflater.inflate(R.layout.color_pick_element, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.colorText);
			rowView.setTag(viewHolder);
		}
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();		
		LabelColor labelColor = mLabelColors.get(position);
		holder.text.setText(labelColor.mText);
		holder.text.setTextColor(labelColor.mTextColor);
		rowView.setBackgroundColor(labelColor.mBackgroundColor);

		return rowView;
	}

}
