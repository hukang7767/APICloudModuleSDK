package com.alpha.modulegnoga.common;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class SysApplication extends Application {
    public static final String TAG = "SysApplication";

    private static final String MAIN_ACTIVITY = "MainActivity";

    private final List<Activity> mList = new LinkedList<Activity>();

    private static SysApplication sInstance;

    private SysApplication() {
    }

    public synchronized static SysApplication getInstance() {
        if (null == sInstance) {
            sInstance = new SysApplication();
        }
        return sInstance;
    }

    public void removeActivity(Activity activity) {
        mList.remove(activity);
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
