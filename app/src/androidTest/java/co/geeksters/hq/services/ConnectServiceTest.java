package co.geeksters.hq.services;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import co.geeksters.hq.events.success.EmptyMemberEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 05/12/14.
 */
public class ConnectServiceTest extends InstrumentationTestCase {

    Bus bus;
    ConnectInterface api;
    String token;

    public static Boolean doing;
    Member member;

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
    }

    @Override
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);
        api = BaseService.adapterWithoutToken().create(ConnectInterface.class);

        /*HashMap<String, String> socials = new HashMap<String, String>();
        socials.put("facebook", "http://facebook.com");
        socials.put("twitter", "http://twitter.com");

        ArrayList<Todo> todos = new ArrayList<Todo>();
        Todo todo = new Todo(1, "todo content", 2014120418);
        todos.add(todo);

        ArrayList<Interest> interests = new ArrayList<Interest>();
        Interest interest = new Interest(1, "interest name");
        interests.add(interest);

        member = new Member(1, "test full name", "test@email.com", "password", "password", "token", true,
                false, socials, todos, interests, null, null, null, null);*/

        Random rand = new Random();
        int randomNum = rand.nextInt((1000 - 0) + 1);

        member = new Member();
        member.fullName = "soukaina";
        //member.email = "test" + randomNum + ".mjahed@gmail.com";
        member.email = "soukaina.mjahed@gmail.com";
        member.password = "soukaina";
        member.passwordConfirmation = "soukaina";
    }

    @Test
    public void testRegisterMember() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onRegisterMemberEvent(MemberEvent event) {
                assertNotNull("on testRegisterMember",event.member);
                assertTrue(event.member instanceof Member);
                // WE ARE DONE
                doneTest();
            }
        });

        api.register(ParseHelper.createTypedInputFromModel(member), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // success_message = response.getAsJsonObject().get("message").getAsString();
                // JsonElement jsonObject = response.getAsJsonObject().get("data");
                Member registred_member = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                // an email to confirm the current account is sent
                bus.post(new MemberEvent(registred_member));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testLoginMember() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onLoginMemberEvent(LoginEvent event) {
                assertNotNull("on testLoginMember",event.accessToken);
                assertTrue(event.accessToken instanceof String);
                token = event.accessToken;
                // WE ARE DONE
                doneTest();
            }
        });

        JSONObject loginParams = new JSONObject();
        loginParams.put("grant_type", "password")
                .put("client_id", new Integer(1))
                .put("client_secret", "pioner911")
                .put("username", "dam@geeksters.co")
                .put("password", "hq43viable")
                .put("scope", "basic");

        api.login(ParseHelper.createTypedInputFromModel(loginParams), new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                String access_token = response.getAsJsonObject().get("access_token").toString();
                bus.post(new LoginEvent(access_token));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testPasswordReminderMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onPasssswordRemindEvent(EmptyMemberEvent event) {
                // assertEquals("on testPasswordRemindMember",successMessage, "success");

                // WE ARE DONE
                doneTest();
            }
        });

        final ArrayList<String> emails = new ArrayList<String>();
        emails.add("soukaina@geeksters.co");
        emails.add("soukaina.mjahed@gmail.com");

        api.passwordReminder(ParseHelper.createTypedInputFromOneKeyValue("email", GeneralHelpers.generateEmailsStringFromList(emails)), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                // successMessage = response.getAsJsonObject().get(emails.get(0)).getAsJsonObject().get("status").toString();
                bus.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }
}