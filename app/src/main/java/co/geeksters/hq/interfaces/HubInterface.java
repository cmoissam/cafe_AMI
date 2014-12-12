package co.geeksters.hq.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.io.File;
import java.util.List;

import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedInput;

public interface HubInterface {
    @GET("/hubs")
    void listAllHubs(Callback<JsonElement> callback);

    @GET("/members/{id}/hubs")
    void listHubsForMember(@Path("id") int userId, Callback<JsonElement> callback);

    @GET("/hubs/{id}")
    void getHubInfo(@Path("id") int hubId, Callback<JsonElement> callback);

    @GET("/hubs/{id}/members")
    void getHubMembers(@Path("id") int hubId, Callback<JSONArray> callback);

    @GET("/hubs/{id}/ambassadors")
    void getHubAmbassadors(@Path("id") int hubId, Callback<JSONArray> callback);

    @POST("/hubs")
    void createHub(@Body TypedInput hub, Callback<JsonElement> callback);

    @POST("/hubs/{id}")
    void updateHub(@Path("id") int hubId, @Body TypedInput hub, Callback<JsonElement> callback);

    @POST("/hubs/{id}/image")
    void updateImageHub(@Path("id") int hubId, @Body TypedInput imageFile, Callback<JsonElement> callback);

    @POST("/hubs/{id}")
    void deleteHub(@Path("id") int hubId, Callback<JsonElement> callback);

}
