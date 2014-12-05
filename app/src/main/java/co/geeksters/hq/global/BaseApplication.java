package co.geeksters.hq.global;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by soukaina on 27/11/14.
 */
public class BaseApplication extends Application {
    private static Bus bus;

    @Override
    public void onCreate()
    {
        super.onCreate();

        this.bus = new Bus();
    }

    public static Bus getEventBus() {
        return bus;
    }
}
