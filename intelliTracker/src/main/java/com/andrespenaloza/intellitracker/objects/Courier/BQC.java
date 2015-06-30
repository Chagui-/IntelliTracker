package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class BQC extends Courier {

    public BQC(){
        mCourierId = 190011;
        mName = "BQC";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$","*** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{9})$" ,"*** *** ***"));
    }
}