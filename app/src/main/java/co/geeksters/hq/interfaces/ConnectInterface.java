package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 * Created by soukaina on 27/11/14.
 */
public interface ConnectInterface {

    @POST("/members")
    void register(@Body TypedInput member, Callback<JsonElement> callback);

    @POST("/oauth/access_token")
    void login(@Body TypedInput loginParams, Callback<JsonElement> callback);

    @POST("/members/password/remind")
    void passwordReminder(@Body TypedInput emails, Callback<JsonElement> callback);
}
