package co.geeksters.hq.services;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.events.success.HubEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.interfaces.HubInterface;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 03/12/14.
 */

public class HubServiceTest extends InstrumentationTestCase {

    Bus bus;
    HubInterface api;
    String token;

    String successMessage;
    public static Boolean doing;
    int id = 3;

    public void beforeTest() {
        this.doing = true;
    }

    public void waitTest() {
        while (this.doing) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doneTest() {
        this.doing = false;
        this.successMessage = "";
    }

    public void loginMember(String grantType, int clientId, String clientSecret, String username,
                            String password, String scope) throws Exception {
        beforeTest();

        ConnectInterface apiLogin = BaseService.adapterWithoutToken().create(ConnectInterface.class);

        JSONObject loginParams = new JSONObject();
        try {
            loginParams.put("grant_type", grantType)
                    .put("client_id", new Integer(clientId))
                    .put("client_secret", clientSecret)
                    .put("username", username)
                    .put("password", password)
                    .put("scope", scope);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiLogin.login(ParseHelpers.createTypedInputFromJsonObject(loginParams), new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                token = response.getAsJsonObject().get("access_token").toString().replace("\"","");
                doneTest();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Override
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);

        try {
            loginMember("password", 1, "pioner911", "dam@geeksters.co", "hq43viable", "basic");
        } catch (Exception e) {
            e.printStackTrace();
        }

        api = BaseService.adapterWithToken(token)
                .create(HubInterface.class);
    }

    @Test
    public void testGetHubs() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubsEvent(HubsEvent event) {
                assertNotNull("on testGetHubs", event.hubs);
                assertTrue("on testGetHubs", event.hubs.get(0) instanceof Hub);
                assertTrue("on testGetHubs", event.hubs.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        api.listAllHubs(new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Hub> hubs = Hub.createListHubsFromJson(responseAsArray);
                bus.post(new HubsEvent(hubs));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: Not Found !!!
    @Test
    public void testHubsForMember() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onHubsForMemberEvent(HubsEvent event) {
                assertNotNull("on testGetHubs", event.hubs);
                assertTrue("on testGetHubs", event.hubs.get(0) instanceof Hub);
                assertTrue("on testGetHubs", event.hubs.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        api.listHubsForMember(66, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Hub> hubs = Hub.createListHubsFromJson(responseAsArray);
                bus.post(new HubsEvent(hubs));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testGetHub() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(HubEvent event) {
                assertNotNull("on testGetHub",event.hub);
                assertTrue(event.hub instanceof Hub);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getHubInfo(id, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Hub hub = Hub.createHubFromJson(response.getAsJsonObject().get("data"));
                bus.post(new HubEvent(hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 500 error !!!
    @Test
    public void testGetHubMembers() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(MembersEvent event) {
                assertNotNull("on testGetHub",event.members);
                assertTrue(event.members.get(0) instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getHubMembers(id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
               // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: Not found !!!
    @Test
    public void testGetHubAmbassadors() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(MembersEvent event) {
                assertNotNull("on testGetHub",event.members);
                assertTrue(event.members.get(0) instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getHubAmbassadors(id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 not allowed !!!
    @Test
    public void testCreateHub() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(MembersEvent event) {
                assertNotNull("on testGetHub",event.members);
                assertTrue(event.members.get(0) instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        Hub hub = new Hub();
        hub.name = "Geeksters";

        api.createHub(ParseHelpers.createTypedInputFromModel(hub), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 not allowed !!!
    @Test
    public void testUpdateHub() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(HubEvent event) {
                assertNotNull("on testGetHub",event.hub);
                assertTrue(event.hub instanceof Hub);

                // WE ARE DONE
                doneTest();
            }
        });

        Hub hub = new Hub();
        hub.name = "Geeksters";

        api.updateHub(id, ParseHelpers.createTypedInputFromModelByMethod(hub, "put"), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 not allowed !!!
    @Test
    public void testUpdateImageHub() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(HubEvent event) {
                assertNotNull("on testGetHub",event.hub);
                assertTrue(event.hub instanceof Hub);

                // WE ARE DONE
                doneTest();
            }
        });

        JSONObject imageFile = new JSONObject();
        try {
            imageFile.put("file", new File(""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.updateImageHub(id, ParseHelpers.createTypedInputFromJsonObject(imageFile), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: Not implemented yet !!!
    @Test
    public void testUpdateHubAmbassadors() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateHubEvent(HubEvent event) {
                assertNotNull("on testUpdateHub",event.hub);
                assertTrue(event.hub instanceof Hub);

                // WE ARE DONE
                doneTest();
            }
        });

        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(44);
        JSONObject idsAmbassadors = new JSONObject();
        try {
            idsAmbassadors.put("ambassadors", GeneralHelpers.generateIdsStringFromList(ids));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.updateHubAmbassadors(id, ParseHelpers.createTypedInputFromJsonObject(idsAmbassadors), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 not allowed !!!
    @Test
    public void testDeleteHub() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetHubEvent(HubEvent event) {
                assertNotNull("on testGetHub",event.hub);
                assertTrue(event.hub instanceof Hub);

                // WE ARE DONE
                doneTest();
            }
        });

        api.deleteHub(id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }
}