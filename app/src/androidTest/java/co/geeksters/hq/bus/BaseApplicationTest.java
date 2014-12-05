package co.geeksters.hq.bus;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;

import co.geeksters.hq.events.success.MemberEvent;

/**
 * Created by soukaina on 03/12/14.
 */

public class BaseApplicationTest extends InstrumentationTestCase {

    Bus bus;

    @Before
    public void setUp() {
        // If we are not concerned on which thread interaction is occurring,
        // we instantiate a bus instance with ThreadEnforcer.ANY.
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public void testGetMember() {
        // Register an object
        // to validate the event posted
        bus.register(new Object() {
            @Subscribe
            public void onGetMemberInfoEvent(MemberEvent event) {
                Log.d("onEvent", "GetMemberInfoEvent");
                assertEquals("test full name", event.member.full_name);
            }
        });



        // Wait for the response now
        //gameProducer.spitGame();

        // Wait for test to finish or timeout
        //while(!testDone.get());
    }

}
