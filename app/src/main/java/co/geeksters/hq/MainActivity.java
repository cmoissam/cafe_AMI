package co.geeksters.hq;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.io.File;

import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.services.ConnectService;
import co.geeksters.hq.services.MemberService;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @AfterViews
    public void initServices() {
        BaseApplication.getEventBus().register(this);

        ConnectService connectService = new ConnectService();
        connectService.login("password", 1, "pioner911", "dam@geeksters.co", "hq43viable", "basic");
    }

    @Subscribe
    public void onLoginEventEvent(LoginEvent event) {
        Log.d("onEvent", event.access_token);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseApplication.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.getEventBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
