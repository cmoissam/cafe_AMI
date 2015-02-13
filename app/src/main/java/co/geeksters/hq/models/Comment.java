package co.geeksters.hq.models;

import android.R.integer;
import android.content.SharedPreferences;

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
import java.util.List;

public class Comment implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String text = "";
    public String createdAt;
    public String updatedAt;
    public Member member;

    /**
     * Methods
     **/

    public static Comment createCommentFromJson(JsonElement response, Member currentMember) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Comment comment = gson.fromJson (response, Comment.class);

        if(comment.member == null) {
            comment.member = currentMember;
        }

        return comment;
    }

    public static List<Comment> createListCommentsFromJson(JsonArray response, Member currentMember) {
//        Gson gson = new GsonBuilder()
//                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                    .create();
//        Type listType = new TypeToken<List<Comment>>(){}.getType();
//        List<Comment> comments = gson.fromJson(response.toString(), listType);

        List<Comment> comments = new ArrayList<Comment>();

        for(int i=0; i<response.size(); i++) {
            Comment comment = createCommentFromJson(response.get(i), currentMember);
            comments.add(comment);
        }

        return comments;
    }
}
