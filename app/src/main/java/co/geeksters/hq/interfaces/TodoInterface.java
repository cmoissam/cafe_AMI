package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface TodoInterface {

    @GET("/members/{id}/todos")
    void listTodosForMember(@Path("id") int user_id, Callback<JSONArray> callback);

    @POST("/members/{id}/todos")
    //void createTodo(@Path("id") int user_id, String text, List<User> associated_members, Integer remind_me_at, Callback<JsonElement> callback);
    void createTodo(@Path("id") int user_id, @Body Todo todo, Callback<JsonElement> callback);

    @POST("/todos/{id}")
    void updateTodo(@Path("id") int todo_id, String text, List<Member> associated_members, Integer remind_me_at, Callback<JsonElement> callback);

    @POST("/todos/{id}")
    void deleteTodo(@Path("id") int todo_id, Callback<JsonElement> callback);
}
