package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class Ruston extends Courier {

    public Ruston(){
        mCourierId = 190015;
        mName = "Ruston";

        mTrackingPatterns.add(new TrackingPatternPair("^(R[A-Z]\\d{9}CN)$"     ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(C[A-Z]\\d{9}CN)$"     ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(CH\\d{9})$"           ,"++ *** *** ***"));
    }
}