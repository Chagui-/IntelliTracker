package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class DHL extends Courier {

    public DHL(){
        mCourierId = 100001;
        mName = "DHL";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$","*** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{9})$" ,"*** *** ***"));
    }
}