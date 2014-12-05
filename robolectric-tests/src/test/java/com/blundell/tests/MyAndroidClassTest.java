package com.blundell.tests;

import android.view.View;

import co.geeksters.hq.RobolectricGradleTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import co.geeksters.hq.MainActivity;
import co.geeksters.hq.R;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(manifest="app/src/main/AndroidManifest.xml", emulateSdk = 18)
public class MyAndroidClassTest {

    @Test
    public void testWhenActivityCreatedHelloTextViewIsVisible() throws Exception {
        MainActivity activity = new MainActivity();

        ActivityController.of(activity).attach().create();

        int visibility = activity.findViewById(R.id.home).getVisibility();
        assertEquals(visibility, View.VISIBLE);
    }
}
