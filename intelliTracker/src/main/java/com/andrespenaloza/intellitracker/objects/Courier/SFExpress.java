package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class SFExpress extends Courier {

    public SFExpress(){
        mCourierId = 100012;
        mName = "S.F. Express";

        mTrackingPatterns.add(new TrackingPatternPair("^(R[A-Z]\\d{9}NL)$","++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{17})$"       ,"*** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"       ,"*** *** *** ***"));
    }
}