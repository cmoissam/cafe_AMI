package co.geeksters.hq.global.helpers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by soukaina on 04/12/14.
 */
public class ParseHelper {

    public static JsonElement getJsonElementFromHttpResponse(HttpResponse response){
        HttpEntity entity = response.getEntity();
        BufferedReader bufferedReader;
        StringBuilder builder = new StringBuilder();;
        Gson gson = new Gson();
        JsonElement json_member = null;
        try {
            if (entity != null) {
                InputStream inputStream = entity.getContent();

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                for (String line = null; (line = bufferedReader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
            }
            json_member = gson.fromJson(builder.toString(), JsonElement.class);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
        return json_member;
    }

    public static HttpResponse getResponseFromAPI(){
        HttpResponse response = null;
        try {
            HttpUriRequest request = new HttpGet("http://192.168.0.8:3000/members/1");
            response = new DefaultHttpClient().execute(request);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
        return response;
    }
}
