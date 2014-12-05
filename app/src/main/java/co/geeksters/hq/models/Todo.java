package co.geeksters.hq.models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

public class Todo {

    /**
     * Attributes
     **/

    public int id;
    public String text;
    public Integer remind_me_at;
	// List of members associated to the current todo task
    public ArrayList<Member> associated_members = new ArrayList<Member>();

    public Todo(int id, String text, Integer remind_me_at){
        this.id = id;
        this.text = text;
        this.remind_me_at = remind_me_at;
    }

    /**
     * Methods
     **/

    public static Todo createTodoFromJson(JsonElement response) {
        Gson gson = new Gson();
        Todo todo = gson.fromJson (response, Todo.class);

        return todo;
    }

    public static List<Todo> createListTodosFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Todo>>(){}.getType();
        List<Todo> todos = gson.fromJson(response.toString(), listType);

        return todos;
    }
}
