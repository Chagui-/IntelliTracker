package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class RETS extends Courier {

    public RETS(){
        mCourierId = 190017;
        mName = "RETS";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$"  ,"++ *** *** *** ++"));
    }
}