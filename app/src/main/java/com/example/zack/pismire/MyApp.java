package com.example.zack.pismire;

import android.app.Application;

/**
 * Created by Zack on 13/09/2015.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
            }
        });
    }
}
