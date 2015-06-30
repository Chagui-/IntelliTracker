package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class TNT extends Courier {

    public TNT(){
        mCourierId = 100004;
        mName = "TNT";

        mTrackingPatterns.add(new TrackingPatternPair("^((GD|GE)\\d{9}(WW|GB))$","++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{6})$","*** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{9})$" ,"*** *** ***"));
    }
}