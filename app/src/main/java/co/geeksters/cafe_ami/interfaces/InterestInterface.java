package co.geeksters.cafe_ami.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface InterestInterface {

    @GET("/interests")
    void listAllInterests(Callback<JsonElement> callback);

    @GET("/interests/{id}")
    void getInterestInfo(@Path("id") int interestId, Callback<JsonElement> callback);

    @POST("/interests")
    void createInterest(@Body TypedInput interest, Callback<JsonElement> callback);

    @POST("/interests/{id}")
    void updateInterest(@Path("id") int interestId, @Body TypedInput interest, Callback<JsonElement> callback);

    @POST("/interests/{id}")
    void deleteInterest(@Path("id") int interestId, Callback<JsonElement> callback);

    @GET("/interest/suggest")
    void suggestInterests(@Query("string") String search, Callback<JsonElement> callback);
}
