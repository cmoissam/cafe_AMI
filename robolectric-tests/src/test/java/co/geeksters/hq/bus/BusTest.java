package co.geeksters.hq.bus;

import android.test.InstrumentationTestCase;

import org.junit.Before;

/**
 * Created by soukaina on 02/12/14.
 */
public class BusTest extends InstrumentationTestCase {
    @Before
    public void setup() {
        //do whatever is necessary before every test
    }

    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
