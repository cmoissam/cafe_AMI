package co.geeksters.hq;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import java.io.File;

import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.services.MemberService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //BaseApplication.getEventBus().register(this);

        MemberService memberService = new MemberService("token");
        //memberService.getMemberInfo(1);

        memberService.updateImageMember(3,new File(""));
    }

    @Subscribe
    public void onGetMemberInfoEvent(MemberEvent event) {

        Log.d("onEvent", "GetMemberInfoEvent");
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

    /*@Override
    public void onPause() {
        super.onPause();
        BaseApplication.getEventBus().unregister(this);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
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
