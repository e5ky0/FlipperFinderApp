package com.pinmyballs;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.pinmyballs.R;

import com.parse.Parse;

/**
 * Created by Raphaël on 02/10/2015. Continued by BinetLoisir on 21/02/2017
 */



public class MyApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        sAnalytics = GoogleAnalytics.getInstance(this);

        //Parse Configuration
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getResources().getString(R.string.parseApplicationId))
                .clientKey(getResources().getString(R.string.parseClientKey))
                .server(getResources().getString(R.string.parseServerUrl)).build());
}

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
