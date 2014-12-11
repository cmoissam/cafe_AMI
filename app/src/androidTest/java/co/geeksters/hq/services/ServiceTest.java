package co.geeksters.hq.services;

import com.eclipsesource.restfuse.DefaultCallbackResource;
import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Request;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Callback;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static com.eclipsesource.restfuse.Assert.assertAccepted;
import static org.junit.Assert.assertNotNull;
/**
 * Created by soukaina on 04/12/14.
 */
@RunWith( HttpJUnitRunner.class )
public class ServiceTest {

    @Rule
    public Destination destination = new Destination(this, "http://restfuse.com");

    @Context
    private Response response;

    private class TestCallbackResource extends DefaultCallbackResource {

        @Override
        public Response post( Request request ) {
            assertNotNull(request.getBody());
            return super.post( request );
        }
    }

    @HttpTest( method = Method.GET, path = "/test" )
    @Callback( port = 9090, path = "/asynchron", resource = TestCallbackResource.class, timeout = 10000 )
    public void testMembreGet() {
        assertAccepted(response);
    }
}