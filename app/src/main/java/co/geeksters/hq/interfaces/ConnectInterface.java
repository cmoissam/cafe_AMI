package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 * Created by soukaina on 27/11/14.
 */
public interface ConnectInterface {

    /* @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    }) */
    //@Headers({ "Content-Type: application/json;charset=UTF-8"})
    //@Headers("Content-Type: application/json")
    //@Headers({"Content-type: application/json", "Accept: */*"})
    @POST("/members")
    void register(@Body TypedInput member, Callback<JsonElement> callback);

    @POST("/oauth/access_token")
    void login(@Body TypedInput loginParams, Callback<JsonElement> callback);

}
