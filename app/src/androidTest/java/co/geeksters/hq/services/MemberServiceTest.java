package co.geeksters.hq.services;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.eclipsesource.restfuse.Assert;
import com.eclipsesource.restfuse.DefaultCallbackResource;
import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.MediaType;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Request;
import com.eclipsesource.restfuse.annotation.Callback;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.http.HttpHeaders;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.eclipsesource.restfuse.Response;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.EmptyMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by soukaina on 03/12/14.
 */

public class MemberServiceTest extends InstrumentationTestCase {

    Bus bus;
    MemberInterface api;
    String token;

    String success_message;
    public static Boolean doing;
    public static String END_POINT_URL = "http://192.168.0.8:3000";
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
        token = "token";
        api = new RestAdapter.Builder()
                             .setEndpoint(END_POINT_URL)
                             .setRequestInterceptor(new RequestInterceptor() {
                                @Override
                                public void intercept(RequestInterceptor.RequestFacade request) {
                                    request.addHeader("Accept", "application/json");
                                    request.addQueryParam("access_token", token);
                                }
                             })
                            .build()
                            .create(MemberInterface.class);

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
    public void testGetMembers() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertEquals("on testGetMembers", event.members.size(), 12);
                assertNotNull("on testGetMembers",event.members.get(0).companies);
                assertNotNull("on testGetMembers",event.members.get(0).interests);
                assertNotNull("on testGetMembers",event.members.get(0).hub_ids);
                assertEquals("on testGetMembers", event.members.get(0).interests.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).companies.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        api.listAllMembers(new retrofit.Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, retrofit.client.Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                bus.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testGetMembersByPagination() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertEquals("on testGetMembers", event.members.size(), 3);
                assertNotNull("on testGetMembers",event.members.get(0).companies);
                assertNotNull("on testGetMembers",event.members.get(0).interests);
                assertNotNull("on testGetMembers",event.members.get(0).hub_ids);
                assertEquals("on testGetMembers", event.members.get(0).interests.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).companies.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        this.api.listAllMembersByPaginationOrSearch("", "", "", "", new retrofit.Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, retrofit.client.Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                bus.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // A REVOIR: TO DO
    @Test
    public void testSearchMembers() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertEquals("on testGetMembers", event.members.size(), 12);
                assertNotNull("on testGetMembers",event.members.get(0).companies);
                assertNotNull("on testGetMembers",event.members.get(0).interests);
                assertNotNull("on testGetMembers",event.members.get(0).hub_ids);
                assertEquals("on testGetMembers", event.members.get(0).interests.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).companies.size(), 2);
                assertEquals("on testGetMembers", event.members.get(0).hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        this.api.searchForMembersFromKey("", new retrofit.Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, retrofit.client.Response rawResponse) {
                List<Member> members = Member.createListUsersFromJson(response);
                BaseApplication.getEventBus().post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });

        waitTest();
    }

    @Test
    public void testGetMember() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetMemberEvent(MemberEvent event) {
                assertNotNull("on testGetMember",event.member);
                assertNotNull("on testGetMember",event.member.companies);
                assertNotNull("on testGetMember",event.member.interests);
                assertNotNull("on testGetMember",event.member.hub_ids);
                assertEquals("on testGetMember", event.member.interests.size(), 2);
                assertEquals("on testGetMember", event.member.companies.size(), 2);
                assertEquals("on testGetMember", event.member.hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        api.getMemberInfo(1, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member member = Member.createUserFromJson(response);
                bus.post(new MemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testUpdateMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateMemberEvent(MemberEvent event) {
                assertNotNull("on testGetMember",event.member);
                assertNotNull("on testGetMember",event.member.companies);
                assertNotNull("on testGetMember",event.member.interests);
                assertNotNull("on testGetMember",event.member.hub_ids);
                assertEquals("on testGetMember",event.member.interests.size(), 2);
                assertEquals("on testGetMember",event.member.companies.size(), 2);
                assertEquals("on testGetMember",event.member.hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        api.updateMember(1, member, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updated_member = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updated_member));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ConnectionFailureEvent());
            }
        });

        waitTest();
    }

    @Test
    public void testUpdateImageMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateImageMemberEvent(MemberEvent event) {
                assertNotNull("on testGetMember",event.member);
                assertNotNull("on testGetMember",event.member.companies);
                assertNotNull("on testGetMember",event.member.interests);
                assertNotNull("on testGetMember",event.member.hub_ids);
                assertEquals("on testGetMember",event.member.interests.size(), 2);
                assertEquals("on testGetMember",event.member.companies.size(), 2);
                assertEquals("on testGetMember",event.member.hub_ids.length, 3);
                // WE ARE DONE
                doneTest();
            }
        });

        api.updateImageMember(1, new File(""), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updated_member = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updated_member));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ConnectionFailureEvent());
            }
        });

        waitTest();
    }

    @Test
    public void testLogoutMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onLogoutMemberEvent(EmptyMemberEvent event) {
                assertEquals("on testLogoutMember",success_message, "success");
                // WE ARE DONE
                doneTest();
            }
        });

        api.logout(new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                success_message = response.getAsJsonObject().get("status").getAsString();
                bus.post(new EmptyMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }


}