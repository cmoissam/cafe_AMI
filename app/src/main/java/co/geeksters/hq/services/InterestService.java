package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateInterestEvent;
import co.geeksters.hq.events.success.DeleteInterestEvent;
import co.geeksters.hq.events.success.GetInterestInfoEvent;
import co.geeksters.hq.events.success.ListAllInterestsEvent;
import co.geeksters.hq.events.success.SuggestionsInterestEvent;
import co.geeksters.hq.events.success.UpdateInterestEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.InterestInterface;
import co.geeksters.hq.models.Interest;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InterestService {

    public final InterestInterface api;

    public InterestService(String token) {
        this.api = BaseService.adapterWithToken(token).create(InterestInterface.class);
    }

    public void listAllInterests() {

        this.api.listAllInterests(new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Interest> interests = Interest.createListInterestsFromJson(response);
                BaseApplication.getEventBus().post(new ListAllInterestsEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getInterestInfo(int interest_id) {

        this.api.getInterestInfo(interest_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response);
                BaseApplication.getEventBus().post(new GetInterestInfoEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void createInterest(Interest interest) {

        this.api.createInterest(interest, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest created_interest = Interest.createInterestFromJson(response);
                BaseApplication.getEventBus().post(new CreateInterestEvent(created_interest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateInterest(int interest_id, String name) {

        this.api.updateInterest(interest_id, name, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest updated_interest = Interest.createInterestFromJson(response);
                BaseApplication.getEventBus().post(new UpdateInterestEvent(updated_interest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteInterest(int interest_id) {

        this.api.deleteInterest(interest_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest deleted_interest = Interest.createInterestFromJson(response);
                BaseApplication.getEventBus().post(new DeleteInterestEvent(deleted_interest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionsInterest(String search) {

        this.api.suggestionsInterest(search, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Interest> interests = Interest.createListInterestsFromJson(response);
                BaseApplication.getEventBus().post(new SuggestionsInterestEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

}
