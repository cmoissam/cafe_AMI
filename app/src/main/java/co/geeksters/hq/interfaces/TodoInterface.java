package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import retrofit.Callback;
import retrofit.http.Body;
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

    @POST("/todos/{id}")
    void updateTodo(@Path("id") int todo_id, String text, List<Member> associated_members, Integer remind_me_at, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/todos/{id}")
    void deleteTodo(@Path("id") int todo_id, @Field("_method") String method, @Field("access_token") String token, Callback<JsonElement> callback);
}
