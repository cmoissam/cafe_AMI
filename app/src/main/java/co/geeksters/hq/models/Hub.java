package co.geeksters.hq.models;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

public class Hub implements Serializable {

    /**
     * Attributes
     **/

    public int id;
    public String name = "";
    public String image = "";
	// A Member have a list of hubs and each hub contains a list of members
    public ArrayList<Member> members = new ArrayList<Member>();
    public List<Member> ambassadors = new ArrayList<Member>();
    public int index;


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

        if(response.getAsJsonObject().has("members") && response.getAsJsonObject().get("members") != null) {
            response.getAsJsonObject().add("members", response.getAsJsonObject().get("members").getAsJsonArray());
        }

        if(response.getAsJsonObject().has("ambassadors") && response.getAsJsonObject().get("ambassadors") != null) {
            response.getAsJsonObject().add("ambassadors", response.getAsJsonObject().get("ambassadors").getAsJsonArray());
        }

        Hub hub = gson.fromJson (response, Hub.class);

        for (int i = 0; i< hub.members.size(); i++) {
            hub.members.get(i).setSocialIdAndHubId(response.getAsJsonObject().get("members").getAsJsonArray().get(i));
        }

        for (int i = 0; i< hub.ambassadors.size(); i++) {
            hub.ambassadors.get(i).setSocialIdAndHubId(response.getAsJsonObject().get("ambassadors").getAsJsonArray().get(i));
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

    public static ArrayList<HashMap<String, String>> hubsInfoForItem(ArrayList<HashMap<String, String>> hubs, List<Hub> hubsList, int from, int to){

        HashMap<String, String> map;

        for(int i = from; i < to; i++) {
            map = new HashMap<String, String>();

            map.put("hubName", GeneralHelpers.firstToUpper(hubsList.get(i).name));
            map.put("membersNumber", hubsList.get(i).members.size() + " Members");

            hubs.add(map);
        }

        return hubs;
    }

    public void saveLastHub(Context context){
        SharedPreferences preferences = context.getSharedPreferences("CurrentUser", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(!preferences.contains("last_hub_index")) {
            editor.putString("last_hub_index", "0");
            editor.commit();
        }

        GlobalVariables.indexPreference = Integer.parseInt(preferences.getString("last_hub_index", ""));

        this.index = GlobalVariables.indexPreference;
        editor.putString("last_hub" + this.id, ParseHelpers.createJsonStringFromModel(this));
        editor.commit();

        GlobalVariables.indexPreference++;

        editor.putString("last_hub_index", String.valueOf(GlobalVariables.indexPreference));
        editor.commit();
    }

    public static List<Hub> orderLastSavedHubsByIndex(List<Hub> lastHubs) {
        //Sorting
        List<Hub> sortedLastHubs = new ArrayList<Hub>();

        while (lastHubs.size() > 0) {
            Hub firstHub = lastHubs.get(0);

            for (int i = 0; i < lastHubs.size(); i++) {
                if (firstHub.index > lastHubs.get(i).index)
                    firstHub = lastHubs.get(i);
            }

            sortedLastHubs.add(firstHub);
            lastHubs.remove(firstHub);
        }

        return sortedLastHubs;
    }

    public static List<Hub> getLastSavedHubs(Context context, List<Hub> hubsEvent) {
        SharedPreferences preferences = context.getSharedPreferences("CurrentUser", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        List<Hub> lastHubs = new ArrayList<Hub>();

        for(int i = 0; i < hubsEvent.size(); i++) {
            if(preferences.contains("last_hub" + hubsEvent.get(i).id)) {
                lastHubs.add(createHubFromJson(createJsonElementFromString(preferences.getString("last_hub" + hubsEvent.get(i).id, ""))));
            }
        }

        lastHubs = orderLastSavedHubsByIndex(lastHubs);
        Collections.reverse(lastHubs);

        List<Hub> lastHubsLimited = new ArrayList<Hub>();

        int size = 0;
        if(lastHubs.size() < GlobalVariables.LIMIT_NUMBER_OF_LAST_SEARCHED_HUB)
            size = lastHubs.size();
        else
            size = GlobalVariables.LIMIT_NUMBER_OF_LAST_SEARCHED_HUB;

        for(int i=0; i<size; i++) {
            lastHubsLimited.add(lastHubs.get(i));
        }

        for(int i=GlobalVariables.LIMIT_NUMBER_OF_LAST_SEARCHED_HUB; i<lastHubs.size(); i++) {
            editor.remove("last_hub" + lastHubs.get(i).id).commit();
        }

        return lastHubsLimited;
    }

    public static List<Hub> getHubsByAlphabeticalOrder(List<Hub> listHubs) {
        Collections.sort(listHubs, new Comparator() {
            @Override
            public int compare(Object hub1, Object hub2) {
                return ((Hub) hub1).name.compareTo(((Hub) hub2).name);
            }
        });

        return listHubs;
    }

    public static List<Hub> concatenateTwoListsOfHubs(List<Hub> list1, List<Hub> list2) {
        List<Hub> listConcatenate = new ArrayList<Hub>();
        // Add Bookmarks
        listConcatenate.addAll(list1);
        // Add hubs
        listConcatenate.addAll(getHubsByAlphabeticalOrder(list2));

        return listConcatenate;
    }
}
