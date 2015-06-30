package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class RUSH extends Courier {

    public RUSH(){
        mCourierId = 190014;
        mName = "RUSH";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]\\d{7})$"           ,"+ *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{3}[A-Z]\\d{4})$"     ,"*** +** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{7})$"                ,"*** *** *"));
    }
}