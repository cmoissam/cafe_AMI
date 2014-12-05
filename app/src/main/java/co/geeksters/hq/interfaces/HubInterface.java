package co.geeksters.hq.interfaces;

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

public interface HubInterface {
    @GET("/hubs")
    void listAllHubs(Callback<JSONArray> callback);

    @GET("/members/{id}/hubs")
    void listHubsForMember(@Path("id") int user_id, Callback<JSONArray> callback);

    @GET("/hubs/{id}")
    void getHubInfo(@Path("id") int hub_id, Callback<JsonElement> callback);

    @GET("/hubs/{id}/members")
    void getHubMembers(@Path("id") int hub_id, Callback<JSONArray> callback);

    @GET("/hubs/{id}/ambassadors")
    void getHubAmbassadors(@Path("id") int hub_id, Callback<JSONArray> callback);

    @POST("/hubs")
    void createHub(@Body Hub hub, Callback<JsonElement> callback);

    @POST("/hubs/{id}")
    void updateHub(@Path("id") int hub_id, String name, String image_url, List<Member> ambassadors, List<Member> members, Callback<JsonElement> callback);

    @POST("/hubs/{id}/image")
    void updateImageHub(@Path("id") int hub_id, File image_file, Callback<JsonElement> callback);

    @POST("/hubs/{id}")
    void deleteHub(@Path("id") int hub_id, Callback<JsonElement> callback);

}
