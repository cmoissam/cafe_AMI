package co.geeksters.hq.bus;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import co.geeksters.hq.models.Member;

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

    @Test
    public void testHttpResponse() throws IOException {
        BufferedReader bufferedReader;
        StringBuilder builder = new StringBuilder();
        Gson gson = new Gson();

        try {
            // Given
            HttpUriRequest request = new HttpGet("http://192.168.0.8:3000/members/1");

            // When
            HttpResponse response = new DefaultHttpClient().execute(request);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream inputStream = entity.getContent();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                for (String line = null; (line = bufferedReader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
            }

            JsonElement json_member = gson.fromJson(builder.toString(), JsonElement.class);

            Member member = Member.createUserFromJson(json_member);

            assertNotNull(null);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }
}
