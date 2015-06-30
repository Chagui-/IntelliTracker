package com.andrespenaloza.intellitracker.objects.Courier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Andres on 29-06-2015.
 */
public class Courier {
    public class TrackingPatternPair{
        String TrackingPattern;
        String PrettyPrintPattern;

        public TrackingPatternPair(String trackingPattern,String prettyPrintPattern ){
            TrackingPattern= trackingPattern;
            PrettyPrintPattern = prettyPrintPattern;
        }
        public TrackingPatternPair(String trackingPattern ){
            TrackingPattern= trackingPattern;
            PrettyPrintPattern = "";
        }
    }
    //  Pretty print format:
    //  * means wildcard
    //  + means to uppercase
    //  - means to lower case
    //  " " means space
    //  empty pattern passes all
    public static final HashMap<Integer,Courier> courierList= new HashMap<Integer,Courier>();

    protected final ArrayList<TrackingPatternPair> mTrackingPatterns = new ArrayList<TrackingPatternPair>();
    protected int  mCourierId;
    protected String mName;

    static{
        putCourierInList(new GlobalPostal());
        putCourierInList(new DHL());
        putCourierInList(new UPS());
        putCourierInList(new Fedex());
        putCourierInList(new TNT());
        putCourierInList(new GLS());
        putCourierInList(new Aramex());
        putCourierInList(new DPD());
        putCourierInList(new EShipper());
        putCourierInList(new Toll());
        putCourierInList(new DPDUK());
        putCourierInList(new OneWorld());
        putCourierInList(new SFExpress());
        putCourierInList(new FLYT());
        putCourierInList(new HHEXP());
        putCourierInList(new XRU());
        putCourierInList(new YUNPOST());
        putCourierInList(new BQC());
        putCourierInList(new YANWEN());
        putCourierInList(new RUSH());
        putCourierInList(new Ruston());
        putCourierInList(new Z007EX());
        putCourierInList(new RETS());
        putCourierInList(new BuyLogic());
    }

    private static void putCourierInList(Courier c){
        courierList.put(c.getCourierId(),c);
    }

    public static String applyPrettyPrintPattern(String trackingNumber,String pattern){
        if (pattern.equals(""))
            return trackingNumber;

        String prettyString = "";
        int j = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char p = pattern.charAt(i);
            char n = 0;
            try {
                n = trackingNumber.charAt(j);
            }catch (IndexOutOfBoundsException ignored){
                //no more numbers left to process
                return prettyString;
            }
            if (p == ' '){
                prettyString += ' ';
                continue;
            }
            j++;
            if (p == '*'){
                prettyString += n;
                continue;
            }
            if (p == '+'){
                prettyString += Character.toUpperCase(n);
                continue;
            }
            if (p == '-'){
                prettyString += Character.toLowerCase(n);
                continue;
            }
        }
        return prettyString;
    }

    public static ArrayList<Courier> getCouriersMatchingTracking(String trackingNumber){
        ArrayList<Courier> couriers = new ArrayList<Courier>();
        for (Courier c : courierList.values()){
            if (c.mCourierId == 0){
                //allways add Global Postal
                couriers.add(c);
                continue;
            }
            for (TrackingPatternPair p : c.mTrackingPatterns){
                if (trackingNumber.toUpperCase(Locale.US).matches(p.TrackingPattern)){
                    couriers.add(c);
                    continue;
                }
            }
        }
        return couriers;
    }
    
    public static ArrayList<Integer> getCourierIds(){
        ArrayList<Integer> courierIds = new ArrayList<Integer>();
        for (Courier c : Courier.courierList.values()){
            courierIds.add(c.getCourierId());
        }
        return courierIds;
    }
    public static ArrayList<Integer> getCourierIds(ArrayList<Courier> couriers){
        ArrayList<Integer> courierIds = new ArrayList<Integer>();
        for (Courier c : couriers){
            courierIds.add(c.getCourierId());
        }
        return courierIds;
    }


    public ArrayList<TrackingPatternPair> getTrackingPatterns(){
        return mTrackingPatterns;
    }

    public TrackingPatternPair matchPattern(String tracking){
        for (TrackingPatternPair p : mTrackingPatterns){
            if (tracking.toUpperCase(Locale.US).matches(p.TrackingPattern))
                return p;
        }
        return null;
    }

    public String getPrettyPrint(String trackingNumber){
        TrackingPatternPair p = matchPattern(trackingNumber.toUpperCase(Locale.US));
        if (p != null)
            return applyPrettyPrintPattern(trackingNumber.toUpperCase(Locale.US),p.PrettyPrintPattern);
        return trackingNumber.toUpperCase(Locale.US);
    }

    public int getCourierId() {
        return mCourierId;
    }

    public String getName() {
        return mName;
    }

    public String toString(){
        return mName + ", " + mCourierId;
    }
}
