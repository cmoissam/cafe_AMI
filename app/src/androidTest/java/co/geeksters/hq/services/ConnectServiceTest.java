package co.geeksters.hq.services;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by soukaina on 05/12/14.
 */
public class ConnectServiceTest extends InstrumentationTestCase {

    public static String END_POINT_URL = "http://192.168.0.8:3000";
    Bus bus;
    ConnectInterface api;
    String token;

    public static Boolean doing;
    Member member;
    String success_message;

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
        token = "token";
        api = new RestAdapter.Builder()
                .setEndpoint(END_POINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(ConnectInterface.class);

        HashMap<String, String> socials = new HashMap<String, String>();
        socials.put("facebook", "http://facebook.com");
        socials.put("twitter", "http://twitter.com");

        ArrayList<Todo> todos = new ArrayList<Todo>();
        Todo todo = new Todo(1, "todo content", 2014120418);
        todos.add(todo);

        ArrayList<Interest> interests = new ArrayList<Interest>();
        Interest interest = new Interest(1, "interest name");
        interests.add(interest);

        member = new Member(1, "test full name", "test@email.com", "password", "token", true,
                "member", socials, todos, interests, null, null, null, null);
    }

    @Test
    public void testRegisterMember() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onRegisterMemberEvent(MemberEvent event) {
                assertNotNull("on testRegisterMember",event.member);
                assertEquals("on testRegisterMember", success_message, "Member created and email confirmation has been sent");
                assertNotNull("on testRegisterMember",event.member.companies);
                assertNotNull("on testRegisterMember",event.member.interests);
                assertNotNull("on testRegisterMember",event.member.hub_ids);
                assertEquals("on testRegisterMember",event.member.interests.size(), 2);
                assertEquals("on testRegisterMember",event.member.companies.size(), 2);
                assertEquals("on testRegisterMember",event.member.hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        api.register(member, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                success_message = response.getAsJsonObject().get("message").getAsString();
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
                assertNotNull("on testLoginMember",event.access_token);
                assertEquals("on testLoginMember",event.access_token, "token");
                // WE ARE DONE
                doneTest();
            }
        });

        api.login("", "", "", "",
                "", "", new Callback<JsonElement>() {
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
}
