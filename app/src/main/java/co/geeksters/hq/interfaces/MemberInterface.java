package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface MemberInterface {

    @POST("/members/logout")
    void logout(Callback<JsonElement> callback);

    @POST("/members/{id}")
    void updateMember(@Path("id") int user_id, @Body Member member, Callback<JsonElement> callback);

    @PUT("/members/{id}/image")
    void updateImageMember(@Path("id") int user_id, @Body File file, Callback<JsonElement> callback);

    @GET("/members")
    void listAllMembers(Callback<JSONArray> callback);

    @GET("/members/list")
    void listAllMembersByPaginationOrSearch(String order, String from, String size, String search, Callback<JSONArray> callback);

    //@Headers("Cache-Control: max-age=14400")
    @GET("/members/{id}")
    void getMemberInfo(@Path("id") int user_id, Callback<JsonElement> callback);

    @GET("/members/search")
    void searchForMembersFromKey(String search, Callback<JSONArray> callback);

    @GET("/members/suggest")
    void suggestionMember(String search, Callback<JSONArray> callback);

    @GET("/members/location")
    void getMembersArroundMe(float radius, Callback<JSONArray> callback);

    @POST("/members/{id}")
    void deleteMember(@Path("id") int user_id, Callback<JsonElement> callback);

    @POST("/members/password/remind")
    void passwordReminder(List<String> emails, Callback<JSONArray> callback);

    // this method is executed from the link sent by email to remind a member to reset his password
    @POST("/members/password/reset")
    void passwordReset(String token, String email, String password, String password_confirmation, Callback<JsonElement> callback);

    @POST("/members/confirmation/send")
    void sendEmailConfirmationOnRegister(int user_id, Callback<JsonElement> callback);

    @POST("/members/confirmation/validate")
    void validateEmailConfirmationOnRegister(String token, Callback<JsonElement> callback);
}
