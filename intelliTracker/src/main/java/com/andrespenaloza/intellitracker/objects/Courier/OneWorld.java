package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class OneWorld extends Courier {

    public OneWorld(){
        mCourierId = 100011;
        mName = "One World";

        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}GB)$"    ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(CZL\\d{5})$"           ,"+++ *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$"             ,"*** *** *** ***"));
    }
}