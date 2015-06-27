package com.andrespenaloza.intellitracker.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.andrespenaloza.intellitracker.MyApplication;
import com.andrespenaloza.intellitracker.R;
import com.andrespenaloza.intellitracker.adapter.PackageTrakingAdapter;
import com.andrespenaloza.intellitracker.adapter.PackageTrakingAdapter.onCustomItemClickListener;
import com.andrespenaloza.intellitracker.adapter.PackageTrakingAdapter.onMoreItemClickListener;
import com.andrespenaloza.intellitracker.connection.TrackingManager;
import com.andrespenaloza.intellitracker.connection.TrackingManager.TrackingListener;
import com.andrespenaloza.intellitracker.factory.DialogFactory.EditItemDialog;
import com.andrespenaloza.intellitracker.objects.ItemManager;
import com.andrespenaloza.intellitracker.objects.TrackingItem;

public class ItemListFragment extends Fragment implements TrackingListener, onCustomItemClickListener, onMoreItemClickListener {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;

	private int mActivatedPosition = ListView.INVALID_POSITION;

	private ListView mListPackageTracking;

	private static PackageTrakingAdapter mAdapter;

	public interface Callbacks {
		public void onItemSelected(TrackingItem item);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(TrackingItem item) {
		}
	};

	@Override
	public void onItemUpdated(TrackingItem item) {
		updateListAdapter();
	}

	public static void updateTracksOnline() {
		for (int i = 0; i < ItemManager.getSelectedItems().size(); i++) {
			TrackingItem item = ItemManager.getSelectedItems().get(i);
			// if (item.getPackageStatus() ==
			// TrackingItem.PACKAGE_STATUS_DELIVERED)
			TrackingManager.startDownload(item);
		}
	}

	public static void updateListAdapter() {
		mAdapter.setItems(ItemManager.getSelectedItems());
		mAdapter.notifyDataSetChanged();
	}

	public ItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = new PackageTrakingAdapter(getActivity());
		mAdapter.setItems(ItemManager.getSelectedItems());
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.list_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);
		mListPackageTracking = (ListView) view.findViewById(R.id.packageTrakingList);
		mListPackageTracking.setAdapter(mAdapter);
		mAdapter.setOnCustomItemClickListener(this);
		mAdapter.setOnMoreItemClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
		TrackingManager.getInstance().addTrackingListener(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();

		TrackingManager.getInstance().removeTrackingListener(this);
		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onCustomItemClick(View view, int position, TrackingItem item) {
		mCallbacks.onItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		mListPackageTracking.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mListPackageTracking.setItemChecked(mActivatedPosition, false);
		} else {
			mListPackageTracking.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onMoreItemClick(View view, final TrackingItem item, int id) {		
		//final TrackingItem item = MyApplication.mItems.get(itemPosition);
		switch (id) {
		case R.id.archive:
			item.archive();		
			updateListAdapter();
			Toast.makeText(getActivity(), "Archived: "+ item.getName(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.unArchive:
			item.unarchive();	
			updateListAdapter();	
			Toast.makeText(getActivity(), "Unarchived: "+ item.getName(), Toast.LENGTH_SHORT).show();		
			break;
		case R.id.edit:
			// create dialog
			// custom dialog
			EditItemDialog dialog = new EditItemDialog(getActivity(),item){
				@Override
				public void AfterBuild() {
					mOkButton.setText("Commit");
					mOkButton.setOnClickListener(new android.view.View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							String oldTrackingNumber = mItem.getTrackingNumber();
							mItem.setName(mNameBox.getText().toString());
							mItem.setTrackingNumber(mTrackingBox.getText().toString());
							for (int i = 0; i < mLabelsToAdd.size(); i++) {
								ItemManager.linkLabel(mItem, mLabelsToAdd.get(i));
							}
							for (int i = 0; i < mLabelsToRemove.size(); i++) {
								ItemManager.unlinkLabel(mItem, mLabelsToRemove.get(i));
							}
							MyApplication.saveEntries(getActivity());
							updateListAdapter();
							if (oldTrackingNumber.compareToIgnoreCase(mItem.getTrackingNumber()) != 0)//number has changed
								TrackingManager.startDownload(mItem);
							dismiss();
						}
					});
				}
				
			};	
			
			dialog.show();
			break;
		case R.id.remove:
			ItemManager.removeItem(item);
			MyApplication.saveEntries(getActivity());
			//clear detail fragment
			//mCallbacks.onItemSelected(null);
			updateListAdapter();
			break;
		case R.id.update:
			TrackingManager.startDownload(item);
			break;
		case R.id.override:
			// Creating the instance of PopupMenu
			PopupMenu popup = new PopupMenu(getActivity(), view);
			// Inflating the Popup using xml file
			if (item.isManualMode())
				popup.getMenuInflater().inflate(R.menu.list_dropdown_status_manual_mode, popup.getMenu());
			else
				popup.getMenuInflater().inflate(R.menu.list_dropdown_status, popup.getMenu());

			// registering popup with OnMenuItemClickListener
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem menuItem) {
					switch (menuItem.getItemId()) {
					case R.id.delivered:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_DELIVERED);
						break;
					case R.id.claimed:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_CLAIMED);
						break;
					case R.id.customs:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_IN_CUSTOMS);
						break;
					case R.id.transit:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_IN_TRANSIT);
						break;
					case R.id.lost:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_LOST);
						break;
					case R.id.pickup:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_WAITING_FOR_PICKUP);
						break;
					case R.id.none:
						item.setPackageStatusOverride(TrackingItem.PACKAGE_STATUS_NO_INFO);
						break;
					default:
						return false;
					}
					updateListAdapter();
					return true;
				}
			});

			popup.show();// showing popup menu
			break;
		default:
			break;
		}
	}

}
