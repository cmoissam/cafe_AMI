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
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.CompanyInterface;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.interfaces.HubInterface;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 15/12/14.
 */
public class CompanyServiceTest extends InstrumentationTestCase {
    Bus bus;
    CompanyInterface api;
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
                .create(CompanyInterface.class);
    }

    @Test
    public void testGetCompanies() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetCompaniesEvent(CompaniesEvent event) {
                assertNotNull("on testGetCompanies", event.companies);
                assertTrue("on testGetCompanies", event.companies.get(0) instanceof Company);
                assertTrue("on testGetCompanies", event.companies.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        api.listAllCompanies(new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Company> companies = Company.createListCompaniesFromJson(responseAsArray);
                bus.post(new CompaniesEvent(companies));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testGetCompanyInfo() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetCompanyEvent(CompanyEvent event) {
                assertNotNull("on testGetCompany", event.company);
                assertTrue("on testGetCompany", event.company instanceof Company);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getCompanyInfo(id, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonElement responseAsJson = response.getAsJsonObject().get("data");
                Company company = Company.createCompanyFromJson(responseAsJson);
                bus.post(new CompanyEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 402 No authentication challenges found !!!
    @Test
    public void testCreateCompany() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onCreateCompanyEvent(CompanyEvent event) {
                assertNotNull("on testCreateCompany",event.company);
                assertTrue(event.company instanceof Company);

                // WE ARE DONE
                doneTest();
            }
        });

        Company company = new Company();
        company.name = "company";

        api.createCompany(ParseHelper.createTypedInputFromModel(company), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.getEventBus().post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 Method not allowed !!!
    @Test
    public void testUpdateCompany() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateCompanyEvent(CompanyEvent event) {
                assertNotNull("on testUpdateCompany",event.company);
                assertTrue(event.company instanceof Company);

                // WE ARE DONE
                doneTest();
            }
        });

        Company company = new Company();
        company.name = "updated company";

        api.updateCompany(id, ParseHelper.createTypedInputFromModelByMethod(company, "put"), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.getEventBus().post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: 405 Method not allowed !!!
    @Test
    public void testDeleteCompany() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onDeleteCompanyEvent(CompanyEvent event) {
                assertNotNull("on testDeleteCompany",event.company);
                assertTrue(event.company instanceof Company);

                // WE ARE DONE
                doneTest();
            }
        });

        api.deleteCompany(id, ParseHelper.createTypedInputFromOneKeyValue("_method", "delete"), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.getEventBus().post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo : 400 Bad request Error
    @Test
    public void testSuggestCompanies() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetCompaniesEvent(CompaniesEvent event) {
                assertNotNull("on testGetCompanies", event.companies);
                assertTrue("on testGetCompanies", event.companies.get(0) instanceof Company);
                assertTrue("on testGetCompanies", event.companies.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        String search = "soukaina";

        api.suggestCompanies(search, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Company> companies = Company.createListCompaniesFromJson(responseAsArray);
                bus.post(new CompaniesEvent(companies));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }
}