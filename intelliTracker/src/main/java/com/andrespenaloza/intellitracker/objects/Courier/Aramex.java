package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class Aramex extends Courier {

    public Aramex(){
        mCourierId = 100006;
        mName = "Aramex";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{20})$"  ,"*** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"  ,"*** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{11})$"  ,"*** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$"  ,"*** *** *** *"));
    }
}