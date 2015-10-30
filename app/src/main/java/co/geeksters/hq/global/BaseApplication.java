package co.geeksters.hq.global;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.geeksters.hq.R;

/**
 * Created by soukaina on 27/11/14.
 */
public class BaseApplication extends Application {
//    private static Context context;


    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link BaseApplication}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.analytics);
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        context = this;
//        File cacheDir = StorageUtils.getOwnCacheDirectory(
//                getApplicationContext(),
//                "/sdcard/Android/data/random_folder_name_for_cache");

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(options)
//                .discCache(new FileCountLimitedDiscCache(cacheDir, new Md5FileNameGenerator(), 1000))
                .build();

        ImageLoader.getInstance().init(config);
    }

    private static Bus bus;
    private static Map<Object, Object> events=new HashMap<Object, Object>();

    public BaseApplication() {
        bus = new Bus(ThreadEnforcer.MAIN);
    }

    public static synchronized void register(Object object) {
        if (events.containsKey(object)) {
            bus.unregister(events.get(object));
        }
        events.put(object, object);
        bus.register(object);
    }

    public static synchronized void unregister(Object object) {
        events.remove(object);
        bus.unregister(object);
    }

    public static synchronized boolean isRegistered(Object object) {
        return events.containsKey(object);
    }

    public synchronized Set<Object> getRegistered() {
        return new HashSet<Object>(events.keySet());
    }

    public synchronized void unregisterAll() {
        for (Object o : events.keySet()) {
            bus.unregister(o);
        }
        events.clear();
    }

    public static void post(Object event) {
        bus.post(event);
    }
}
