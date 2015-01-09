package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.InterestEvent;
import co.geeksters.hq.events.success.InterestsEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.ParseHelpers;
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

        this.api.listAllInterests(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Interest> interests = Interest.createListInterestsFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new InterestsEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getInterestInfo(int interestId) {

        this.api.getInterestInfo(interestId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest interest = Interest.createInterestFromJson(response);
                BaseApplication.post(new InterestEvent(interest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createInterest(Interest interest) {

        this.api.createInterest(ParseHelpers.createTypedInputFromModel(interest), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest createdInterest = Interest.createInterestFromJson(response);
                BaseApplication.post(new InterestEvent(createdInterest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateInterest(int interestId, Interest interest) {

        this.api.updateInterest(interestId, ParseHelpers.createTypedInputFromModel(interest), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest updatedInterest = Interest.createInterestFromJson(response);
                BaseApplication.post(new InterestEvent(updatedInterest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteInterest(int interestId) {

        this.api.deleteInterest(interestId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Interest deletedInterest = Interest.createInterestFromJson(response);
                BaseApplication.post(new InterestEvent(deletedInterest));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionsInterest(String search) {

        this.api.suggestInterests(search, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Interest> interests = Interest.createListInterestsFromJson(response.getAsJsonObject().get("data").getAsJsonArray());
                BaseApplication.post(new InterestsEvent(interests));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

}
