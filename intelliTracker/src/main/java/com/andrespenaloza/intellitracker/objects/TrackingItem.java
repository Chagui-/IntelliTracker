package com.andrespenaloza.intellitracker.objects;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Andres on 26-06-2015.
 */
public class TrackingItem {
    public static final int STATUS_SEARCHING_COURIER = 3;
    public static final int STATUS_IDLE = 2;
    public static final int STATUS_UPDATING = 1;
    //custom erros
    public static final int STATUS_ERROR_INTERNET = -10;
    public static final int STATUS_ERROR_CONNECTION_TIMEOUT = -11;
    public static final int STATUS_ERROR_NO_HTTP_RESPONSE = -12;

    public static final int STATUS_ERROR_UNKNOWN = 0;
    //server errors
    public static final int STATUS_ERROR_ILLEGAL_1 			= -1;
    public static final int STATUS_ERROR_ILLEGAL_2 			= -2;
    public static final int STATUS_ERROR_SYSTEM_UPDATING	= -3;
    public static final int STATUS_ERROR_ILLEGAL_4 			= -4;
    public static final int STATUS_ERROR_TRACKING_TOO_OFTEN	= -5;
    public static final int STATUS_ERROR_WEBSITE 			= -6;
    public static final int STATUS_ERROR_TRACKING_NUMBER 	= -7;
    public static final int STATUS_ERROR_CAPTCHA		 	= -8;
    public static final int STATUS_ERROR_WEB_PROXY		 	= -9;

    public static final int STATUS_SERVER_STATE_UNABLE_TO_TRACK 	= 0;
    public static final int STATUS_SERVER_STATE_NORMAL_TRACKING 	= 1;
    public static final int STATUS_SERVER_STATE_NOT_FOUND 			= 2;
    public static final int STATUS_SERVER_STATE_WEB_ERROR 			= 10;
    public static final int STATUS_SERVER_STATE_PROCESS_ERROR 		= 11;
    public static final int STATUS_SERVER_STATE_SERVICE_ERROR 		= 12;
    public static final int STATUS_SERVER_STATE_CACHE_WEB_ERROR		= 20;
    public static final int STATUS_SERVER_STATE_CACHE_PROCESS_ERROR	= 21;
    public static final int STATUS_SERVER_STATE_CACHE_SERVICE_ERROR	= 22;

    public static final String STATUS_SERVER_STATE_STRING_UNABLE_TO_TRACK 		=
            "Tracking number not recognized or country doesn't support online tracking.";
    public static final String STATUS_SERVER_STATE_STRING_NORMAL_TRACKING 		=
            "Normal tracking.";
    public static final String STATUS_SERVER_STATE_STRING_NOT_FOUND 			=
            "Tracking result isn't available yet.";
    public static final String STATUS_SERVER_STATE_STRING_WEB_ERROR 			=
            "Access to server error, maybe caused by temporarily disconnected server.";
    public static final String STATUS_SERVER_STATE_STRING_PROCESS_ERROR 		=
            "Process error, check again later.";
    public static final String STATUS_SERVER_STATE_STRING_SERVICE_ERROR 		=
            "Service error, check again later.";
    public static final String STATUS_SERVER_STATE_STRING_CACHE_WEB_ERROR			=
            "Web error, using cache.";
    public static final String STATUS_SERVER_STATE_STRING_CACHE_PROCESS_ERROR 	=
            "Process error, using cache.";
    public static final String STATUS_SERVER_STATE_STRING_CACHE_SERVICE_ERROR 	=
            "Service error, using cacher.";

    public static final int PACKAGE_STATUS_NO_INFO = 0;
    public static final int PACKAGE_STATUS_IN_TRANSIT = 11;
    public static final int PACKAGE_STATUS_LOST = 12;
    public static final int PACKAGE_STATUS_CLAIMED = 13;
    public static final int PACKAGE_STATUS_DELIVERED = 14;
    public static final int PACKAGE_STATUS_WAITING_FOR_PICKUP = 15;
    public static final int PACKAGE_STATUS_IN_CUSTOMS = 16;

