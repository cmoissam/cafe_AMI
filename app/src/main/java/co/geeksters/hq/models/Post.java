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
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String text;
    public integer createdAt;
	// A Post have a list of comments and each comment is relative to a single
	// Post
    public ArrayList<Comment> comments = new ArrayList<Comment>();

    /**
     * Methods
     **/

    public static Post createPostFromJson(JsonElement response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Post post = gson.fromJson (response, Post.class);

        return post;
    }

    public static List<Post> createListPostsFromJson(JSONArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Type listType = new TypeToken<List<Post>>(){}.getType();
        List<Post> posts = gson.fromJson(response.toString(), listType);

        return posts;
    }
}
