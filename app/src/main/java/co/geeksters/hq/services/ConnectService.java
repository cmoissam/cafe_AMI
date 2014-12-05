package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 27/11/14.
 */
public class ConnectService {

    public final ConnectInterface api;

    public ConnectService() {
        this.api = BaseService.adapterWithoutToken().create(ConnectInterface.class);
    }

    public void register(Member member) {

        this.api.register(member, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member registred_member = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                // an email to confirm the current account is sent
                BaseApplication.getEventBus().post(new MemberEvent(registred_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }


    public void login(String grant_type, String client_id, String client_secret, String username,
                      String password, String scope) {

        this.api.login(grant_type, client_id, client_secret, username,
        password, scope, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                String access_token = response.getAsJsonObject().get("access_token").toString();
                BaseApplication.getEventBus().post(new LoginEvent(access_token));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }
}
