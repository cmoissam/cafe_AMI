package co.geeksters.cafe_ami.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Interest implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String name = "";
	// A User have a list of interests and each interest is relative to a list
	// of User
    public ArrayList<Member> members = new ArrayList<Member>();

    /**
     * Constructors
     **/

    public Interest(){

    }

    public Interest(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     * Methods
     **/

    public static Interest createInterestFromJson(JsonElement response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        if(response.getAsJsonObject().get("members") != null) {
            response.getAsJsonObject().add("members", response.getAsJsonObject().get("members").getAsJsonArray());
        }

        Interest interest = gson.fromJson (response, Interest.class);

        for (int i = 0; i< interest.members.size(); i++){
            interest.members.get(i).setSocialIdAndHubId(response.getAsJsonObject().get("members").getAsJsonArray().get(i));
        }

        return interest;
    }

    public static List<Interest> createListInterestsFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        /*Type listType = new TypeToken<List<Interest>>(){}.getType();
        List<Interest> interests = gson.fromJson(response.toString(), listType);*/

        List<Interest> interests = new ArrayList<Interest>();

        for (int i = 0; i< response.size(); i++) {
            Interest interest = createInterestFromJson(response.get(i));
            interests.add(interest);
        }

        return interests;
    }

}
