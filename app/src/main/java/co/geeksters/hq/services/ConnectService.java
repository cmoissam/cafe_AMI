package co.geeksters.hq.services;

import android.util.Log;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * Created by soukaina on 27/11/14.
 */
public class ConnectService {

    public final ConnectInterface api;

    public ConnectService() {
        this.api = BaseService.adapterWithoutToken().create(ConnectInterface.class);
    }

    public void register(Member member) {

        this.api.register(ParseHelper.createTypedInputFromModel(member), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member registredMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                // an email to confirm the current account is sent
                BaseApplication.post(new MemberEvent(registredMember));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }


    public void login(String grantType, int clientId, String clientSecret, String username,
                      String password, String scope) {

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

        this.api.login(ParseHelper.createTypedInputFromJsonObject(loginParams), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                String accessToken = response.getAsJsonObject().get("access_token").toString();
                BaseApplication.post(new LoginEvent(accessToken));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                Log.d("Status Failure", error.getResponse().getStatus() + "");
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
