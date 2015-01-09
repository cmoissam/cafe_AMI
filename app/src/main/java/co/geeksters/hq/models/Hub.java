package co.geeksters.hq.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Hub implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String name = "";
    public String image = "";
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
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        if(response.getAsJsonObject().get("members") != null) {
            response.getAsJsonObject().add("members", Member.parseMembersResponse(response.getAsJsonObject().get("members").getAsJsonArray()));
        }

        Hub hub = gson.fromJson (response, Hub.class);

        for (int i = 0; i< hub.members.size(); i++){
            hub.members.get(i).setSocialIdAndHubId(response.getAsJsonObject().get("members").getAsJsonArray().get(i));
        }

        return hub;
    }

    public static List<Hub> createListHubsFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Type listType = new TypeToken<List<Hub>>(){}.getType();
        List<Hub> hubs = gson.fromJson(response.toString(), listType);

        /*List<Hub> hubs = new ArrayList<Hub>();
        for (int i = 0; i< response.size(); i++) {
            Hub hub = createHubFromJson(response.get(i));
            hubs.add(hub);
        }*/

        return hubs;
    }
}
