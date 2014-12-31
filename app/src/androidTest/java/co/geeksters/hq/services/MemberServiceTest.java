package co.geeksters.hq.services;

import android.test.InstrumentationTestCase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLConnection;
import java.util.List;

import co.geeksters.hq.events.success.LogoutMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.ConnectInterface;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by soukaina on 03/12/14.
 */

public class MemberServiceTest extends InstrumentationTestCase {

    Bus bus;
    MemberInterface api;
    String token;

    String successMessage;
    public static Boolean doing;
    int id = 780;

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
        this.successMessage = "";
    }

    public void loginMember(String grantType, int clientId, String clientSecret, String username,
                            String password, String scope) throws Exception {
        beforeTest();

        ConnectInterface apiLogin = BaseService.adapterWithoutToken().create(ConnectInterface.class);

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

        apiLogin.login(ParseHelper.createTypedInputFromJsonObject(loginParams), new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                token = response.getAsJsonObject().get("access_token").toString().replace("\"","");
                doneTest();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Override
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);

        try {
            loginMember("password", 1, "pioner911", "soukaina@geeksters.co", "soukaina", "basic");
        } catch (Exception e) {
            e.printStackTrace();
        }

        api = BaseService.adapterWithToken(token)
                            .create(MemberInterface.class);
    }

    @Test
    public void testGetMembers() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers", event.members);
                assertTrue("on testGetMembers", event.members.get(0) instanceof Member);
                assertTrue("on testGetMembers", event.members.size() > 0);

                // WE ARE DONE
                doneTest();
            }
        });

        api.listAllMembers(new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) { // 21s pour avoir la réponse
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Member> members = Member.createListUsersFromJson(responseAsArray);
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

        final int from = 0;
        final int size = 5;
        String order = "desc";
        String col = "id";

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersByPaginationEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertEquals("on testGetMembers", event.members.size(), size);
                assertTrue("on testGetMembers", event.members.get(0) instanceof Member);
                assertEquals("on testGetMembers", event.members.get(0).id, from);

                // WE ARE DONE
                doneTest();
            }
        });

        this.api.listAllMembersByPaginationOrSearch(from, size, order, col, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                bus.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testSearchMembers() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onGetSearchMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertTrue("on testGetMembers", event.members.get(0) instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        api.searchForMembersFromKey("soukaina", new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                bus.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: Bad request !!!
    @Test
    public void testSuggestMembers() throws Exception {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onSuggestMembersEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);
                assertTrue("on testGetMembers", event.members.get(0) instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        String search = "soukaina";
        api.suggestionMember(search, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                bus.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testGetMembersArrountMe() throws Exception {
        beforeTest();

        float radius = (float) 5.0;

        bus.register(new Object() {
            @Subscribe
            public void onGetMembersArroundMeEvent(MembersEvent event) {
                assertNotNull("on testGetMembers",event.members);

                // WE ARE DONE
                doneTest();
            }
        });

        this.api.getMembersArroundMe(id, radius, new retrofit.Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, retrofit.client.Response rawResponse) {
                /* JsonArray sources = response.getAsJsonObject().get("result").getAsJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for (int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("_source"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                bus.post(new MembersEvent(members));*/
            }

            @Override
            public void failure(RetrofitError error) {
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
                assertTrue(event.member instanceof Member);

                // WE ARE DONE
                doneTest();
            }
        });

        api.getMemberInfo(5, new retrofit.Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member member = Member.createUserFromJson(response.getAsJsonObject().get("data"));
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
                assertNotNull("on updateUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        Member member = new Member();
        member.fullName = "soukaina";
        member.email = "soukaina@geeksters.co";

        api.updateMember(id, "put", token, member.fullName, member.email, null, null,
                null, null, null, false, false, false, false, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                //422 : email exists
            }
        });

        waitTest();
    }

    // Todo: retrofit.RetrofitError: /home/soukaina/Images/capture.png: open failed: ENOENT (No such file or directory) !!!
    @Test
    public void testUpdateImageMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateImageMemberEvent(MemberEvent event) {
                assertNotNull("on testUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        /*Context context = new LoginActivity();
        Bitmap bitMap = null;
        try {
            bitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File mFile1 = Environment.getExternalStorageDirectory();

        String fileName ="img1.png";

        File mFile2 = new File(mFile1,fileName);
        try {
            FileOutputStream outStream;

            outStream = new FileOutputStream(mFile2);

            bitMap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String    sdPath = mFile1.getAbsolutePath().toString()+"/"+fileName;

        Log.i("MAULIK", "Your IMAGE ABSOLUTE PATH:-"+sdPath);

        File temp=new File(sdPath);

        if(!temp.exists()){
            Log.e("file","no image file at location :"+sdPath);
        }*/

        final String uploadFilePath = "/mnt/sdcard/";
        final String uploadFileName = "service_lifecycle.png";

        //uploadFile(uploadFilePath + "" + uploadFileName);
        File sourceFile = new File(uploadFilePath + "" + uploadFileName);

        if (sourceFile.isFile()){
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        File file = new File("/home/soukaina/Images/capture.png");
        api.updateImageMember(id, new TypedString(token), new TypedFile(URLConnection.guessContentTypeFromName(file.getName()), file), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: to delete !!!
    @Test
    public void testUpdateStatusMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateStatusMemberEvent(MemberEvent event) {
                assertNotNull("on testUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        boolean ambassador = true;

        api.updateStatusMember(id, ParseHelper.createTypedInputFromOneKeyValue("ambassador", ambassador), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: to delete !!!
    @Test
    public void testUpdateTokenMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateTokenMemberEvent(MemberEvent event) {
                assertNotNull("on testUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        api.updateTokenMember(id, ParseHelper.createTypedInputFromOneKeyValue("device_token", token), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: to delete !!!
    @Test
    public void testupdateNotifyOptionsMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateNotifyOptionsMemberEvent(MemberEvent event) {
                assertNotNull("on testUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        Boolean notifyByEmailOnComment = true;
        Boolean notifyByPushOnComment = true;
        Boolean notifyByEmailOnTodo = true;
        Boolean notifyByPushOnTodo = true;

        JSONObject jsonUpdateNotifMember = new JSONObject();
        try {
            jsonUpdateNotifMember.put("notify_by_email_on_comment", notifyByEmailOnComment)
                                 .put("notify_by_push_on_comment", notifyByPushOnComment)
                                 .put("notify_by_email_on_todo", notifyByEmailOnTodo)
                                 .put("notify_by_push_on_todo", notifyByPushOnTodo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.updateNotifyOptionsMember(id, ParseHelper.createTypedInputFromJsonObject(jsonUpdateNotifMember), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    // Todo: to delete !!!
    @Test
    public void testupdateLocationMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onUpdateLocationEvent(MemberEvent event) {
                assertNotNull("on testUpdateMember",event.member);

                // WE ARE DONE
                doneTest();
            }
        });

        float latitude = (float) 34.776466;
        float longitude = (float) 39.776466;

        JSONObject jsonUpdateLocationMember = new JSONObject();

        try {
            jsonUpdateLocationMember.put("latitude", latitude)
                                    .put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        api.updateLocationMember(id, ParseHelper.createTypedInputFromJsonObject(jsonUpdateLocationMember), new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                bus.post(new MemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testDeleteMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onDeleteMemberEvent(LogoutMemberEvent event) {
                assertNotNull("on testDeleteMember", successMessage);
                assertEquals("on testDeleteMember", successMessage, "Member successfully deleted");

                // WE ARE DONE
                doneTest();
            }
        });

        api.deleteMember(id, "delete", token, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                successMessage = response.getAsJsonObject().get("message").toString();
                bus.post(new LogoutMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }

    @Test
    public void testLogoutMember() {
        beforeTest();

        bus.register(new Object() {
            @Subscribe
            public void onLogoutMemberEvent(LogoutMemberEvent event) {
                assertEquals("on testLogoutMember",successMessage, "success");

                // WE ARE DONE
                doneTest();
            }
        });

        api.logout(token, new retrofit.Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, retrofit.client.Response rawResponse) {
                successMessage = response.getAsJsonObject().get("status").getAsString();
                bus.post(new LogoutMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        waitTest();
    }
}