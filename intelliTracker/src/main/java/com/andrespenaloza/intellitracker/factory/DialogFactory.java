package com.andrespenaloza.intellitracker.factory;

import android.app.Dialog;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.adapter.ColorPickerAdapter;
import com.andrespenaloza.intellitracker.factory.LabelFactory.LabelColor;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.Label;
import com.andrespenaloza.intellitracker.objects.TrackingItem;
import com.andrespenaloza.intellitracker.ui.ItemListActivity;

import java.util.ArrayList;

public class DialogFactory {

	static public abstract class EditItemDialog extends Dialog {
		Context mContext;
		protected TrackingItem mItem;
		private String name, tracking;

		protected EditText mNameBox, mTrackingBox;
		protected Button mOkButton, mCancelButton;

		protected ArrayList<Label> mLabelsToAdd = new ArrayList<Label>();
		protected ArrayList<Label> mLabelsToRemove = new ArrayList<Label>();

		public EditItemDialog(Context context, TrackingItem item) {
			super(context);
			mContext = context;
			mItem = item;
			name = item.getName();
			tracking = item.getTrackingNumber();
			build();
			AfterBuild();
		}

		public EditItemDialog(Context context) {
			super(context);
			mContext = context;
			mItem = null;
			name = "";
			tracking = "";
			build();
			AfterBuild();
		}

		public abstract void AfterBuild();

		private void build() {

			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.edit_track_dialog);
			// dialog.setTitle("Edit Entry");

			// set the custom dialog components - text, image and button

			mNameBox = (EditText) findViewById(R.id.name_box);
			mTrackingBox = (EditText) findViewById(R.id.tracking_box);
			mNameBox.setText(name);
			mTrackingBox.setText(tracking);
			mOkButton = (Button) findViewById(R.id.ok);
			mCancelButton = (Button) findViewById(R.id.cancel);
			ImageButton addLabelButton = (ImageButton) findViewById(R.id.add_label);
			final LinearLayout layoutForLabels = (LinearLayout) findViewById(R.id.label_layout);

			mLabelsToAdd = new ArrayList<Label>();
			mLabelsToRemove = new ArrayList<Label>();

			// create label views
			if (mItem != null) {
				for (int i = 0; i < mItem.getLabels().size(); i++) {
					Label label = mItem.getLabels().get(i);
					if (label.getName().compareTo(ItemManager.LABEL_CATEGORY_NONE) == 0 || label.getName().compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) == 0)
						continue;
					LabelFactory.createLabel(layoutForLabels, mLabelsToAdd, mLabelsToRemove, label, mContext);
				}
			}

			mOkButton.setText("Ok");

			addLabelButton.setOnClickListener(new android.view.View.OnClickListener() {
				private int ID_NEW_LABEL = 0;
				private int ID_OTHER_LABEL = 1;

				@Override
				public void onClick(View v) {
					// Creating the instance of PopupMenu
					PopupMenu popup = new PopupMenu(mContext, v);
					// Inflating the Popup using xml file
					popup.getMenu().add(Menu.NONE, ID_NEW_LABEL, 0, "New Category");

					main_loop: for (int i = 0; i < ItemManager.getLabels().size(); i++) {
						Label label = ItemManager.getLabels().get(i);
						// see if its not allready in item
						if (mItem != null) {
							if (mItem.getLabel(label.getName()) != null)
								continue;
						}
						// see if its not allready in mLabelsToAdd
						for (int j = 0; j < mLabelsToAdd.size(); j++) {
							if (label.getName() == mLabelsToAdd.get(j).getName())
								continue main_loop;
						}
						//see ifits not automatic  labels
						if (label.getName().compareTo(ItemManager.LABEL_CATEGORY_NONE) == 0 || label.getName().compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) == 0)
							continue;
						
						popup.getMenu().add(Menu.NONE, ID_OTHER_LABEL, 0, label.getName());
					}

					// registering popup with OnMenuItemClickListener
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						public boolean onMenuItemClick(MenuItem menuItem) {
							if (menuItem.getItemId() == ID_NEW_LABEL) {
								final Dialog dialog = getNewLabelDialog(mContext, layoutForLabels, mLabelsToAdd, mLabelsToRemove);
								dialog.show();
							} else { // add label to list
								Label label = ItemManager.getLabel(menuItem.getTitle().toString());
								LabelFactory.createLabel(layoutForLabels, mLabelsToAdd, mLabelsToRemove, label, mContext);
								//mLabelsToAdd.add(label);
							}
							return true;
						}
					});

