package co.geeksters.hq.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.GPSFailureEvent;
import co.geeksters.hq.events.success.EmptyMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Social;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

public class MemberService {

    public final MemberInterface api;
    public String token;

    public MemberService(String token) {
        this.api = BaseService.adapterWithToken(token).create(MemberInterface.class);
        this.token = token;
    }

    public void logout() {
        this.api.logout(this.token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateMember(int userId, Member member) {

/*
        @Path("id") int userId, @Field("_method") String method, @Field("access_token") String token, @Field("full_name") String fullName,
        @Field("email") String email, @Field("hub") Hub hub, @Field("blurp") String blurp, @Field("social") Social social,
        @Field("interests") List<Interest> interests, @Field("companies") List<Company> companies, @Field("updated_at") String updatedAt,
*/

        // TODO : Hub update
        this.api.updateMember(userId, "put", this.token, member.fullName, member.email, null, member.blurp, null, null, null,
                member.notifyByEmailOnComment, member.notifyByPushOnComment, member.notifyByEmailOnTodo, member.notifyByPushOnTodo,
                new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateImageMember(int userId, File file) {

        /*JSONObject jsonUpdateImageMember = new JSONObject();
        try {
            jsonUpdateImageMember.put("id", userId)
                                 .put("file", file);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String mimeType = null;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TypedFile fileTyped = new TypedFile(mimeType, file);

        this.api.updateImageMember(userId, new TypedString(this.token), fileTyped, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateStatusMember(int userId, boolean ambassador) {

        this.api.updateStatusMember(userId, ParseHelper.createTypedInputFromOneKeyValue("ambassador", ambassador), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateTokenMember(int userId) {

        this.api.updateTokenMember(userId, ParseHelper.createTypedInputFromOneKeyValue("device_token", this.token), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateNotifyOptionsMember(int userId, Boolean notifyByEmailOnComment, Boolean notifyByPushOnComment,
                                          Boolean notifyByEmailOnTodo, Boolean notifyByPushOnTodo) {

        JSONObject jsonUpdateNotifMember = new JSONObject();
        try {
            jsonUpdateNotifMember.put("notify_by_email_on_comment", notifyByEmailOnComment)
                                 .put("notify_by_push_on_comment", notifyByPushOnComment)
                                 .put("notify_by_email_on_todo", notifyByEmailOnTodo)
                                 .put("notify_by_push_on_todo", notifyByPushOnTodo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.api.updateNotifyOptionsMember(userId, ParseHelper.createTypedInputFromJsonObject(jsonUpdateNotifMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateLocationMember(int userId, float latitude, float longitude) {

        JSONObject jsonUpdateLocationMember = new JSONObject();

        try {
            jsonUpdateLocationMember.put("latitude", latitude)
                                    .put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.api.updateLocationMember(userId, ParseHelper.createTypedInputFromJsonObject(jsonUpdateLocationMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllMembers() {

        this.api.listAllMembers(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }


    public void listAllMembersByPaginationOrSearch(int from, int size, String order, String col) {

        this.api.listAllMembersByPaginationOrSearch(from, size, order, col, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMemberInfo(int userId) {

        this.api.getMemberInfo(userId, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member member = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new MemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void searchForMembersFromKey(String search) {

        this.api.searchForMembersFromKey(search, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionMember(String search) {

        this.api.suggestionMember(search, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMembersArroundMe(int userId, float radius) {

        this.api.getMembersArroundMe(userId, radius, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                //List<Member> members = Member.createListUsersFromJson(response);
                //BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new GPSFailureEvent());
            }
        });
    }

    public void deleteMember(int userId) {

        this.api.deleteMember(userId, "delete", this.token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    /*public void passwordReminder(ArrayList<String> emails) {

        this.api.passwordReminder(ParseHelper.createTypedInputFromOneKeyValue("email", GeneralHelpers.generateEmailsStringFromList(emails)), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }*/

    public void passwordReset(String token, String email, String password, String passwordConfirmation) {

        JSONObject jsonResetPasswordMember = new JSONObject();

        try {
            jsonResetPasswordMember.put("token", token)
                                    .put("email", email)
                                    .put("password", password)
                                    .put("password_confirmation", passwordConfirmation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.api.passwordReset(ParseHelper.createTypedInputFromJsonObject(jsonResetPasswordMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void sendEmailConfirmationOnRegister(int userId) {

        this.api.sendEmailConfirmationOnRegister(ParseHelper.createTypedInputFromOneKeyValue("id", userId), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member member = Member.createUserFromJson(response);
                BaseApplication.post(new MemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void validateEmailConfirmationOnRegister(String token) {

        this.api.validateEmailConfirmationOnRegister(ParseHelper.createTypedInputFromOneKeyValue("token",token), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

}
