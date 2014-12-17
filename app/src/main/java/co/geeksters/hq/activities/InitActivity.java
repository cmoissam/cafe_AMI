package co.geeksters.hq.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.geeksters.hq.R;
import co.geeksters.hq.global.BaseApplication;

@EActivity(R.layout.activity_init)
public class InitActivity extends Activity {
    @AfterViews
    public void setActionBarColor(){
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#308BD1")));
    }

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseApplication.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }


}
