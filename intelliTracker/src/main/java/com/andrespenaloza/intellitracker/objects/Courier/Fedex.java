package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class Fedex extends Courier {

    public Fedex(){
        mCourierId = 100003;
        mName = "Fedex";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$" ,"*** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$" ,"*** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{15})$" ,"*** *** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{20})$" ,"*** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{22})$" ,"*** *** *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{34})$" ,"*** *** *** *** *** *** *** *** *** *** *** *"));
    }
}