package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class Z007EX extends Courier {

    public Z007EX(){
        mCourierId = 190016;
        mName = "007EX";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$"     ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(EX007\\d{7})$"                ,"++*** *** *** *"));
    }
}