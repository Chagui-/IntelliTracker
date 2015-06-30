package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class Toll extends Courier {

    public Toll(){
        mCourierId = 100009;
        mName = "Toll";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"  ,"*** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{9})$"   ,"*** *** ***"));
    }
}