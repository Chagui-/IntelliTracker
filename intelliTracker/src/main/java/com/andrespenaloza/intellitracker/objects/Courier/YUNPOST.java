package com.andrespenaloza.intellitracker.objects.Courier;

/**
 * Created by Andres on 29-06-2015.
 */
public class YUNPOST extends Courier {

    public YUNPOST(){
        mCourierId = 190008;
        mName = "YUNPOST";

        mTrackingPatterns.add(new TrackingPatternPair("^((YT\\d{19}))$","++ *** *** *** *** *** ***"));
        mTrackingPatterns.add(new TrackingPatternPair("^(YT\\d{18})$"  ,"++ *** *** *** *** *** *** *"));
    }
}