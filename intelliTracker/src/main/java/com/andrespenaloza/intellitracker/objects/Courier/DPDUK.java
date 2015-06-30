package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class DPDUK extends Courier {

    public DPDUK(){
        mCourierId = 100010;
        mName = "DPD(UK)";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{14}[A-Z0-9])$" ,"*** *** *** *** **+"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{14})$"         ,"*** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$"         ,"*** *** *** *"));
    }
}