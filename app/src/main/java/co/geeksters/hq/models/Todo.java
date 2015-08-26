package co.geeksters.hq.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Todo implements Serializable {

    /**
     * Attributes
     **/


    public int id;
    public String text;
    public String remindMeAt;
    public String createdAt;
    public String updatedAt;
    public int memberId;
	// List of members associated to the current todo task
    public ArrayList<Member> members = new ArrayList<Member>();

/*
    public Todo(int id, String text, String remind_me_at) {
        this.id = id;
        this.text = text;
        this.remindMeAt = remind_me_at;
    }
*/

    /**
     * Methods
     **/

    public static Todo createTodoFromJson(JsonElement response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Todo todo = gson.fromJson (response, Todo.class);

        return todo;
    }

    public static List<Todo> createListTodosFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Type listType = new TypeToken<List<Todo>>(){}.getType();
        List<Todo> todos = gson.fromJson(response.toString(), listType);

        return todos;
    }

    public static ArrayList<HashMap<String, String>> todosInfoForItem(List<Todo> todosList){
        HashMap<String, String> map;
        ArrayList<HashMap<String,String>> todos = new ArrayList<HashMap<String, String>>();

        for(int i = 0; i < todosList.size(); i++) {
            map = new HashMap<String, String>();

            map.put("id", String.valueOf(todosList.get(i).id));
            map.put("text", todosList.get(i).text);
            map.put("remind_me_at", String.valueOf(todosList.get(i).remindMeAt));
            map.put("members", String.valueOf(todosList.get(i).members));

            todos.add(map);
        }

        return todos;
    }

    public static String arrayToString(List<Member> members) {

        String membersString = "";
        for (int i = 0; i < members.size(); i++) {
            if (i < members.size() - 1)
                membersString = membersString + members.get(i).id + ",";
            else
                membersString = membersString + members.get(i).id;
        }

        return membersString;
    }


}
