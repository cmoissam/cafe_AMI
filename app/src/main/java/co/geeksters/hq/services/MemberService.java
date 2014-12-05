package co.geeksters.hq.services;

import android.util.Log;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.GPSFailureEvent;
import co.geeksters.hq.events.success.EmptyMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MemberService {

    public final MemberInterface api;
    public String token;

    public MemberService(String token) {
        this.api = BaseService.adapterWithToken(token).create(MemberInterface.class);
        this.token = token;
    }

    public void logout() {
        this.api.logout(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.getEventBus().post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateMember(int user_id, Member member) {

        this.api.updateMember(user_id, member, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updated_member = Member.createUserFromJson(response);
                BaseApplication.getEventBus().post(new MemberEvent(updated_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateImageMember(int user_id, File file) {

        this.api.updateImageMember(user_id, file, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updated_member = Member.createUserFromJson(response);
                BaseApplication.getEventBus().post(new MemberEvent(updated_member));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllMembers(int user_id, File file) {

        this.api.listAllMembers(new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }


    public void listAllMembersByPaginationOrSearch(String order, String from, String size, String search) {

        this.api.listAllMembersByPaginationOrSearch(order, from, size, search, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMemberInfo(int user_id) {

        this.api.getMemberInfo(user_id, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                Log.d("Success", "");
                Member member = Member.createUserFromJson(response);
                BaseApplication.getEventBus().post(new MemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                Log.d("Failure", "");
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void searchForMembersFromKey(String search) {

        this.api.searchForMembersFromKey(search, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionMember(String search) {

        this.api.suggestionMember(search, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMembersArroundMe(float radius) {

        this.api.getMembersArroundMe(radius, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new GPSFailureEvent());
            }
        });
    }

    public void deleteMember(int user_id) {

        this.api.deleteMember(user_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                //Member deleted_member = Member.createUserFromJson(response);
                BaseApplication.getEventBus().post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void passwordReminder(List<String> emails) {

        this.api.passwordReminder(emails, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                BaseApplication.getEventBus().post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void passwordReset(String token, String email, String password, String password_confirmation) {

        this.api.passwordReset(token, email, password, password_confirmation, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.getEventBus().post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void sendEmailConfirmationOnRegister(int user_id) {

        this.api.sendEmailConfirmationOnRegister(user_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member member = Member.createUserFromJson(response);
                BaseApplication.getEventBus().post(new MemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void validateEmailConfirmationOnRegister() {

        this.api.validateEmailConfirmationOnRegister(this.token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.getEventBus().post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

}
