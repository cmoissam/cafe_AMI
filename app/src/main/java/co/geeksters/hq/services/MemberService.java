package co.geeksters.hq.services;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.GPSFailureEvent;
import co.geeksters.hq.events.failure.UnauthorizedFailureEvent;
import co.geeksters.hq.events.success.DeleteMemberEvent;
import co.geeksters.hq.events.success.EmptyEvent;
import co.geeksters.hq.events.success.MembersAroundMeEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.MembersSearchEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.events.success.SaveMemberForLogoutEvent;
import co.geeksters.hq.events.success.UpdateMemberLocationEvent;
import co.geeksters.hq.events.success.UploadImageEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.interfaces.MemberInterface;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

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
                BaseApplication.post(new EmptyEvent());
            }

            @Override
            public void failure(RetrofitError error) {

                if(error == null)
                    BaseApplication.post(new ConnectionFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }



    public void updateMember(int userId, Member member) {

        // TODO : Hub update
        if (member.hub == null) {
            if(member.social != null)
            {
            this.api.updateMember(userId, "put", this.token, member.fullName, member.email, "", member.blurp,member.goal,member.phone,member.whatsapp, member.social.twitter, member.social.facebook,
                    member.social.linkedin, member.social.skype, member.social.blog, member.social.website, member.social.other, member.interestsToUpdate(), member.companiesToUpdate(),
                    member.latitude, member.longitude,
                    member.notifyByEmailOnComment, member.notifyByPushOnComment, member.notifyByEmailOnTodo, member.notifyByPushOnTodo, member.radarVisibility, member.deviceToken, member.deviceType,
                    new Callback<JsonElement>() {

                        @Override
                        public void success(JsonElement response, Response rawResponse) {
                            Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                            if (!GlobalVariables.updatePositionFromRadar)
                                if(GlobalVariables.inMyProfileFragment)
                                    BaseApplication.post(new SaveMemberForLogoutEvent());
                            else
                                    BaseApplication.post(new SaveMemberEvent(updatedMember));
                            else
                                BaseApplication.post(new UpdateMemberLocationEvent(updatedMember));
                        }

                        @Override
                        public void failure(RetrofitError error) {

                            if(error == null)
                                BaseApplication.post(new UnauthorizedFailureEvent());
                            else
                            if (error.getResponse() == null) {
                                BaseApplication.post(new UnauthorizedFailureEvent());
                            } else if (error.getResponse() != null) {
                                if (error.getResponse().getStatus() == 401) {
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                }
                            } else
                                BaseApplication.post(new ConnectionFailureEvent());
                        }
                    });}else{
                this.api.updateMember(userId, "put", this.token, member.fullName, member.email, "", member.blurp,member.goal,member.phone,member.whatsapp, "","",
                        "", "","", "", "", member.interestsToUpdate(), member.companiesToUpdate(),
                        member.latitude, member.longitude,
                        member.notifyByEmailOnComment, member.notifyByPushOnComment, member.notifyByEmailOnTodo, member.notifyByPushOnTodo, member.radarVisibility, member.deviceToken, member.deviceType,
                        new Callback<JsonElement>() {

                            @Override
                            public void success(JsonElement response, Response rawResponse) {
                                Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                                if (!GlobalVariables.updatePositionFromRadar)
                                    if(GlobalVariables.inMyProfileFragment)
                                        BaseApplication.post(new SaveMemberForLogoutEvent());
                                    else
                                        BaseApplication.post(new SaveMemberEvent(updatedMember));
                                else
                                    BaseApplication.post(new UpdateMemberLocationEvent(updatedMember));
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                if(error == null)
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                else
                                if (error.getResponse() == null) {
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                } else if (error.getResponse() != null) {
                                    if (error.getResponse().getStatus() == 401) {
                                        BaseApplication.post(new UnauthorizedFailureEvent());
                                    }
                                } else
                                    BaseApplication.post(new ConnectionFailureEvent());
                            }
                        });
            }
        } else {
            if(member.social != null)
            {
            this.api.updateMember(userId, "put", this.token, member.fullName, member.email, member.hub.name, member.blurp,member.goal,member.phone,member.whatsapp, member.social.twitter, member.social.facebook,
                    member.social.linkedin, member.social.skype, member.social.blog, member.social.website, member.social.other, member.interestsToUpdate(), member.companiesToUpdate(),
                    member.latitude, member.longitude,
                    member.notifyByEmailOnComment, member.notifyByPushOnComment, member.notifyByEmailOnTodo, member.notifyByPushOnTodo, member.radarVisibility, member.deviceToken, member.deviceType,
                    new Callback<JsonElement>() {

                        @Override
                        public void success(JsonElement response, Response rawResponse) {
                            Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                            if (!GlobalVariables.updatePositionFromRadar)
                                if(GlobalVariables.inMyProfileFragment)
                                    BaseApplication.post(new SaveMemberForLogoutEvent());
                                else
                                    BaseApplication.post(new SaveMemberEvent(updatedMember));
                            else
                                BaseApplication.post(new UpdateMemberLocationEvent(updatedMember));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if(error == null)
                                BaseApplication.post(new UnauthorizedFailureEvent());
                            else  if (error.getResponse() == null) {
                                BaseApplication.post(new UnauthorizedFailureEvent());
                            } else if (error.getResponse() != null) {
                                if (error.getResponse().getStatus() == 401) {
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                }
                            } else
                                BaseApplication.post(new ConnectionFailureEvent());
                        }
                    });}else{

                this.api.updateMember(userId, "put", this.token, member.fullName, member.email, member.hub.name, member.blurp,member.goal,member.phone,member.whatsapp, "","",
                        "", "","", "", "", member.interestsToUpdate(), member.companiesToUpdate(),
                        member.latitude, member.longitude,
                        member.notifyByEmailOnComment, member.notifyByPushOnComment, member.notifyByEmailOnTodo, member.notifyByPushOnTodo, member.radarVisibility, member.deviceToken, member.deviceType,
                        new Callback<JsonElement>() {

                            @Override
                            public void success(JsonElement response, Response rawResponse) {
                                Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                                if (!GlobalVariables.updatePositionFromRadar)
                                    if(GlobalVariables.inMyProfileFragment)
                                        BaseApplication.post(new SaveMemberForLogoutEvent());
                                    else
                                        BaseApplication.post(new SaveMemberEvent(updatedMember));
                                else
                                    BaseApplication.post(new UpdateMemberLocationEvent(updatedMember));
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                if(error == null)
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                else  if (error.getResponse() == null) {
                                    BaseApplication.post(new UnauthorizedFailureEvent());
                                } else if (error.getResponse() != null) {
                                    if (error.getResponse().getStatus() == 401) {
                                        BaseApplication.post(new UnauthorizedFailureEvent());
                                    }
                                } else
                                    BaseApplication.post(new ConnectionFailureEvent());
                            }
                        });




            }
        }
    }

    public void updateImage(int userId,TypedFile file) {
        //uploadImage(@Path("id") int userId,@Part("access_token") String token,
        // @Part("_method") String method, @Part("file") File file, Callback<JsonElement> callback);

        this.api.uploadImage(userId, this.token, "put", file, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new UploadImageEvent(updatedMember.image));
            }

            @Override
            public void failure(RetrofitError error) {
                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }


    public void updateStatusMember(int userId, boolean ambassador) {

        this.api.updateStatusMember(userId, ParseHelpers.createTypedInputFromOneKeyValue("ambassador", ambassador), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new SaveMemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateTokenMember(int userId) {

        this.api.updateTokenMember(userId, ParseHelpers.createTypedInputFromOneKeyValue("device_token", this.token), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new SaveMemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {

                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else
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

        this.api.updateNotifyOptionsMember(userId, ParseHelpers.createTypedInputFromJsonObject(jsonUpdateNotifMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new SaveMemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else
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

        this.api.updateLocationMember(userId, ParseHelpers.createTypedInputFromJsonObject(jsonUpdateLocationMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member updatedMember = Member.createUserFromJson(response);
                BaseApplication.post(new SaveMemberEvent(updatedMember));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
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
                if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllMembersByPaginationOrSearch(int from, int size, String order, String col) {

        this.api.listAllMembersByPaginationOrSearch(from, size, order, col, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonArray();

                List<Member> members = Member.createListUsersFromJson(sources);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllMembersByPaginationForTodo(int from, int size, String order, String col) {

        this.api.listAllMembersByPaginationForTodo(from, size, order, col, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonArray();

                List<Member> members = Member.createListUsersFromJson(sources);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
    public void searchForMembersForTodo(String search, int from, int size, String order, String col) {

        this.api.searchForMembersForTodo(search, from, size, order, col, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for (int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject());
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersSearchEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if (error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else if (error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                } else if (error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                } else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMemberInfo(int userId) {

        this.api.getMemberInfo(userId, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member member = Member.createUserFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new SaveMemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {

                if(error == null)
                    BaseApplication.post(new ConnectionFailureEvent());
                else
                // popup to inform the current user of the failure
                if(error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void searchForMembersFromKey(String search, int from, int size, String order, String col) {

        this.api.searchForMembersFromKey(search,from, size, order, col, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("result").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject());
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersSearchEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionMember(String search) {

        this.api.suggestionMember(search, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray sources = response.getAsJsonObject().get("members").getAsJsonArray();
                JsonArray responseAsArray = new JsonArray();

                for(int i = 0; i < sources.size(); i++) {
                    responseAsArray.add(sources.get(i).getAsJsonObject().get("payload"));
                }

                List<Member> members = Member.createListUsersFromJson(responseAsArray);
                BaseApplication.post(new MembersEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse().getStatus() == 401) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getMembersArroundMe(int userId, float radius) {

        this.api.getMembersArroundMe(userId, radius, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray data = response.getAsJsonObject().get("data").getAsJsonArray();

                List<Member> members = Member.createListUsersFromJson(data);
                BaseApplication.post(new MembersAroundMeEvent(members));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                BaseApplication.post(new GPSFailureEvent());
            }
        });
    }

    public void deleteMember(int userId) {

        this.api.deleteMember(userId, "delete", this.token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new DeleteMemberEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
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

        this.api.passwordReset(ParseHelpers.createTypedInputFromJsonObject(jsonResetPasswordMember), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void sendEmailConfirmationOnRegister(int userId) {

        this.api.sendEmailConfirmationOnRegister(ParseHelpers.createTypedInputFromOneKeyValue("id", userId), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Member member = Member.createUserFromJson(response);
                BaseApplication.post(new SaveMemberEvent(member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void validateEmailConfirmationOnRegister(String token) {

        this.api.validateEmailConfirmationOnRegister(ParseHelpers.createTypedInputFromOneKeyValue("token", token), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                BaseApplication.post(new EmptyEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

}
