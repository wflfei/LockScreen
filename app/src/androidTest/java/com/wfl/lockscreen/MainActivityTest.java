package com.wfl.lockscreen;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;

/**
 * Created by wfl on 15/10/9.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{
    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();


        setActivityInitialTouchMode(true);


    }
}
