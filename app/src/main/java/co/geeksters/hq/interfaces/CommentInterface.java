package co.geeksters.hq.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Comment;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface CommentInterface {

    @GET("/posts/{id}/comments")
    void listCommentsForPost(@Path("id") int post_id, Callback<JsonArray> callback);

    @FormUrlEncoded
    @POST("/posts/{id}/comments")
    void commentPost(@Path("id") int post_id, @Field("access_token") String token, @Field("text") String comment, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/posts/{id}/comments/{comment}")
    void deleteComment(@Path("id") int postId, @Path("comment") int commentId, @Field("_method") String method, @Field("access_token") String token, Callback<JsonElement> callback);
}
