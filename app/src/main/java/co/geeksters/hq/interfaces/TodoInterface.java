package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface TodoInterface {

    @GET("/members/todos")
    void listTodosForMember(Callback<JsonElement> callback);
    @FormUrlEncoded
    @POST("/members/todos")
    //void createTodo(@Path("id") int user_id, String text, List<User> associated_members, Integer remind_me_at, Callback<JsonElement> callback);
    void createTodo(@Field("text") String text, @Field("members_id") String associated_members, @Field("remind_me_at") String remind_me_at, @Field("access_token") String token, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/todos/{id}")
    void updateTodo(@Path("id") int todo_id,@Field("text") String text, @Field("members_id") String associated_members, @Field("remind_me_at") String remind_me_at, @Field("access_token") String token, @Field("_method") String method, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/todos/{id}")
    void deleteTodo(@Path("id") int todo_id, @Field("_method") String method, @Field("access_token") String token, Callback<JsonElement> callback);
}
