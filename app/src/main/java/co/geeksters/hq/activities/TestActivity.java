package co.geeksters.hq.activities;

import android.app.Activity;
import android.os.Bundle;

import co.geeksters.hq.global.CircleView;

/**
 * Created by soukaina on 20/01/15.
 */
public class TestActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CircleView(this));
    }
}