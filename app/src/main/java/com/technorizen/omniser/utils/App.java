package com.technorizen.omniser.utils;

import android.app.Activity;
import android.app.Application;

public class App extends Application {

    Activity activity;

    public static void changeStatusBarColor(Activity activity) {
        ProjectUtil.changeStatusBarColor(activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
