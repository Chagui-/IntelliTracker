package com.andrespenaloza.intellitracker.factory;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.objects.ItemManager.Label;

public class LabelFactory {
	public static interface OnLabelButtonClickListener {
		public void onlabelButtonClicked(LabelView lv);
	}

	public static class LabelView extends LinearLayout {

		OnLabelButtonClickListener mListener;

		public LabelView(Context context) {
			super(context);
		}

		public LabelView(Context context, String name, int backgroundColor, int textColor, boolean withButton) {
			super(context);
			init(context, name, backgroundColor, textColor, withButton);
		}

		private void init(Context context, String name, int backgroundColor, int textColor, boolean withButton) {
			View view = View.inflate(context, R.layout.label, this);
			TextView labelNameTv = (TextView) view.findViewById(R.id.labelName);
			View backgroundView = (View) view.findViewById(R.id.background);
			ImageButton removeLabelBt = (ImageButton) view.findViewById(R.id.removeLabel);
			if (withButton) {
				// removeLabelBt.setTag(view);
				removeLabelBt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// View labelView = (View) v.getTag();
						LabelView.this.setVisibility(View.GONE);// TODO is it ok
																// to not delete
																// it?
						if (mListener != null)
							mListener.onlabelButtonClicked(LabelView.this);
					}
				});
			} else {
				removeLabelBt.setVisibility(View.GONE);
				labelNameTv.setTextSize(11);
			}

			labelNameTv.setText(name);
			labelNameTv.setTextColor(textColor);

			GradientDrawable background = (GradientDrawable) backgroundView.getBackground();
			background.setColor(backgroundColor);
		}

		public void setOnLabelButtonClickedListener(OnLabelButtonClickListener listener) {
			mListener = listener;
		}
	}

	public static class LabelColor {
		public String mText;
		public int mTextColor;
		public int mBackgroundColor;

		public LabelColor(String text, int backgroundColor, int textColor) {
			mText = text;
			mBackgroundColor = backgroundColor;
			mTextColor = textColor;
		}
	}

	static public ArrayList<LabelColor> LABEL_COLORS = new ArrayList<LabelFactory.LabelColor>();

	static public LabelView createLabelView(Context context, String name, int backgroundColor, int textColor, boolean withButton) {
		LabelView view = new LabelView(context, name, backgroundColor, textColor, withButton);
		return view;
	}

	static public void createLabel(final LinearLayout layoutForLabels, final ArrayList<Label> labelsToAdd, final ArrayList<Label> labelsToRemove, Label label, Context context) {
		LabelView labelView = LabelFactory
				.createLabelView(context, label.getName(), label.getColor().mBackgroundColor, label.getColor().mTextColor, true);
		labelView.setTag(label);
		labelView.setOnLabelButtonClickedListener(new OnLabelButtonClickListener() {
			@Override
			public void onlabelButtonClicked(LabelView lv) {
				Label label = (Label) lv.getTag();
				labelsToRemove.add(label);
			}
		});
		layoutForLabels.addView(labelView, 0);

		labelsToAdd.add(label);
	}
}
