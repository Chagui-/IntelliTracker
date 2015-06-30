package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class BuyLogic extends Courier {

    public BuyLogic(){
        mCourierId = 190018;
        mName = "BuyLogic";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$" ,"++ *** *** *** ++"));
    }
}