    public static final String PACKAGE_STATUS_STRING_NO_INFO 				= "Not found";
    public static final String PACKAGE_STATUS_STRING_IN_TRANSIT 			= "In transit";
    public static final String PACKAGE_STATUS_STRING_LOST 					= "Package lost";
    public static final String PACKAGE_STATUS_STRING_CLAIMED 				= "Claimed";
    public static final String PACKAGE_STATUS_STRING_DELIVERED 				= "Delivered";
    public static final String PACKAGE_STATUS_STRING_WAITING_FOR_PICKUP 	= "Waiting for pickup";
    public static final String PACKAGE_STATUS_STRING_IN_CUSTOMS 			= "In customs";

    public static final int COURIER_UNKNOWN = 0;
    public static final int COURIER_GLOBAL_POSTAL = 1;
    public static final int COURIER_DHL = 100002;
    public static final int COURIER_UPS = 100003;
    public static final int COURIER_FEDEX = 100004;
    public static final int COURIER_TNT = 100005;
    public static final int COURIER_GLS = 100006;
    public static final int COURIER_ARAMEX = 100007;
    public static final int COURIER_DPD = 100008;
    public static final int COURIER_ESHIPPER = 100009;

    public static final String COURIER_GLOBAL_POSTAL_CODE	= "^([A-Z]{2}\\d{9}[A-Z]{2})$";
    public static final String COURIER_DHL_CODE 			= "^((\\d{10})|(\\d{9}))$";
    public static final String COURIER_UPS_CODE 			= "^((1Z[A-Z0-9]{9}\\d{7})|(\\d{10})|(\\d{12})|(\\d{9}))$";
    public static final String COURIER_FEDEX_CODE 			= "^((\\d{15})|(\\d{12})|(\\d{10}))$";
    public static final String COURIER_TNT_CODE 			= "^((GD\\d{9}WW)|(\\d{9})|(GE\\d{9}WW)|(\\d{9})|(\\d{6}))$";
    public static final String COURIER_GLS_CODE 			= "^(([A-Z]{2}\\d{9}GB)|(\\d{20})|(\\d{14})|(\\d{12})|(\\d{11})|(\\d{10}))$";
    public static final String COURIER_ARAMEX_CODE 			= "^((\\d{20})|(\\d{12})|(\\d{11})|(\\d{10}))$";
    public static final String COURIER_DPD_CODE 			= "^((\\d{14}[A-Z0-9])|(\\d{14})|(\\d{10}))$";
    public static final String COURIER_ESHIPPER_CODE 		= "^(([A-Z]{3}\\d{16})|([A-Z]{3}[A-Z0-9]\\d{16})|([A-Z]{5}\\d{16}))$";

    public static final int COURIER_NUMBER = 9;

    public static class StatusPair {
        public String mTime;
        public String mStatus;

        public String toString(){
            return mTime + ", " + mStatus;
        }
    }

    int mCourier;
    String mName;
    String mHash;
    String mTrackingNumber;
    ArrayList<StatusPair> mStatusList;
    String mStatus; //mStatusServer
    String mStatusOld; //mStatus
    int mPackageStatus;
    int mPackageStatusOld;
    Date mLastUpdated;
    Date mLastQuery;
    Date mLastStatusChanged;
    Date mLastStatusChangedOverride;
    Date mFirstTimeUpdated;
    int mCurrentStatusFlag;
    int mPackageStatusOverride;
    ArrayList<Long> mLabelIDS;
    boolean isArchived;
    private long id;

    public int getCourier() {
        return mCourier;
    }

    public String getStatusServer() {
        return mStatus;
    }


    public int getCourierType() {
        return mCourier - 1;
    }

    public void setCourier(int courier) {
        if (courier == 100001)
            courier = 1;
        mCourier = courier;
    }

    public int getCurrentStatusFlag() {
        return mCurrentStatusFlag;
    }

    public boolean archive() {
        if (isArchived)
            return false;
        ItemManager.archive(this);
        isArchived = true;
        return true;
    }

    public boolean isArchive() {
        return isArchived;
    }

    public void unarchive() {
        ItemManager.unarchive(this);
        isArchived = false;
    }

