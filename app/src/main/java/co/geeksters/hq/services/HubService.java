package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.io.File;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateHubEvent;
import co.geeksters.hq.events.success.DeleteHubEvent;
import co.geeksters.hq.events.success.GetHubAmbassadorsEvent;
import co.geeksters.hq.events.success.GetHubInfoEvent;
import co.geeksters.hq.events.success.ListAllHubsEvent;
import co.geeksters.hq.events.success.ListHubsForMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.UpdateHubEvent;
import co.geeksters.hq.events.success.UpdateImageHubEvent;
import co.geeksters.hq.global.BaseApplication;
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

        this.api.listAllHubs(new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Hub> hubs = Hub.createListHubsFromJson(response);
                BaseApplication.getEventBus().post(new ListAllHubsEvent(hubs));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void listHubsForMember(int user_id) {

        this.api.listHubsForMember(user_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Hub> hubs_for_member = Hub.createListHubsFromJson(response);
                BaseApplication.getEventBus().post(new ListHubsForMemberEvent(hubs_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubInfo(int hub_id) {

        this.api.getHubInfo(hub_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub hub = Hub.createHubFromJson(response);
                BaseApplication.getEventBus().post(new GetHubInfoEvent(hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubMembers(int hub_id) {

        this.api.getHubMembers(hub_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                //List<Member> members_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.getEventBus().post(new MembersEvent(members_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getHubAmbassadors(int hub_id) {

        this.api.getHubAmbassadors(hub_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                //List<Member> ambassadors_of_hub = Member.createListUsersFromJson(response);
                //BaseApplication.getEventBus().post(new GetHubAmbassadorsEvent(ambassadors_of_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void createHub(Hub hub) {

        this.api.createHub(hub, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub created_hub = Hub.createHubFromJson(response);
                BaseApplication.getEventBus().post(new CreateHubEvent(created_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateHub(int hub_id, String name, String image_url, List<Member> ambassadors, List<Member> members) {

        this.api.updateHub(hub_id, name, image_url, ambassadors, members, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub updated_hub = Hub.createHubFromJson(response);
                BaseApplication.getEventBus().post(new UpdateHubEvent(updated_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateImageHub(int hub_id, File image_file) {

        this.api.updateImageHub(hub_id, image_file, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub updated_hub = Hub.createHubFromJson(response);
                BaseApplication.getEventBus().post(new UpdateImageHubEvent(updated_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteHub(int hub_id) {

        this.api.deleteHub(hub_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Hub deleted_hub = Hub.createHubFromJson(response);
                BaseApplication.getEventBus().post(new DeleteHubEvent(deleted_hub));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }
}
