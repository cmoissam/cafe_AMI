package co.geeksters.cafe_ami.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface PostInterface {
    @GET("/posts")
    void listAllPosts(Callback<JsonElement> callback);

    @GET("/members/posts")
    void listPostsForMe(Callback<JsonElement> callback);

    @GET("/members/{id}/posts")
    void listPostsForMember(@Path("id") int memberId, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/posts")
    void createPost(@Field("access_token") String accessToken, @Field("title") String title, @Field("content") String content,@Field("interests") String interests,@Field("concernedMembers") String concernedMembers, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/posts/{id}")
    void deletePost(@Path("id") int postId, @Field("_method") String method, @Field("access_token") String token, Callback<JsonElement> callback);
}
