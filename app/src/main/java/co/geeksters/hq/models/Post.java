package co.geeksters.hq.models;

import android.R.integer;
import android.content.Context;

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

import co.geeksters.hq.R;
import co.geeksters.hq.global.helpers.GeneralHelpers;

public class Post implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String title;
    public String content;
    public String createdAt;
    public String updatedAt;
    public Member member;

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

        Member.parseMemberResponse(response);

        Post post = gson.fromJson(response, Post.class);

        return post;
    }

    public static List<Post> createListPostsFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        /*Type listType = new TypeToken<List<Member>>(){}.getType();
        List<Member> members = gson.fromJson(response.toString(), listType);*/

        List<Post> posts = new ArrayList<Post>();

        for (int i = 0; i< response.size(); i++) {
            Post post = createPostFromJson(response.get(i));
            posts.add(post);
        }

        return posts;
    }


//    public static List<Post> createListPostsFromJson(JsonElement response) {
//        Gson gson = new GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .create();
//
//        Type listType = new TypeToken<List<Post>>(){}.getType();
//        List<Post> posts = gson.fromJson(response.toString(), listType);
//
//        return posts;
//    }

    public static ArrayList<HashMap<String, String>> postsInfoForItem(List<Post> postsList){
        HashMap<String, String> map;
        ArrayList<HashMap<String,String>> posts = new ArrayList<HashMap<String, String>>();

        for(int i = 0; i < postsList.size(); i++) {
            map = new HashMap<String, String>();

            /*if(!membersList.get(i).image.equals(""))
                map.put("picture", membersList.get(i).image);
            else*/
            map.put("id", String.valueOf(postsList.get(i).id));
            map.put("text", postsList.get(i).content);
            map.put("created_at", String.valueOf(postsList.get(i).createdAt));
            map.put("comments_size", String.valueOf(postsList.get(i).comments.size()));

            posts.add(map);
        }

        return posts;
    }

}
