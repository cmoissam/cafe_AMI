package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.global.helpers.ParseHelper;

public class Member {

    public int id;
    public String full_name;
	public String email;
	public String password;
    public String password_confirmation;
	public String device_token;
	public String goal;
	public String blurp;
	public String phone;
	public String address;
	public int newsletter = 0;
    public String created_at;
    public String updated_at;
	public String image;
	public Boolean confirmed;
    public String confirmation;
	public String device_id;
	public float latitude;
	public float longitude;
	public Boolean notify_by_email_on_comment = true;
	public Boolean notify_by_push_on_comment = true;
	public Boolean notify_by_email_on_todo = true;
	public Boolean notify_by_push_on_todo = true;

	// A User can be a member or an ambassador
	public Boolean ambassador;

	// twitter, skype, facebook, linkedin, blog, website and others
    public Social social;

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
	public Hub hub;

    public ArrayList<Member> references = new ArrayList<Member>();

    /**
     * Constructors
     **/

    public Member(){
    }

    public Member(String fullName, String email, String password, String passwordConfirmation){
        this.full_name = fullName;
        this.email = email;
        this.password = password;
        this.password_confirmation = passwordConfirmation;
    }

    /**
     * Setters
     **/

    public void setSocialId(int socialId){
        this.social = new Social(socialId);
    }

    public void setHubId(int hubId){
        this.hub = new Hub(hubId);
    }

    public Member setSocialIdAndHubId(JsonElement response){
        if(!response.getAsJsonObject().get("social_id").isJsonNull()) {
            this.setSocialId(response.getAsJsonObject().get("social_id").getAsInt());
        }

        if(this.hub == null  && response.getAsJsonObject().get("hub_id") != null) {
            this.setHubId(response.getAsJsonObject().get("hub_id").getAsInt());
        }

        return this;
    }

    /**
     * Methods
     **/

    public static Member createUserFromJson(JsonElement response) {
        Gson gson = new Gson();

        response = parseMemberResponse(response);

        Member member = gson.fromJson (response, Member.class);

        return member.setSocialIdAndHubId(response);
    }

    public static List<Member> createListUsersFromJson(JsonArray response) {
        Gson gson = new Gson();

        /*Type listType = new TypeToken<List<Member>>(){}.getType();
        List<Member> members = gson.fromJson(response.toString(), listType);*/

        List<Member> members = new ArrayList<Member>();

        for (int i = 0; i< response.size(); i++) {
            Member member = createUserFromJson(response.get(i));
            members.add(member);
        }

        return members;
    }

    public static JsonElement parseMemberResponse(JsonElement response){
        if(response.getAsJsonObject().get("newsletter").toString().equals("false")){
            response.getAsJsonObject().addProperty("newsletter", "0");
        }

        if(response.getAsJsonObject().get("newsletter").toString().equals("true")){
            response.getAsJsonObject().addProperty("newsletter", "1");
        }

        if(response.getAsJsonObject().get("references") != null) {
            JsonArray references = response.getAsJsonObject().get("references").getAsJsonArray();

            for (int i = 0; i < references.size(); i++) {
                if (references.get(i).getAsJsonObject().get("newsletter").toString().equals("false")) {
                    references.get(i).getAsJsonObject().addProperty("newsletter", "0");
                }

                if (references.get(i).getAsJsonObject().get("newsletter").toString().equals("true")) {
                    references.get(i).getAsJsonObject().addProperty("newsletter", "1");
                }
            }
        }

        if(response.getAsJsonObject().get("created_at").isJsonObject()){
            response.getAsJsonObject().addProperty("created_at", response.getAsJsonObject().get("created_at").getAsJsonObject().get("date").toString().replace("\"", "")
            );
        }

        if(response.getAsJsonObject().get("updated_at").isJsonObject()){
            response.getAsJsonObject().addProperty("updated_at", response.getAsJsonObject().get("updated_at").getAsJsonObject().get("date").toString().replace("\"", "")
            );
        }

        if(response.getAsJsonObject().get("ambassador") == null){
            response.getAsJsonObject().addProperty("ambassador", "false");
        }

        return response;
    }

    public static JsonArray parseMembersResponse(JsonArray response){
        JsonArray parsedMembers = new JsonArray();

        for (int i = 0; i < response.size(); i++) {
            JsonElement parsedMember = parseMemberResponse(response.get(i));
            parsedMembers.add(parsedMember);
        }

        return parsedMembers;
    }
}
