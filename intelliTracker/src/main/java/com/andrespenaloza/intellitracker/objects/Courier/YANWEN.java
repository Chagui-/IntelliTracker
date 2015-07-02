package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class YANWEN extends Courier {

    public YANWEN(){
        mCourierId = 190012;
        mName = "YANWEN";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{10})$"         ,"++ *** *** ****"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}(YP|YW))$"   ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(Y[A-Z]\\d{9}CN)$"          ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(Y[A-Z]\\d{9})$"            ,"++ *** *** ***"));
    }
}