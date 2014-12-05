package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateTodoEvent;
import co.geeksters.hq.events.success.DeleteTodoEvent;
import co.geeksters.hq.events.success.ListTodosForMemberEvent;
import co.geeksters.hq.events.success.UpdateTodoEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.TodoInterface;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TodoService {

    public final TodoInterface api;

    public TodoService(String token) {
        this.api = BaseService.adapterWithToken(token).create(TodoInterface.class);
    }

    public void listTodosForMember(int user_id) {

        this.api.listTodosForMember(user_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Todo> todos_for_member = Todo.createListTodosFromJson(response);
                BaseApplication.getEventBus().post(new ListTodosForMemberEvent(todos_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void createTodo(int user_id, Todo todo) {
    //public void createTodo(int user_id, String text, List<User> associated_members, Integer remind_me_at) {

        this.api.createTodo(user_id, todo, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo created_todo = Todo.createTodoFromJson(response);
                BaseApplication.getEventBus().post(new CreateTodoEvent(created_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateTodo(int todo_id, String text, List<Member> associated_members, Integer remind_me_at) {

        this.api.updateTodo(todo_id, text, associated_members, remind_me_at, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo updated_todo = Todo.createTodoFromJson(response);
                BaseApplication.getEventBus().post(new UpdateTodoEvent(updated_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteTodo(int todo_id) {

        this.api.deleteTodo(todo_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Todo deleted_todo = Todo.createTodoFromJson(response);
                BaseApplication.getEventBus().post(new DeleteTodoEvent(deleted_todo));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }
}
