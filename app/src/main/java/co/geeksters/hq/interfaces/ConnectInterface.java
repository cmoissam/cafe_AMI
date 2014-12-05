package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by soukaina on 27/11/14.
 */
public interface ConnectInterface {

    @POST("/members")
    void register(@Body Member member, Callback<JsonElement> callback);

    @POST("/oauth/access_token")
    void login(String grant_type, String client_id, String client_secret, String username,
               String password, String scope, Callback<JsonElement> callback);

}
