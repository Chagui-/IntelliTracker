package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class GLS extends Courier {

    public GLS(){
        mCourierId = 100005;
        mName = "GLS";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}GB)$"    ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{20})$"             ,"*** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{14})$"             ,"*** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"             ,"*** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{11})$"             ,"*** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$"             ,"*** *** *** *"));
    }
}