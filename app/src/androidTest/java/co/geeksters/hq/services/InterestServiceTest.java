package co.geeksters.hq.services;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import co.geeksters.hq.events.success.CompaniesEvent;
import co.geeksters.hq.events.success.CompanyEvent;
import co.geeksters.hq.events.success.InterestEvent;
import co.geeksters.hq.events.success.InterestsEvent;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.CompanyInterface;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.interfaces.InterestInterface;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Interest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 15/12/14.
 */
public class InterestServiceTest extends InstrumentationTestCase {
    Bus bus;
    InterestInterface api;
    String token;

    String successMessage;
    public static Boolean doing;
    int id = 1;

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

        apiLogin.login(ParseHelper.createTypedInputFromJsonObject(loginParams), new Callback<JsonElement>() {
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
                .create(InterestInterface.class);
    }

    @Test
    public void testGetInterests() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetInterestsEvent(InterestsEvent event) {
                assertNotNull("on testGetInterests", event.interests);
                assertTrue("on testGetInterests", event.interests.get(0) instanceof Interest);
                assertTrue("on testGetInterests", event.interests.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        api.listAllInterests(new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Interest> interests = Interest.createListInterestsFromJson(responseAsArray);
                bus.post(new InterestsEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testGetInterest() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetInterestEvent(InterestEvent event) {
                assertNotNull("on testGetInterest", event.interest);
                assertTrue("on testGetInterest", event.interest instanceof Interest);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getInterestInfo(id, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response.getAsJsonObject().get("data"));
                bus.post(new InterestEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 402 No authentication challenges found !!!
    @Test
    public void testCreateInterest() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onCreateInterestEvent(InterestEvent event) {
                assertNotNull("on testGetInterest", event.interest);
                assertTrue("on testGetInterest", event.interest instanceof Interest);

                // WE ARE DONE
                doneTest();
            }
        });

        Interest interest = new Interest();
        interest.name = "interest";

        api.createInterest(ParseHelper.createTypedInputFromModel(interest), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response.getAsJsonObject().get("data"));
                bus.post(new InterestEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 Method not allowed !!!
    @Test
    public void testUpdateInterest() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onCreateInterestEvent(InterestEvent event) {
                assertNotNull("on testUpdateInterest", event.interest);
                assertTrue("on testUpdateInterest", event.interest instanceof Interest);

                // WE ARE DONE
                doneTest();
            }
        });

        Interest interest = new Interest();
        interest.name = "interest";

        api.updateInterest(id, ParseHelper.createTypedInputFromModelByMethod(interest,"put"), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response.getAsJsonObject().get("data"));
                bus.post(new InterestEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 Method not allowed !!!
    @Test
    public void testDeleteInterest() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onDeleteInterestEvent(InterestEvent event) {
                assertNotNull("on testDeleteInterest", event.interest);
                assertTrue("on testUpdateInterest", event.interest instanceof Interest);

                // WE ARE DONE
                doneTest();
            }
        });

        api.updateInterest(id, ParseHelper.createTypedInputFromOneKeyValue("_method","delete"), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response.getAsJsonObject().get("data"));
                bus.post(new InterestEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo : 400 Bad request Error
    @Test
    public void testSuggestInterests() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetInterestsEvent(InterestsEvent event) {
                assertNotNull("on testGetInterests", event.interests);
                assertTrue("on testGetInterests", event.interests.get(0) instanceof Interest);
                assertTrue("on testGetInterests", event.interests.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        String search = "international development";

        api.suggestInterests(search, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Interest> interests = Interest.createListInterestsFromJson(responseAsArray);
                bus.post(new InterestsEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }
}
