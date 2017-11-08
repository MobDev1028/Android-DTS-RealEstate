package com.dts.dts;

import android.app.Application;
import android.content.Intent;

import com.dts.dts.remote.VolleySingleton;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.support.multidex.MultiDexApplication;
/**
 * Created by Android Dev E5550 on 11/18/2016.
 */

public class App extends MultiDexApplication {
    public static VolleySingleton volleyInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        volleyInstance = VolleySingleton.getInstance(this);

        //handleUnCaughtExceptions();
    }

    private void handleUnCaughtExceptions() {
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
            }
        });
    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically

        //System.exit(1); // kill off the crashed app

        /*Intent intent = new Intent ();
        intent.setAction ("com.dts.dts.activities.MainActivity"); // see step 5.
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity (intent);*/
    }
}
