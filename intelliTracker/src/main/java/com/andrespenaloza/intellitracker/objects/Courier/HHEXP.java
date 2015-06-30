package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class HHEXP extends Courier {

    public HHEXP(){
        mCourierId = 190003;
        mName = "HHEXP";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$"      ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{6}[A-Z0-9]{3})$"   ,"++ *** *** *** +++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(HH\\d{11})$"                   ,"++ *** *** *** **"));
    }
}