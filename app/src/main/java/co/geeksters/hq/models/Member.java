package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Member {

    /**
     * Attributes
     **/

    public int id;
    public String full_name;
	public String email;
	public String password;
	public String device_token;
	public String goal;
	public String description;
	public String phone;
	public String address;
	public Boolean newsletter = false;
	public String image;
	public Boolean activation;
	public String device_id;
	public float latitude;
	public float longitude;
	public Boolean notify_by_email_on_comment = true;
	public Boolean notify_by_push_on_comment = true;
	public Boolean notify_by_email_on_todo = true;
	public Boolean notify_by_push_on_todo = true;

	// A User can be a member or an ambassador
	public String status;

	// twitter, skype, facebook, linkedin, blog, website and others
    // To do : Social as a Model
	public HashMap<String, String> socials = new HashMap<String, String>();

	// A User have a list of todos and each todo is relative to a single User
	public ArrayList<Todo> todos = new ArrayList<Todo>();

	// A User have a list of interests and each interest is relative to a list
	// of User
	public ArrayList<Interest> interests = new ArrayList<Interest>();

	// A User have a list of posts and each post is relative to a single User
	public ArrayList<Post> posts = new ArrayList<Post>();

	// A Member is a part of a list of companies and each company is represented
	// by a list of members
	public ArrayList<Company> companies = new ArrayList<Company>();

	// A User create comments
	public ArrayList<Comment> comments = new ArrayList<Comment>();

	// A Member have a list of hubs and each hub contains a list of members
	public int[] hub_ids;

    /**
     * Constructors
     **/

    public Member(int id, String full_name, String email, String password, String device_token,
                  boolean activation, String status, HashMap<String, String> socials,
                  ArrayList<Todo> todos, ArrayList<Interest> interests, ArrayList<Post> posts,
                  ArrayList<Company> companies, ArrayList<Comment> comments, int[] hubs){
        this.id = id;
        this.full_name = full_name;
        this.email = email;
        this.password = password;
        this.device_token = device_token;
        this.activation = activation;
        this.status = status;
        this.socials = socials;
        this.todos = todos;
        this.interests = interests;
        this.posts = posts;
        this.companies = companies;
        this.comments = comments;
        this.hub_ids = hubs;
    }

    /**
     * Methods
     **/

    public static Member createUserFromJson(JsonElement response) {
        Gson gson = new Gson();
        Member member = gson.fromJson (response, Member.class);

        return member;
    }

    public static List<Member> createListUsersFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Member>>(){}.getType();
        List<Member> members = gson.fromJson(response.toString(), listType);

        return members;
    }
}
