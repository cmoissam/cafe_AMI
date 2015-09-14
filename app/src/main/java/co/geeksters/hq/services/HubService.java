package co.geeksters.hq.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.UnauthorizedFailureEvent;
import co.geeksters.hq.events.success.AmbassadorsEvent;
import co.geeksters.hq.events.success.HubEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.interfaces.HubInterface;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HubService {

    public final HubInterface api;

    public HubService(String token) {
        this.api = BaseService.adapterWithToken(token).create(HubInterface.class);
    }

    public void listAllHubs() {

        this.api.listAllHubs(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Hub> hubs = Hub.createListHubsFromJson(responseAsArray);
                BaseApplication.post(new HubsEvent(hubs));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listHubsForMember(int userId) {

        this.api.listHubsForMember(userId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Hub> hubs = Hub.createListHubsFromJson(responseAsArray);
                BaseApplication.post(new HubsEvent(hubs));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubInfo(int hubId) {

        this.api.getHubInfo(hubId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub hub = Hub.createHubFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new HubEvent(hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubMembers(int hubId) {

        this.api.getHubMembers(hubId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Member> members_of_hub = Member.createListUsersFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                // BaseApplication.post(new ConnectionFailureEvent());
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubAmbassadors(int hubId) {

        this.api.getHubAmbassadors(hubId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Member> members_of_hub = Member.createListUsersFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new AmbassadorsEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createHub(Hub hub) {

        this.api.createHub(ParseHelpers.createTypedInputFromModel(hub), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub createdHub = Hub.createHubFromJson(response);
                BaseApplication.post(new HubEvent(createdHub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateHub(int hubId, String name, String imageUrl, ArrayList<Member> ambassadors, ArrayList<Member> members) {

        Hub hub = new Hub();
        hub.name = name;
        hub.image = imageUrl;
        hub.members = members;
        hub.ambassadors = ambassadors;

        this.api.updateHub(hubId, ParseHelpers.createTypedInputFromModel(hub), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub updatedHub = Hub.createHubFromJson(response);
                BaseApplication.post(new HubEvent(updatedHub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateImageHub(int hubId, File imageFile) {

        JSONObject jsonFile = new JSONObject();
        try {
            jsonFile.put("file", imageFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.api.updateImageHub(hubId, ParseHelpers.createTypedInputFromJsonObject(jsonFile), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub updatedHub = Hub.createHubFromJson(response);
                BaseApplication.post(new HubEvent(updatedHub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateHubAmbassadors(int hubId, ArrayList<Integer> ids) {

        JSONObject jsonAmbassadors = new JSONObject();
        try {
            jsonAmbassadors.put("ambassadors", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.api.updateHubAmbassadors(hubId, ParseHelpers.createTypedInputFromJsonObject(jsonAmbassadors), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub updatedHub = Hub.createHubFromJson(response);
                BaseApplication.post(new HubEvent(updatedHub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteHub(int hubId) {

        this.api.deleteHub(hubId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub deleted_hub = Hub.createHubFromJson(response);
                BaseApplication.post(new HubEvent(deleted_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
