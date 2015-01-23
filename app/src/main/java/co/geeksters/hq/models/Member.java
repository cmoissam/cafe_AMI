package co.geeksters.hq.models;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.helpers.GeneralHelpers;

public class Member implements Serializable{

    public int id;
    public String fullName = "";
	public String email = "";
	public String password = "";
    public String passwordConfirmation = "";
	public String deviceToken = "";
	public String goal = "";
	public String blurp = "";
	public String phone = "";
	public String address = "";
	public int newsletter = 0;
    public String createdAt = "";
    public String updatedAt = "";
	public String image;
	public Boolean confirmed = false;
    public String confirmation = "";
	public String deviceId = "";
	public float latitude = 0;
	public float longitude = 0;
	public float distance = 0;
	public Boolean notifyByEmailOnComment = false;
	public Boolean notifyByPushOnComment = false;
	public Boolean notifyByEmailOnTodo = false;
	public Boolean notifyByPushOnTodo = false;

	// A User can be a member or an ambassador
	public Boolean ambassador = false;

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
	public Hub hub = new Hub();

    public ArrayList<Member> references = new ArrayList<Member>();

    /**
     * Constructors
     **/

    public Member(){
    }

    public Member(String fullName, String email, String password, String passwordConfirmation){
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    /**
     * Setters
     **/

    public void setSocialId(int socialId){
        if(this.social == null)
            this.social = new Social(socialId);
        else
            this.social.id = socialId;
    }

    public void setHubId(int hubId){
        this.hub.id = hubId;
    }

    public Member setSocialIdAndHubId(JsonElement response){
        if(response.getAsJsonObject().has("social_id")) {
            if (!response.getAsJsonObject().get("social_id").isJsonNull()) {
                this.setSocialId(response.getAsJsonObject().get("social_id").getAsInt());
            }
        }
        if(response.getAsJsonObject().has("hub_id")) {
            if (this.hub == null && !response.getAsJsonObject().get("hub_id").isJsonNull()) {
                this.setHubId(response.getAsJsonObject().get("hub_id").getAsInt());
            }
        }

        return this;
    }

    /**
     * Methods
     **/

    public static Member createUserFromJson(JsonElement response) {
        response = parseMemberResponse(response);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Member member = gson.fromJson(response, Member.class);

        return member.setSocialIdAndHubId(response);
    }

    public static List<Member> createListUsersFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

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
        if(response.getAsJsonObject().has("newsletter") && response.getAsJsonObject().get("newsletter").toString().equals("false")){
            response.getAsJsonObject().addProperty("newsletter", "0");
        }

        if(response.getAsJsonObject().has("newsletter") && response.getAsJsonObject().get("newsletter").toString().equals("true")){
            response.getAsJsonObject().addProperty("newsletter", "1");
        }

        if(response.getAsJsonObject().has("references") && response.getAsJsonObject().get("references") != null) {
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

        if(response.getAsJsonObject().has("created_at") && response.getAsJsonObject().has("updated_at")) {

            if (response.getAsJsonObject().get("created_at").isJsonObject()) {
                response.getAsJsonObject().addProperty("created_at", response.getAsJsonObject().get("created_at").getAsJsonObject().get("date").toString().replace("\"", "")
                );
            }

            if (response.getAsJsonObject().get("updated_at").isJsonObject()) {
                response.getAsJsonObject().addProperty("updated_at", response.getAsJsonObject().get("updated_at").getAsJsonObject().get("date").toString().replace("\"", "")
                );
            }
        } else if(response.getAsJsonObject().has("createdAt") && response.getAsJsonObject().has("updatedAt")) {

            if (response.getAsJsonObject().get("createdAt").isJsonObject()) {
                response.getAsJsonObject().addProperty("createdAt", response.getAsJsonObject().get("createdAt").getAsJsonObject().get("date").toString().replace("\"", "")
                );
            }

            if (response.getAsJsonObject().get("updatedAt").isJsonObject()) {
                response.getAsJsonObject().addProperty("updatedAt", response.getAsJsonObject().get("updatedAt").getAsJsonObject().get("date").toString().replace("\"", "")
                );
            }
        }

        if(response.getAsJsonObject().has("ambassador") && response.getAsJsonObject().get("ambassador") == null){
            response.getAsJsonObject().addProperty("ambassador", "false");
        }

        return response;
    }

    public static JsonArray parseMembersResponse(JsonArray response) {
        JsonArray parsedMembers = new JsonArray();

        for (int i = 0; i < response.size(); i++) {
            JsonElement parsedMember = parseMemberResponse(response.get(i));
            parsedMembers.add(parsedMember);
        }

        return parsedMembers;
    }

    public String returnNameForNullCompaniesValue(){
        if(this.companies.size() == 0)
            return "";
        else {
            if (this.companies.get(0) == null)
                return "";
            else
                return Character.toUpperCase(companies.get(0).name.charAt(0)) + companies.get(0).name.substring(1);
        }
    }

    public String returnNameForNullInterestsValue(int id) {
        if(this.interests.size() == 0)
            return "";
        else {
            if (this.interests.get(id) == null)
                return "";
            else
                return interests.get(id).name;
        }
    }

    public static List<Member> orderMembersByDescDistance(List<Member> listMembers) {
        List<Member> membersByDistance = new ArrayList<Member>();

        List<Member> listMembersRemovable = new ArrayList<Member>();
        listMembersRemovable.addAll(listMembers);

        while(listMembersRemovable.size() > 0) {
            Member memberWithLessDistance = listMembersRemovable.get(0);

            for (int i = 0; i < listMembersRemovable.size(); i++) {
                if(memberWithLessDistance.distance >= listMembersRemovable.get(i).distance)
                    memberWithLessDistance = listMembersRemovable.get(i);
            }

            membersByDistance.add(memberWithLessDistance);
            listMembersRemovable.remove(memberWithLessDistance);
        }

        return membersByDistance;
    }

    public static List<Member> orderMembersByAscDistance(List<Member> listMembers) {
        List<Member> membersByDistance = new ArrayList<Member>();

        List<Member> listMembersRemovable = new ArrayList<Member>();
        listMembersRemovable.addAll(listMembers);

        while(listMembersRemovable.size() > 0) {
            Member memberWithHighterDistance = listMembersRemovable.get(0);

            for (int i = 0; i < listMembersRemovable.size(); i++) {
                if(memberWithHighterDistance.distance <= listMembersRemovable.get(i).distance)
                    memberWithHighterDistance = listMembersRemovable.get(i);
            }

            membersByDistance.add(memberWithHighterDistance);
            listMembersRemovable.remove(memberWithHighterDistance);
        }

        return membersByDistance;
    }

    public static ArrayList<HashMap<String, String>> membersInfoForItem(Context context, ArrayList<HashMap<String, String>> members, List<Member> membersList){

        HashMap<String, String> map;

        //membersList = orderMembersByDescDistance(membersList);

        for(int i = 0; i < membersList.size(); i++) {
            map = new HashMap<String, String>();

            /*if(!membersList.get(i).image.equals(""))
                map.put("picture", membersList.get(i).image);
            else*/
            map.put("picture", String.valueOf(R.drawable.no_image_member));
            map.put("fullName", GeneralHelpers.firstToUpper(membersList.get(i).fullName));

            if(membersList.get(i).hub != null && !membersList.get(i).hub.name.equals(""))
                map.put("hubName", GeneralHelpers.firstToUpper(membersList.get(i).hub.name));
            else
                map.put("hubName", context.getResources().getString(R.string.empty_hub_name));

            map.put("distance", GeneralHelpers.distanceByInterval(membersList.get(i).distance));

            members.add(map);
        }

        return members;
    }

    public static ArrayList<HashMap<String, String>> membersInfoForItemByDistance(Context context, ArrayList<HashMap<String, String>> members, List<Member> membersList){

        HashMap<String, String> map;

//        membersList = orderMembersByDescDistance(membersList);

        for(int i = 0; i < membersList.size(); i++) {
            map = new HashMap<String, String>();

            /*if(!membersList.get(i).image.equals(""))
                map.put("picture", membersList.get(i).image);
            else*/
            map.put("picture", String.valueOf(R.drawable.no_image_member));
            map.put("fullName", GeneralHelpers.firstToUpper(membersList.get(i).fullName));

            if(membersList.get(i).hub != null && !membersList.get(i).hub.name.equals(""))
                map.put("hubName", GeneralHelpers.firstToUpper(membersList.get(i).hub.name));
            else
                map.put("hubName", context.getResources().getString(R.string.empty_hub_name));

            map.put("distance", GeneralHelpers.distanceByInterval(membersList.get(i).distance));

            members.add(map);
        }

        return members;
    }
}
