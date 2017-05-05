package com.example.android.newsreader;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by toddskinner on 5/4/17.
 */

public class MyApplication extends Application {
    public Tracker mTracker;

    public void startTracking(){
        if(mTracker == null){
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
        }
    }

    public Tracker getTracker(){
        startTracking();
        return mTracker;
    }
}
