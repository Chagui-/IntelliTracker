package com.andrespenaloza.intellitracker.objects;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.andrespenaloza.intellitracker.objects.Courier.Courier;

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
            "";
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

    public static class StatusPair {
        public String mTime;
        public String mStatus;

        public StatusPair(){
            mTime = "";
            mStatus = "";
        }

        public StatusPair(String timeStatusPair){
            String[] split = TextUtils.split(timeStatusPair," -- ");
            mTime = split[0];
            mStatus = split[1];
        }

        public String toString(){
            return mTime + " -- " + mStatus;
        }
    }

    private int id;
    private String mName;
    private String mTrackingNumber;
    private Date mDateCreated;
    private Date mLastQuery;
    private ArrayList<StatusPair> mStatusList;
    private int mPackageStatus;
    private int mPackageStatusManual;
    private String mOriginCountry;
    private String mDestinationCountry;
    private ArrayList<Integer> mCourierIds;

    ArrayList<Integer> mLabelIDS;
    String mStatusServer;
    int mWorkerStatusFlag;
    Date mLastPackageStatusManualChanged;

    public String getStatusServer() {
        return mStatusServer;
    }

    public ArrayList<Integer> getCourierIds() {
        return mCourierIds;
    }

    public void setCourierId(int c) {
        mCourierIds.clear();
        mCourierIds.add(c);
    }

    public void removeCourierId(int c) {
        mCourierIds.remove((Integer)c);
    }

    public static ArrayList<Integer> stringToCourierIds(String courierIds){
        ArrayList<Integer> output = new ArrayList<Integer>();
        if (courierIds != null){
            String[] split = TextUtils.split(courierIds,";");
            for (String s : split){
                try {
                    output.add(Integer.valueOf(s));
                }catch (NumberFormatException ignored){ }
            }
        }
        return output;
    }

    public static String courierIdsToString(ArrayList<Integer> courierIds){
        String output = "";
        if (courierIds != null){
            output = TextUtils.join(";",courierIds);
        }
        return output;
    }

    public int getCurrentStatusFlag() {
        return mWorkerStatusFlag;
    }

    public boolean archive() {
        if (isArchived())
            return false;
        ItemManager.archive(this);
        return true;
    }

    public boolean isArchived() {
        return getLabel(ItemManager.LABEL_CATEGORY_UNFINISHED) == null;
    }

    public void unarchive() {
        ItemManager.unarchive(this);
    }

    public static ArrayList<StatusPair> stringToStatusList(String statusList){
        ArrayList<StatusPair> output = new ArrayList<StatusPair>();
        if (statusList != null){
            String[] split = TextUtils.split(statusList,";");
            for (String s : split){
                output.add(new StatusPair(s));
            }
        }
        return output;
    }

    public static String statusListToString(ArrayList<StatusPair> statusList){
        String output = "";
        if (statusList != null){
            output = TextUtils.join(";",statusList);
        }
        return output;
    }

    public TrackingItem(int id, String name,
                        String trackingNumber, Date dateCreated,
                        Date lastQuery, ArrayList<StatusPair> statusList,
                        int packageStatus, int packageStatusManual,
                        String originCountry, String destinationCountry,
                        ArrayList<Integer> courier) {
        this.id = id;
        mName = name;
        mTrackingNumber = trackingNumber;
        mDateCreated = dateCreated;
        mLastQuery = lastQuery;
        mStatusList = statusList;
        mPackageStatus = packageStatus;
        mPackageStatusManual = packageStatusManual;
        mOriginCountry = originCountry;
        mDestinationCountry = destinationCountry;

        mCourierIds = courier;

        mLabelIDS = new ArrayList<Integer>();
        mStatusServer = "";
        mLastQuery = new Date(0);

        if (isManualMode()) {
            // set manual mode
            //mPackageStatusManual = PACKAGE_STATUS_IN_TRANSIT;
            mStatusServer = "Manual tracking enabled.";
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

    public boolean hasLabel(Integer labelID) {
        return mLabelIDS.indexOf(labelID) != -1;
    }

    public boolean hasCustomLabels() {
        for (int i = 0; i < mLabelIDS.size(); i++) {
            String name = ItemManager.getLabel(mLabelIDS.get(i)).getName();
            if (name.compareTo(ItemManager.LABEL_CATEGORY_NONE) != 0 && name.compareTo(ItemManager.LABEL_CATEGORY_UNFINISHED) != 0)
                return true;
        }
        return false;
    }

    public void removeLabel(Integer labelID) {
        mLabelIDS.remove(labelID);
    }

    public void addLabel(Integer labelID) {
        if (!hasLabel(labelID)) {
            mLabelIDS.add(labelID);
        }
    }

    public boolean isManualMode() {
        return mTrackingNumber.length() == 0;
    }

    public String getName() {
        return mName;
    }

    public Date getDateCreated() {
        return mDateCreated;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        if (mTrackingNumber.compareTo(trackingNumber) == 0)
            return;

        mTrackingNumber = trackingNumber;

        // search couriers by tracking number
        mCourierIds = Courier.getCourierIds(Courier.getCouriersMatchingTracking(trackingNumber));
        ItemManager.getInstance(null).saveTrackingItem(this);

        if (isManualMode()) {
            // set manual mode
            mPackageStatusManual = PACKAGE_STATUS_IN_TRANSIT;
            mStatusServer = "Manual tracking enabled.";
        }
    }

    public void update(Date firstDate, Date lastDate, String lastStatus, ArrayList<StatusPair> status, int package_status) {
        // Update mStatusServer
        mStatusList.clear();
        mStatusList.addAll(status);

        // update delivered status
        mPackageStatus = package_status;

        ItemManager.getInstance(null).saveTrackingItem(this);
    }

    public void setStatus(int status){
        setStatus(status, STATUS_SERVER_STATE_NORMAL_TRACKING, STATUS_SERVER_STATE_NORMAL_TRACKING);
    }

    public void setStatus(int status, int server_status_origin,int server_status_destination) {
        mWorkerStatusFlag = status;
        switch (mWorkerStatusFlag) {
            case STATUS_IDLE:
                // Ok
                switch (server_status_origin) {
                    case STATUS_SERVER_STATE_NORMAL_TRACKING:
                        mStatusServer = STATUS_SERVER_STATE_STRING_NORMAL_TRACKING;
                        break;
                    case STATUS_SERVER_STATE_NOT_FOUND:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_NOT_FOUND + "\n";
                        break;
                    case STATUS_SERVER_STATE_WEB_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_WEB_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_PROCESS_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_PROCESS_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_SERVICE_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_SERVICE_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_WEB_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_WEB_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_SERVICE_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_SERVICE_ERROR + "\n";
                        break;
                    case STATUS_SERVER_STATE_CACHE_PROCESS_ERROR:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_CACHE_PROCESS_ERROR + "\n";
                        break;

                    case STATUS_SERVER_STATE_UNABLE_TO_TRACK:
                    default:
                        mStatusServer = "(Origin Country)" + STATUS_SERVER_STATE_STRING_UNABLE_TO_TRACK + "\n";
                        break;
                }

                switch (server_status_destination) {
                    case STATUS_SERVER_STATE_NORMAL_TRACKING:
                        mStatusServer = STATUS_SERVER_STATE_STRING_NORMAL_TRACKING;
                        break;
                    case STATUS_SERVER_STATE_NOT_FOUND: //this one generates spam
                        //mStatusServer += ", (Destination Country)" + STATUS_SERVER_STATE_STRING_NOT_FOUND;
                        break;
                    case STATUS_SERVER_STATE_WEB_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_WEB_ERROR;
                        break;
                    case STATUS_SERVER_STATE_PROCESS_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_PROCESS_ERROR;
                        break;
                    case STATUS_SERVER_STATE_SERVICE_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_SERVICE_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_WEB_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_WEB_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_PROCESS_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_PROCESS_ERROR;
                        break;
                    case STATUS_SERVER_STATE_CACHE_SERVICE_ERROR:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_CACHE_SERVICE_ERROR;
                        break;

                    case STATUS_SERVER_STATE_UNABLE_TO_TRACK:
                    default:
                        if (server_status_origin == STATUS_SERVER_STATE_NORMAL_TRACKING){
                            mStatusServer = "";
                        }
                        mStatusServer += "(Destination Country)" + STATUS_SERVER_STATE_STRING_UNABLE_TO_TRACK;
                        break;
                }

                return;
            case STATUS_SEARCHING_COURIER:
                mStatusServer = "Searching for correct courier...";
                break;
            case STATUS_UPDATING:
                mStatusServer = "Updating...";
                break;
            case STATUS_ERROR_ILLEGAL_1:
                mStatusServer = "Server Error 1.";
                break;
            case STATUS_ERROR_ILLEGAL_2:
                mStatusServer = "Server Error 2.";
                break;
            case STATUS_ERROR_SYSTEM_UPDATING:
                mStatusServer = "The system is being updated, try again later.";
                break;
            case STATUS_ERROR_ILLEGAL_4:
                mStatusServer = "Server Error 4.";
                break;
            case STATUS_ERROR_TRACKING_TOO_OFTEN:
                mStatusServer = "Your tracking is too often, try again later.";
                break;
            case STATUS_ERROR_WEBSITE:
                mStatusServer = "Website type error try again later.";
                break;
            case STATUS_ERROR_TRACKING_NUMBER:
                mStatusServer = "Not yet processed, or tracking number error.";
                break;
            case STATUS_ERROR_CAPTCHA:
                mStatusServer = "Tracking bloqued by server, try later.";
                break;
            case STATUS_ERROR_WEB_PROXY:
                mStatusServer = "Please disable web proxy.";
                break;

            case STATUS_ERROR_INTERNET:
                mStatusServer = "No internet connection.";
                break;
            case STATUS_ERROR_CONNECTION_TIMEOUT:
                mStatusServer = "Connection time out.";
                break;
            case STATUS_ERROR_NO_HTTP_RESPONSE:
                mStatusServer = "No response from server.";
                break;
            case STATUS_ERROR_UNKNOWN:
            default:
                mStatusServer = "Unkown error.";
                break;
        }
        //mPackageStatus = PACKAGE_STATUS_NO_INFO;
    }

    public ArrayList<StatusPair> getStatusList() {
        return mStatusList;
    }

    public StatusPair getLastStatus() {
        if (mStatusList.size() != 0)
            return mStatusList.get(0);
        return new StatusPair();
    }

    public Date getLastDateUpdated(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        String dateString = null;
        Date d;
        if (mStatusList.size() != 0)
            dateString = mStatusList.get(0).mTime;
        try {
            d = format.parse(dateString);
        }catch (Exception e){
            e.printStackTrace();
            return new Date(0);
        }
        return d;
    }

    public Date getFirstDateUpdated(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        String dateString = null;
        Date d;
        if (mStatusList.size() != 0)
            dateString = mStatusList.get(mStatusList.size() - 1).mTime;
        try {
            d = format.parse(dateString);
        }catch (Exception e){
            e.printStackTrace();
            return new Date(0);
        }
        return d;
    }

    public boolean canUpdate() {
        return (new Date().getTime() - mLastQuery.getTime()) / (1000f * 60) > 3f;// 3 min
    }

    public int getDaysInTransit() {
        try {
            Date date = new Date();
            if (mPackageStatus == PACKAGE_STATUS_DELIVERED) {
                date = getLastDateUpdated();
            }
            if (mPackageStatusManual == PACKAGE_STATUS_DELIVERED || mPackageStatusManual == PACKAGE_STATUS_CLAIMED) {
                date = mLastPackageStatusManualChanged;
            }
            return getDaysSince(date);
        } catch (Exception ignored) {

        }
        return 0;
    }

    public int getDaysSince(Date date) {
        int days = 0;
        try {
            days = (int) ((date.getTime() - mDateCreated.getTime()) / (1000 * 3600 * 24));
            if (days <0){
                //get day from first update
                days =  (int) ((date.getTime() - getFirstDateUpdated().getTime()) / (1000 * 3600 * 24));
            }
        } catch (Exception ignored) { }
        return days;
    }

    @SuppressLint("SimpleDateFormat")
    public int getDaysSince(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        int days = 0;
        try {
            Date dateDate = format.parse(date);
            days =  (int) ((dateDate.getTime() - mDateCreated.getTime()) / (1000 * 3600 * 24));
            if (days <0){
                //get day from first update
                days =  (int) ((dateDate.getTime() - getFirstDateUpdated().getTime()) / (1000 * 3600 * 24));
            }
        } catch (Exception ignored) { }
        return days;
    }

    public int getPackageStatus() {
        return mPackageStatus;
    }

    public void setPackageStatus(int packageStatus) {
        mPackageStatus = packageStatus;
    }

    public void setPackageStatusManual(int status) {
        mPackageStatusManual = status;
        mLastPackageStatusManualChanged = new Date();
    }

    public int getPackageStatusManual() {
        return mPackageStatusManual;
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

    public String getPackageStatusManualText() {
        String output = "";
        switch (mPackageStatusManual) {
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

    public boolean isPackageStatusManual() {
        return mPackageStatusManual != TrackingItem.PACKAGE_STATUS_NO_INFO;
    }

    public int getId() {
        return id;
    }

    public void setLastQuery(Date date) {
        mLastQuery = date;
    }
    public Date getLastQuery() {
        return mLastQuery;
    }
    public String getOriginCountry() {
        return mOriginCountry;
    }

    public String getDestinationCountry() {
        return mDestinationCountry;
    }

}
