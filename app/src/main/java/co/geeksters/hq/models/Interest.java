package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Interest {

    /**
     * Attributes
     **/

    public int id;
    public String name;
	// A User have a list of interests and each interest is relative to a list
	// of User
    public ArrayList<Member> members = new ArrayList<Member>();

    public Interest(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Methods
     **/

    public static Interest createInterestFromJson(JsonElement response) {
        Gson gson = new Gson();
        Interest interest = gson.fromJson (response, Interest.class);

        return interest;
    }

    public static List<Interest> createListInterestsFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Interest>>(){}.getType();
        List<Interest> interests = gson.fromJson(response.toString(), listType);

        return interests;
    }

}
