package com.andrespenaloza.intellitracker.objects;

import com.andrespenaloza.intellitracker.factory.LabelFactory;

import java.util.ArrayList;
import java.util.Date;

public class Label{
    private String mName;
    private LabelFactory.LabelColor mColor;
    private long id;

    private ArrayList<Long> mItemIDS;

    public Label(String name, LabelFactory.LabelColor color) {
        mName = name;
        mColor = color;
        mItemIDS = new ArrayList<Long>();
        id = new Date().getTime();
    }

    public LabelFactory.LabelColor getColor() {
        return mColor;
    }

    public void setColor(LabelFactory.LabelColor color) {
        mColor = color;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void addTrackingItem(Long itemID) {
        if (hasTrackingItem(itemID) == false) {
            mItemIDS.add(itemID);
        }
    }

    public boolean hasTrackingItem(long itemID) {
        return mItemIDS.indexOf(itemID) == -1 ? false : true;
    }

    public void removeTrackingItem(Long itemID) {
        mItemIDS.remove(itemID);
    }

    public String toString() {
        return mName;
    }

    public ArrayList<TrackingItem> getItems() {
        ArrayList<TrackingItem> output = new ArrayList<TrackingItem>();
        for (int i = 0; i < mItemIDS.size(); i++) {
            TrackingItem item = ItemManager.getTrackingItem(mItemIDS.get(i));
            if (item == null){
                //bad state, correct
                mItemIDS.remove(i);
                continue;
            }
            output.add(item);
        }
        return output;
    }

    public long getId() {
        return id;
    }
}