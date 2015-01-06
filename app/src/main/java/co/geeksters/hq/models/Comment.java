package co.geeksters.hq.models;

import android.R.integer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class Comment implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String text = "";
    public integer createdAt;

    /**
     * Methods
     **/

    public static Comment createCommentFromJson(JsonElement response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Comment comment = gson.fromJson (response, Comment.class);

        return comment;
    }

    public static List<Comment> createListCommentsFromJson(JSONArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Type listType = new TypeToken<List<Comment>>(){}.getType();
        List<Comment> comments = gson.fromJson(response.toString(), listType);

        return comments;
    }

}
