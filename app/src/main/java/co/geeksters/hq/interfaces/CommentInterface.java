package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Comment;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface CommentInterface {

    @GET("/posts/{id}/comments")
    void listCommentsForPost(@Path("id") int post_id, Callback<JSONArray> callback);

    @POST("/posts/{id}/comments")
    void commentPost(@Path("id") int post_id, @Body Comment comment, Callback<JsonElement> callback);

    @POST("/comments/{comment_id}")
    void deleteComment(@Path("id") int comment_id, Callback<JsonElement> callback);
}
