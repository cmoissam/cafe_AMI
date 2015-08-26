package co.geeksters.hq.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateTodoEvent;
import co.geeksters.hq.events.success.DeleteTodosEvent;
import co.geeksters.hq.events.success.PostsEvent;
import co.geeksters.hq.events.success.TodoEvent;
import co.geeksters.hq.events.success.TodosEvent;
import co.geeksters.hq.events.success.UpdateTodoEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.TodoInterface;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.models.Todo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TodoService {

    public final TodoInterface api;
    String token;

    public TodoService(String token) {
        this.api = BaseService.adapterWithToken(token).create(TodoInterface.class);
        this.token = token;
    }

    public void listTodosForMember() {
        this.api.listTodosForMember(new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Todo> todos_for_member = Todo.createListTodosFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new TodosEvent(todos_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createTodo(Todo todo) {
    //public void createTodo(int user_id, String text, List<User> associated_members, Integer remind_me_at) {

        this.api.createTodo(todo.text,Todo.arrayToString(todo.members),todo.remindMeAt,token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo created_todo = Todo.createTodoFromJson(response);
                BaseApplication.post(new CreateTodoEvent(created_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateTodo(int todo_id, String text, List<Member> associated_members, Integer remind_me_at) {

        this.api.updateTodo(todo_id, text, associated_members, remind_me_at, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo updated_todo = Todo.createTodoFromJson(response);
                BaseApplication.post(new UpdateTodoEvent(updated_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteTodo(int todo_id) {

        this.api.deleteTodo(todo_id, "delete", this.token, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Todo> todos = Todo.createListTodosFromJson(responseAsArray);
                BaseApplication.post(new DeleteTodosEvent(todos));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
