package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class FLYT extends Courier {

    public FLYT(){
        mCourierId = 190002;
        mName = "FLYT";

        mTrackingPatterns.add(new TrackingPatternPair("^(A\\d{15})$","+ *** *** *** *** ***"));
    }
}

