package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Interest;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface InterestInterface {

    @GET("/interests")
    void listAllInterests(Callback<JSONArray> callback);

    @GET("/interests/{id}")
    void getInterestInfo(@Path("id") int interest_id, Callback<JsonElement> callback);

    @POST("/interests")
    void createInterest(@Body Interest interest, Callback<JsonElement> callback);

    @POST("/interests/{id}")
    void updateInterest(@Path("id") int interest_id, String name, Callback<JsonElement> callback);

    @POST("/interests/{id}")
    void deleteInterest(@Path("id") int interest_id, Callback<JsonElement> callback);

    @GET("/interests/suggest")
    void suggestionsInterest(String search, Callback<JSONArray> callback);
}
