package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.global.helpers.ParseHelper;

public class Hub {

    /**
     * Attributes
     **/

    public int id;
    public String name;
    public String image;
	// A Member have a list of hubs and each hub contains a list of members
    public ArrayList<Member> members = new ArrayList<Member>();
    public ArrayList<Member> ambassadors = new ArrayList<Member>();
    /**
     * Constructors
     **/

    public Hub(){

    }

    public Hub(int id){
        this.id = id;
    }

    /**
     * Methods
     **/

    public static Hub createHubFromJson(JsonElement response) {
        Gson gson = new Gson();

        if(response.getAsJsonObject().get("members") != null) {
            response.getAsJsonObject().add("members", Member.parseMembersResponse(response.getAsJsonObject().get("members").getAsJsonArray()));
        }

        Hub hub = gson.fromJson (response, Hub.class);

        for (int i = 0; i< hub.members.size(); i++){
            if(hub.members.get(i).social == null && !response.getAsJsonObject().get("members").getAsJsonArray().get(i).getAsJsonObject().get("social_id").isJsonNull()) {
                hub.members.get(i).setSocialId(response.getAsJsonObject().get("members").getAsJsonArray().get(i).getAsJsonObject().get("social_id").getAsInt());
            }

            if(hub.members.get(i).hub == null && !response.getAsJsonObject().get("members").getAsJsonArray().get(i).getAsJsonObject().get("hub_id").isJsonNull()) {
                hub.members.get(i).setHubId(response.getAsJsonObject().get("members").getAsJsonArray().get(i).getAsJsonObject().get("hub_id").getAsInt());
            }
        }

        return hub;
    }

    public static List<Hub> createListHubsFromJson(JsonArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Hub>>(){}.getType();
        List<Hub> hubs = gson.fromJson(response.toString(), listType);

        return hubs;
    }


}
