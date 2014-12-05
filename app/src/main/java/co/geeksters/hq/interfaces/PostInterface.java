package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Post;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface PostInterface {

    @GET("/posts")
    void listAllPosts(String order, String from, String size, Callback<JSONArray> callback);

    @GET("/members/{id}/posts")
    void listPostsForMember(@Path("id") int user_id, Callback<JSONArray> callback);

    @POST("/members/{id}/posts")
    void createPost(@Path("id") int user_id, @Body Post post, Callback<JsonElement> callback);

    @POST("/posts/{id}")
    void deletePost(@Path("id") int post_id, Callback<JsonElement> callback);

}
