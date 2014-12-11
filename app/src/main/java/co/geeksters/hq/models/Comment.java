package co.geeksters.hq.models;

import android.R.integer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class Comment {

    /**
     * Attributes
     **/

    public int id;
    public String text;
    public integer created_at;

    /**
     * Methods
     **/

    public static Comment createCommentFromJson(JsonElement response) {
        Gson gson = new Gson();
        Comment comment = gson.fromJson (response, Comment.class);

        return comment;
    }

    public static List<Comment> createListCommentsFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Comment>>(){}.getType();
        List<Comment> comments = gson.fromJson(response.toString(), listType);

        return comments;
    }

}
