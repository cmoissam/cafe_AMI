package co.geeksters.hq;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import co.geeksters.hq.activities.MainActivityTest;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
public class MyAndroidClassTest {

    @Test
    public void testWhenActivityCreatedHelloTextViewIsVisible() throws Exception {
        MainActivityTest activity = new MainActivityTest();

        ActivityController.of(activity).attach().create();

        int visibility = activity.findViewById(R.id.home).getVisibility();
        assertEquals(visibility, View.VISIBLE);
    }
}
