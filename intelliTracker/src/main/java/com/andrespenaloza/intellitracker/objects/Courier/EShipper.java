package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class EShipper extends Courier {

    public EShipper(){
        mCourierId = 100008;
        mName = "EShipper";

        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{30})$"                 ,"*** *** *** *** *** *** *** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{26})$"                 ,"*** *** *** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$"  ,"++ *** *** *** ++"));
        mTrackingPatterns.add(new TrackingPatternPair("^(BL\\d{12})$"               ,"++ *** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{5}\\d{16})$"         ,"+++++ *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{4}[0-9A-Z]\\d{16})$" ,"+++++ *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{3}\\d{16})$"         ,"+++ *** *** *** *** *** *"));
    }
}