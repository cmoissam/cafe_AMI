package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Hub {

    /**
     * Attributes
     **/

    public int id;
    public String name;
    public String image;
	// A Member have a list of hubs and each hub contains a list of members
    public ArrayList<Member> members = new ArrayList<Member>();

    /**
     * Methods
     **/

    public static Hub createHubFromJson(JsonElement response) {
        Gson gson = new Gson();
        Hub hub = gson.fromJson (response, Hub.class);

        return hub;
    }

    public static List<Hub> createListHubsFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Hub>>(){}.getType();
        List<Hub> hubs = gson.fromJson(response.toString(), listType);

        return hubs;
    }

}
