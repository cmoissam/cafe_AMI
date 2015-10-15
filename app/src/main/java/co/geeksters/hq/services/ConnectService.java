package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.ExistingAccountEvent;
import co.geeksters.hq.events.failure.LoginFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.events.success.PasswordResetEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.models.EmailResonse;
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

        this.api.register(ParseHelpers.createTypedInputFromModel(member), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member registredMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                // an email to confirm the current account is sent
                BaseApplication.post(new SaveMemberEvent(registredMember));
            }

            @Override
            public void failure(RetrofitError error) {
                if(error.getResponse().getStatus() == 422){
                    BaseApplication.post(new ExistingAccountEvent());
                }
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

        this.api.login(ParseHelpers.createTypedInputFromJsonObject(loginParams), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                String accessToken = response.getAsJsonObject().get("access_token").toString();
                Member member = Member.createUserFromJson(response.getAsJsonObject().get("member"));
                BaseApplication.post(new LoginEvent(accessToken, member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error != null) {
                    if(error.getResponse() != null) {
                        if (error.getResponse().getStatus() == 400) {
                            BaseApplication.post(new LoginFailureEvent("wrong password"));
                        }
                        else if(error.getResponse().getStatus() == 403)
                        {
                            if(error.getResponse().getReason().equals("need email confirmation"))
                                BaseApplication.post(new LoginFailureEvent("need email confirmation"));
                            else
                            BaseApplication.post(new LoginFailureEvent("not existant email"));
                        }
                        else
                            BaseApplication.post(new ConnectionFailureEvent());
                    }
                    else
                        BaseApplication.post(new ConnectionFailureEvent());
                }
                else BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void passwordReminder(final List<String> emails) {

        this.api.passwordReminder(ParseHelpers.createTypedInputFromOneKeyValue("email", GeneralHelpers.generateEmailsStringFromList(emails)), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                ArrayList<EmailResonse> emailsResponse = new ArrayList<EmailResonse>();

                for (int i = 0; i < GlobalVariables.emails.size(); i++){
                    EmailResonse emailResonse = new EmailResonse();
                    emailResonse.email = GlobalVariables.emails.get(i);
                    emailResonse.status = response.getAsJsonObject().get(GlobalVariables.emails.get(i).trim()).getAsJsonObject().get("status").toString();
                    emailResonse.message = response.getAsJsonObject().get(GlobalVariables.emails.get(i).trim()).getAsJsonObject().get("message").toString();

                    emailsResponse.add(emailResonse);
                }

                BaseApplication.post(new PasswordResetEvent(emailsResponse));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
