package co.geeksters.hq.models;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

public class Post {

    /**
     * Attributes
     **/

    public int id;
    public String text;
    public integer created_at;
	// A Post have a list of comments and each comment is relative to a single
	// Post
    public ArrayList<Comment> comments = new ArrayList<Comment>();

    /**
     * Methods
     **/

    public static Post createPostFromJson(JsonElement response) {
        Gson gson = new Gson();
        Post post = gson.fromJson (response, Post.class);

        return post;
    }

    public static List<Post> createListPostsFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Post>>(){}.getType();
        List<Post> posts = gson.fromJson(response.toString(), listType);

        return posts;
    }
}