    public TrackingItem(String name, String trackingNumber) {
        isArchived = false;
        mName = name;
        mHash = "";
        mTrackingNumber = trackingNumber;
        mStatusList = new ArrayList<StatusPair>();
        mLabelIDS = new ArrayList<Long>();
        mStatus = "--";
        mStatusOld = "--";
        mLastUpdated = new Date(0);
        mLastQuery = new Date(0);
        mLastStatusChangedOverride = new Date(0);
        mLastStatusChanged = new Date(0);
        id = new Date().getTime();
        if (isManualMode()) {
            // set manual mode
            mFirstTimeUpdated = new Date();
            mPackageStatusOverride = PACKAGE_STATUS_IN_TRANSIT;
            mStatus = "Manual tracking enabled.";
        }
    }

    public ArrayList<Label> getLabels() {
        ArrayList<Label> output = new ArrayList<Label>();
        for (int i = 0; i < mLabelIDS.size(); i++) {
            output.add(ItemManager.getLabel(mLabelIDS.get(i)));
        }
        return output;
    }

    public Label getLabel(String name) {
        for (int i = 0; i < mLabelIDS.size(); i++) {
            Label label = ItemManager.getLabel(mLabelIDS.get(i));
            if (label.getName().equals(name))
                return label;
        }
        return null;
    }

    public boolean hasLabel(long labelID) {
        return mLabelIDS.indexOf(labelID) == -1 ? false : true;
    }

