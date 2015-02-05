package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Post;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface PostInterface {

    @GET("/posts")
    void listAllPosts(Callback<JsonElement> callback);

    @GET("/members/posts")
    void listPostsForMember(Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/posts")
    void createPost(@Field("access_token") String accessToken, @Field("title") String title, @Field("content") String content, Callback<JsonElement> callback);

    @POST("/posts/{id}")
    void deletePost(@Path("id") int post_id, Callback<JsonElement> callback);
}
