package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class GlobalPostal extends Courier {

    public GlobalPostal(){
        mCourierId = 0;
        mName = "Global Postal";

        //Common Global
        mTrackingPatterns.add(new TrackingPatternPair("^([A-Z]{2}\\d{9}[A-Z]{2})$","++ *** *** *** ++"));

        //Chile
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{12})$","*** *** *** ***"));

        //USA
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{10})$","*** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{20})$","*** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{22})$","*** *** *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{26})$","*** *** *** *** *** *** *** *** **"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{28})$","*** *** *** *** *** *** *** *** *** *"));
        mTrackingPatterns.add(new TrackingPatternPair("^(\\d{30})$","*** *** *** *** *** *** *** *** *** ***"));
    }
}
