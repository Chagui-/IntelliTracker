package com.andrespenaloza.intellitracker.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.andrespenaloza.intellitracker.MyApplication;
import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.adapter.DrawerMenuAdapter;
import com.andrespenaloza.intellitracker.adapter.DrawerMenuAdapter.onCustomItemClickListener;
import com.andrespenaloza.intellitracker.adapter.DrawerMenuAdapter.onMoreItemClickListener;
import com.andrespenaloza.intellitracker.connection.TrackingManager;
import com.andrespenaloza.intellitracker.factory.DialogFactory.EditItemDialog;
import com.andrespenaloza.intellitracker.factory.DialogFactory.EditLabelDialog;
import com.andrespenaloza.intellitracker.factory.DialogFactory.LabelColorHolder;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.Label;
import com.andrespenaloza.intellitracker.objects.TrackingItem;
import com.andrespenaloza.intellitracker.ui.fragment.ItemDetailFragment;
import com.andrespenaloza.intellitracker.ui.fragment.ItemListFragment;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ItemListActivity extends FragmentActivity implements ItemListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static DrawerMenuAdapter mDrawerAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private int REQUEST_LOAD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);


		if (findViewById(R.id.item_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.item_list)).setActivateOnItemClick(true);
		}
		TrackingManager.setContext(getApplicationContext());

		SetupDrawer();

	}

	static public void updateDrawerAdapter() {
		mDrawerAdapter.setLabels(ItemManager.getLabels());
		mDrawerAdapter.notifyDataSetChanged();
	}

	private void SetupDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerAdapter = new DrawerMenuAdapter(this, ItemManager.getLabels());
		mDrawerList.setAdapter(mDrawerAdapter);
		// Set the list's click listener
		mDrawerAdapter.setOnCustomItemClickListener(new onCustomItemClickListener() {
			@Override
			public void onCustomItemClick(View view, int position, Label label) {
				mDrawerList.setItemChecked(position, true);
				// Toast.makeText(ItemListActivity.this, "" + position,
				// Toast.LENGTH_SHORT).show();
				mDrawerLayout.closeDrawer(mDrawerList);
				ItemManager.selectLabel(label.getName());
				ItemListFragment.updateListAdapter();
			}
		});
		mDrawerAdapter.setOnMoreItemClickListener(new onMoreItemClickListener() {

			@Override
			public void onMoreItemClick(View view, Label label, int id) {
				switch (id) {
				case R.id.edit:
					EditLabelDialog dialog = new EditLabelDialog(ItemListActivity.this, label) {

						@Override
						public void AfterBuild() {
							mOkButton.setText("Commit");
							mOkButton.setOnClickListener(new android.view.View.OnClickListener() {

								@Override
								public void onClick(View v) {
									LabelColorHolder lch = (LabelColorHolder) mLabelColorButton.getTag();
									mLabel.setName(mNameBox.getText().toString());
									mLabel.setColor(lch.labelColor);
									ItemListFragment.updateListAdapter();
									updateDrawerAdapter();

									dismiss();
								}
							});
						}
					};
					dialog.show();
					break;
				case R.id.remove:
					ItemManager.removeLabel(label);
					MyApplication.saveEntries(ItemListActivity.this);
					updateDrawerAdapter();
					ItemListFragment.updateListAdapter();
					break;
				default:
					return;
				}
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(getResources().getString(R.string.app_name));
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle("Categories");
				updateDrawerAdapter();
			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(TrackingItem item) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, item.getId());
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getId());
			startActivity(detailIntent);
		}
	}

	@Override
	protected void onPause() {
		MyApplication.saveEntries(this);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_new_track:
			mDrawerLayout.closeDrawer(mDrawerList);
			newTrack();
			break;
		case R.id.action_batch_track:
			mDrawerLayout.closeDrawer(mDrawerList);
			newTrackFromFile();
			break;
		case R.id.action_update:
			mDrawerLayout.closeDrawer(mDrawerList);
			ItemListFragment.updateTracksOnline();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void newTrackFromFile() {
		// get from file
		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, "/sdcard");
		intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

		// can user select directories or not
		intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

		// alternatively you can set file filter
		// intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		startActivityForResult(intent, REQUEST_LOAD);
	}

	public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_LOAD) {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

				FileReader fr = null;
				boolean error = false;
				String errorLines = "";
				int lineNumber = 0;
				
				try {
					fr = new FileReader(filePath);
					BufferedReader reader = new BufferedReader(fr);
					String tracking, name;
					String line = null;
					
					while ((line = reader.readLine()) != null) {
						lineNumber++;
						if (!(line.startsWith("*"))) {
							try {
								String[] parsedLine = line.split("\\t");
								// parse
								tracking = parsedLine[0];
								name = parsedLine[1];
								TrackingItem item = ItemManager.addTrackingItem(name, tracking);
								TrackingManager.startDownload(item);
							} catch (Exception e) {
								//error parsing
								if (error == false)
									errorLines += "" + lineNumber;
								else
									errorLines += ", " + lineNumber;
								error = true;
							}
						}
					}

				} catch (IOException e) {
					// File not found, etc
					e.printStackTrace();
					return;
				}
				finally {
					if (error){
						Toast.makeText(this, "Error parsing file at line: " + errorLines, Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(this, "Loading " + lineNumber + "new item(s).", Toast.LENGTH_LONG).show();
					}
					try {
						fr.close();
					} catch (IOException e) {
						//cant close
					}
					MyApplication.saveEntries(ItemListActivity.this);
					ItemListFragment.updateListAdapter();
				}
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {

		}
	}

	private void newTrack() {
		EditItemDialog dialog = new EditItemDialog(this) {
			@Override
			public void AfterBuild() {
				mOkButton.setOnClickListener(new android.view.View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mItem = ItemManager.addTrackingItem(mNameBox.getText().toString(), mTrackingBox.getText().toString());
						for (int i = 0; i < mLabelsToAdd.size(); i++) {
							ItemManager.linkLabel(mItem, mLabelsToAdd.get(i));
						}
						for (int i = 0; i < mLabelsToRemove.size(); i++) {
							ItemManager.unlinkLabel(mItem, mLabelsToRemove.get(i));
						}
						MyApplication.saveEntries(ItemListActivity.this);
						TrackingManager.startDownload(mItem);
						ItemListFragment.updateListAdapter();
						dismiss();
					}
				});
			}
		};
		dialog.show();

	}
}
