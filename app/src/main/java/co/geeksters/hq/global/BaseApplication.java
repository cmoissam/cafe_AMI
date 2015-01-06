package co.geeksters.hq.global;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by soukaina on 27/11/14.
 */
public class BaseApplication extends Application {
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
