package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class UPS extends Courier {

    public UPS(){
        mCourierId = 100002;
        mName = "UPS";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]\\d{10})$"            ,"+ *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(1Z[A-Z0-9]{9}\\d{7})$"     ,"*+ *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"                 ,"*** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$"                 ,"*** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{9})$"                  ,"*** *** ***"));
    }
}