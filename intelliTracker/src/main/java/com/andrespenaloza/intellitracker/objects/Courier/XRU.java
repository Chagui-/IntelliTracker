package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class XRU extends Courier {

    public XRU(){
        mCourierId = 190007;
        mName = "XRU";

        mTrackingPatterns.add(new TrackingPatternPair("^(XRU\\d{11})$","+++ *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(XRU\\d{10})$","+++ *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(XRU\\d{9})$","+++ *** *** ***"));
    }
}