    public boolean hasCustomLabels() {
        for (int i = 0; i < mLabelIDS.size(); i++) {
            String name = ItemManager.getLabel(mLabelIDS.get(i)).getName();
            if (name.compareTo(ItemManager.LABEL_CATEGORY_NONE) != 0 && name.compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) != 0)
                return true;
            ;
        }
        return false;
    }

    public void removeLabel(long labelID) {
        mLabelIDS.remove(labelID);
    }

    public void addLabel(long labelID) {
        if (hasLabel(labelID) == false) {
            mLabelIDS.add(labelID);
        }
    }

    public boolean isManualMode() {
        return mTrackingNumber.length() == 0;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setHash(String hash) {
        mHash = hash;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        if (mTrackingNumber.compareTo(trackingNumber) == 0)
            return;
        mLastQuery = new Date(0);
        mTrackingNumber = trackingNumber;
        mHash = "";
        if (mTrackingNumber.matches("^[a-zA-Z]{2}\\d{9}[a-zA-Z]{2}$")) {
            // global postal
            setCourier(COURIER_GLOBAL_POSTAL);
        } else {
            setCourier(COURIER_UNKNOWN);
        }
        if (isManualMode()) {
            // set manual mode
            mFirstTimeUpdated = new Date();
            mPackageStatusOverride = PACKAGE_STATUS_IN_TRANSIT;
            mStatus = "Manual tracking enabled.";
        }
    }

    public String getHash() {
        return mHash;
    }

    public void update(Date firstDate, Date lastDate, String lastStatus, ArrayList<StatusPair> status, int package_status) {
        if (mHash == null || mHash == "") {

        }
        // Update mStatus
        //mStatus = lastStatus;
        mStatusOld = lastStatus;
        mStatusList.clear();
        mStatusList.addAll(status);
        // Update mLastUpdated
        mLastUpdated = new Date();
        mLastStatusChanged = lastDate;
        mFirstTimeUpdated = firstDate;
        // update delivered status
        mPackageStatus = package_status;
        mPackageStatusOld = mPackageStatus;
    }

    public void setStatus(int status){
        setStatus(status,STATUS_SERVER_STATE_NORMAL_TRACKING,STATUS_SERVER_STATE_NORMAL_TRACKING);
    }

    public void setStatus(int status, int server_status_origin,int server_status_destination) {
        mCurrentStatusFlag = status;
        switch (mCurrentStatusFlag) {
            case STATUS_IDLE:
                // Ok
                //mStatus = mStatusOld;
                mPackageStatus = mPackageStatusOld;
                switch (server_status_origin) {
                    case STATUS_SERVER_STATE_NORMAL_TRACKING:
                        mStatus = STATUS_SERVER_STATE_STRING_NORMAL_TRACKING;
                        break;
                    case STATUS_SERVER_STATE_NOT_FOUND:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_NOT_FOUND + "\n";
                        break;
                    case STATUS_SERVER_STATE_WEB_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_WEB_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_PROCESS_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_PROCESS_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_SERVICE_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_SERVICE_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_WEB_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_WEB_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_SERVICE_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_SERVICE_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_PROCESS_ERROR:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_PROCESS_ERROR + "\n";
                        break;

                    case STATUS_SERVER_STATE_UNABLE_TO_TRACK:
                    default:
                        mStatus = "(Origin Country)" + STATUS_SERVER_STATE_STRING_UNABLE_TO_TRACK + "\n";
                        break;
                }

                switch (server_status_destination) {
                    case STATUS_SERVER_STATE_NORMAL_TRACKING:
                        mStatus = STATUS_SERVER_STATE_STRING_NORMAL_TRACKING;
                        break;
                    case STATUS_SERVER_STATE_NOT_FOUND: //this one generates spam
                        //mStatus += ", (Destination Country)" + STATUS_SERVER_STATE_STRING_NOT_FOUND;
                        break;
                    case STATUS_SERVER_STATE_WEB_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_WEB_ERROR;
                        break;
                    case STATUS_SERVER_STATE_PROCESS_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_PROCESS_ERROR;
                        break;
                    case STATUS_SERVER_STATE_SERVICE_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_SERVICE_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_WEB_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_WEB_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_PROCESS_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_PROCESS_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_SERVICE_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_SERVICE_ERROR;
                        break;

                    case STATUS_SERVER_STATE_UNABLE_TO_TRACK:
                    default:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatus = "";
                        }
                        mStatus += "(Destination Country)" + STATUS_SERVER_STATE_STRING_UNABLE_TO_TRACK;
                        break;
                }

                return;
            case STATUS_SEARCHING_COURIER:
                mStatus = "Searching for correct courier...";
                break;
            case STATUS_UPDATING:
                mStatus = "Updating...";
                break;
            case STATUS_ERROR_ILLEGAL_1:
                mStatus = "Server Error 1.";
                break;
            case STATUS_ERROR_ILLEGAL_2:
                mStatus = "Server Error 2.";
                break;
            case STATUS_ERROR_SYSTEM_UPDATING:
                mStatus = "The system is being updated, try again later.";
                break;
            case STATUS_ERROR_ILLEGAL_4:
                mStatus = "Server Error 4.";
                break;
            case STATUS_ERROR_TRACKING_TOO_OFTEN:
                mStatus = "Your tracking is too often, try again later.";
                break;
            case STATUS_ERROR_WEBSITE:
                mStatus = "Website type error try again later.";
                break;
            case STATUS_ERROR_TRACKING_NUMBER:
                mStatus = "Not yet processed, or tracking number error.";
                break;
            case STATUS_ERROR_CAPTCHA:
                mStatus = "Tracking bloqued by server, try later.";
                break;
            case STATUS_ERROR_WEB_PROXY:
                mStatus = "Please disable web proxy.";
                break;

            case STATUS_ERROR_INTERNET:
                mStatus = "No internet connection.";
                break;
            case STATUS_ERROR_CONNECTION_TIMEOUT:
                mStatus = "Connection time out.";
                break;
            case STATUS_ERROR_NO_HTTP_RESPONSE:
                mStatus = "No response from server.";
                break;
            case STATUS_ERROR_UNKNOWN:
            default:
                mStatus = "Unkown error.";
                //mStatusOld = mStatus;
                break;
        }
        //mPackageStatus = PACKAGE_STATUS_NO_INFO;
    }

    public ArrayList<StatusPair> getStatusList() {
        return mStatusList;
    }

    public String getStatus() {
        return mStatusOld;
    }

    public Date getLastUpdated() {
        return mLastUpdated;
    }

    public boolean canUpdate() {
        return (new Date().getTime() - mLastQuery.getTime()) / (1000f * 60) > 5f;// 5
        // min
    }

    public int getDaysInTransit() {
        try {
            Date date = new Date();
            if (mPackageStatus == PACKAGE_STATUS_DELIVERED) {
                date = mLastStatusChanged;
            }
            if (mPackageStatusOverride == PACKAGE_STATUS_DELIVERED || mPackageStatusOverride == PACKAGE_STATUS_CLAIMED) {
                date = mLastStatusChangedOverride;
            }
            return (int) ((date.getTime() - mFirstTimeUpdated.getTime()) / (1000 * 3600 * 24));
        } catch (Exception e) {

        }
        return 0;
    }

    public int getDaysSince(Date date) {
        try {
            return (int) ((date.getTime() - mFirstTimeUpdated.getTime()) / (1000 * 3600 * 24));
        } catch (Exception e) {

        }
        return 0;
    }

    @SuppressLint("SimpleDateFormat")
    public int getDaysSince(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        try {
            Date dateDate = format.parse(date);
            return (int) ((dateDate.getTime() - mFirstTimeUpdated.getTime()) / (1000 * 3600 * 24));
        } catch (Exception e) {

        }
        return 0;
    }

    public boolean hasHash() {
        if (mHash == null || mHash == "") {
            return false;
        }
        return true;
    }

    public int getPackageStatus() {
        return mPackageStatus;
    }

    public void setPackageStatus(int packageStatus) {
        mPackageStatus = packageStatus;
    }

    public void setPackageStatusOverride(int status) {
        mPackageStatusOverride = status;
        mLastStatusChangedOverride = new Date();
    }

    public int getPackageStatusOverride() {
        return mPackageStatusOverride;
    }

    public String toString() {
        return mName + ", " + mTrackingNumber;
    }

    public String getPackageStatusText() {
        String output = "";
        switch (mPackageStatus) {
            case TrackingItem.PACKAGE_STATUS_NO_INFO:
                output = TrackingItem.PACKAGE_STATUS_STRING_NO_INFO;
                break;
            case TrackingItem.PACKAGE_STATUS_DELIVERED:
                output = TrackingItem.PACKAGE_STATUS_STRING_DELIVERED;
                break;
            case TrackingItem.PACKAGE_STATUS_CLAIMED:
                output = TrackingItem.PACKAGE_STATUS_STRING_CLAIMED;
                break;
            case TrackingItem.PACKAGE_STATUS_IN_CUSTOMS:
                output = TrackingItem.PACKAGE_STATUS_STRING_IN_CUSTOMS;
                break;
            case TrackingItem.PACKAGE_STATUS_IN_TRANSIT:
                output = TrackingItem.PACKAGE_STATUS_STRING_IN_TRANSIT;
                break;
            case TrackingItem.PACKAGE_STATUS_LOST:
                output = TrackingItem.PACKAGE_STATUS_STRING_LOST;
                break;
            case TrackingItem.PACKAGE_STATUS_WAITING_FOR_PICKUP:
                output = TrackingItem.PACKAGE_STATUS_STRING_WAITING_FOR_PICKUP;
                break;
            default:
                break;
        }
        return output;
    }

    public String getPackageStatusOverrideText() {
        String output = "";
        switch (mPackageStatusOverride) {
            case TrackingItem.PACKAGE_STATUS_NO_INFO:
                output = "No Info";
                break;
            case TrackingItem.PACKAGE_STATUS_DELIVERED:
                output = "Delivered";
                break;
            case TrackingItem.PACKAGE_STATUS_CLAIMED:
                output = "Claimed";
                break;
            case TrackingItem.PACKAGE_STATUS_IN_CUSTOMS:
                output = "In Customs";
                break;
            case TrackingItem.PACKAGE_STATUS_IN_TRANSIT:
                output = "In Transit";
                break;
            case TrackingItem.PACKAGE_STATUS_LOST:
                output = "Lost";
                break;
            case TrackingItem.PACKAGE_STATUS_WAITING_FOR_PICKUP:
                output = "Waiting for Pickup";
                break;
            default:
                break;
        }
        return output + " (Manual)";
    }

    public boolean isPackageStatusOverride() {
        return mPackageStatusOverride != TrackingItem.PACKAGE_STATUS_NO_INFO;
    }

    public long getId() {
        return id;
    }

    public void setlastQuery(Date date) {
        mLastQuery = date;
    }
}
