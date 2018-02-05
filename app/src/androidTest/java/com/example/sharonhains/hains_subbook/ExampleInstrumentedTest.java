/*
 * Copyright (c) 2018, Sharon Hains. CMPUT 301. University of Alberta - All Rights Reserved. You may use, distribute, or modify this code under the terms and conditions fo the Code of Student Behaviour at the University of Alberta.
 */

package com.example.sharonhains.hains_subbook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.sharonhains.hains_subbook", appContext.getPackageName());
    }
}
