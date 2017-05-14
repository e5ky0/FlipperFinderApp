package com.pinmyballs;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Raphaël on 02/10/2015. Continued by BinetLoisir on 21/02/2017
 */
public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getResources().getString(R.string.parseApplicationId))
                .clientKey(getResources().getString(R.string.parseClientKey))
                .server(getResources().getString(R.string.parseServerUrl)).build());
    }
}