					popup.show();// showing popup menu
				}
			});
			mCancelButton.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			mOkButton.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "Ok Clicked", Toast.LENGTH_SHORT).show();
				}
			});
		}

	}

	public static class LabelColorHolder {
		public LabelColor labelColor;

		public LabelColorHolder(LabelColor labelColor) {
			this.labelColor = labelColor;
		}
	}

	private static Dialog GetColorPickDialog(Context context, final Button labelColorButton, View v) {
		// dialog for creating new label
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.color_pick_dialog);
		// dialog.setTitle("New Tracking");

		// set the custom dialog components -
		// text,
		// image and button

		ListView colorLv = (ListView) dialog.findViewById(R.id.colorList);
		colorLv.setAdapter(new ColorPickerAdapter(context, LabelFactory.LABEL_COLORS));

		colorLv.setTag(v.getTag());

		colorLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				LabelColorHolder lch = (LabelColorHolder) ((View) view.getParent()).getTag();

				lch.labelColor = LabelFactory.LABEL_COLORS.get(position);
				labelColorButton.setBackgroundColor(lch.labelColor.mBackgroundColor);
				labelColorButton.setTextColor(lch.labelColor.mTextColor);
				dialog.dismiss();
			}
		});

		return dialog;
	}

	public static Dialog getNewLabelDialog(final Context context, final LinearLayout layoutForLabels, final ArrayList<Label> labelsToAdd,
			final ArrayList<Label> labelsToRemove) {
		// dialog for creating new label
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.add_label_dialog);
		// dialog.setTitle("New Tracking");

		// set the custom dialog components - text,
		// image and button

		final EditText nameBox = (EditText) dialog.findViewById(R.id.name_box);
		Button okButton = (Button) dialog.findViewById(R.id.ok);
		Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
		final Button labelColorButton = (Button) dialog.findViewById(R.id.buttonColor);

		LabelColor labelColor = LabelFactory.LABEL_COLORS.get(0);

		LabelColorHolder lch = new LabelColorHolder(labelColor);

		labelColorButton.setBackgroundColor(labelColor.mBackgroundColor);
		labelColorButton.setTextColor(labelColor.mTextColor);

		labelColorButton.setTag(lch);

		labelColorButton.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog dialog = GetColorPickDialog(context, labelColorButton, v);
				dialog.show();
			}
		});

		cancelButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		okButton.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LabelColorHolder lch = (LabelColorHolder) labelColorButton.getTag();

				Label label = ItemManager.addLabel(nameBox.getText().toString(), lch.labelColor);

				LabelFactory.createLabel(layoutForLabels, labelsToAdd, labelsToRemove, label, context);
				ItemListActivity.updateDrawerAdapter();
				dialog.dismiss();
			}

		});
		return dialog;
	}

	static public abstract class EditLabelDialog extends Dialog {

		Context mContext;
		protected Label mLabel;

		protected EditText mNameBox, mTrackingBox;
		protected Button mOkButton, mCancelButton, mLabelColorButton;
		
		public abstract void AfterBuild();

		public EditLabelDialog(Context context, Label label) {
			super(context);
			mContext = context;
			mLabel = label;
			build();
			AfterBuild();
		}

		private void build() {
			// dialog for creating new label
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.add_label_dialog);

			// set the custom dialog components - text,
			// image and button

			mNameBox = (EditText) findViewById(R.id.name_box);
			mOkButton = (Button) findViewById(R.id.ok);
			mCancelButton = (Button) findViewById(R.id.cancel);
			mLabelColorButton = (Button) findViewById(R.id.buttonColor);

			mNameBox.setText(mLabel.getName());
			mLabelColorButton.setBackgroundColor(mLabel.getColor().mBackgroundColor);
			mLabelColorButton.setTextColor(mLabel.getColor().mTextColor);

			LabelColor labelColor = mLabel.getColor();

			LabelColorHolder lch = new LabelColorHolder(labelColor);

			mLabelColorButton.setTag(lch);

			mLabelColorButton.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Dialog dialog = GetColorPickDialog(mContext, mLabelColorButton, v);
					dialog.show();
				}
			});

			mCancelButton.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			mOkButton.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(mContext, "Ok Clicked", Toast.LENGTH_SHORT).show();
				}

			});
		}

	}
}
