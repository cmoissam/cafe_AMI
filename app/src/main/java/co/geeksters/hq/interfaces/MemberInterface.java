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
import co.geeksters.hq.models.Social;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedString;

public interface MemberInterface {

    @FormUrlEncoded
    @POST("/member/logout")
    void logout(@Field("access_token") String token, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/{id}")
    void updateMember(@Path("id") int userId, @Field("_method") String method, @Field("access_token") String token, @Field("full_name") String fullName,
                      @Field("email") String email, @Field("hub") Hub hub, @Field("blurp") String blurp, @Field("social") Social social,
                      @Field("interests") List<Interest> interests, @Field("companies") List<Company> companies, @Field("notify_by_email_on_comment") Boolean notifyByEmailOnComment,
                      @Field("notify_by_push_on_comment") Boolean notifyByPushOnComment, @Field("notify_by_email_on_todo") Boolean notifyByEmailOnTodo,
                      @Field("notify_by_push_on_todo") Boolean notifyByPushOnTodo, Callback<JsonElement> callback);

    @Multipart
    @POST("/member/profile/image")
    void updateImageMember(@Part("id") int id, @Part("access_token") TypedString token, @Part("file") TypedFile photo, Callback<JsonElement> callback);

    @GET("/members")
    void listAllMembers(Callback<JsonElement> callback);

    @GET("/member/list")
    void listAllMembersByPaginationOrSearch(@Query("from") int from, @Query("size") int size, @Query("order") String order, @Query("col") String col, Callback<JsonElement> callback);

    @GET("/members/{id}")
    void getMemberInfo(@Path("id") int userId, Callback<JsonElement> callback);

    @GET("/members/search")
    void searchForMembersFromKey(@Query("string") String string, Callback<JsonElement> callback);

    @GET("/member/suggest")
    void suggestionMember(@Query("string") String search, Callback<JsonElement> callback);

    @GET("/members/{id}/around")
    void getMembersArroundMe(@Path("id") int userId, @Query("radius") float radius, Callback<JsonElement> callback);

    @FormUrlEncoded
    @POST("/members/{id}")
    void deleteMember(@Path("id") int userId, @Field("_method") String method, @Field("access_token") String token, Callback<JsonElement> callback);

    /*@POST("/members/password/remind")
    void passwordReminder(@Body TypedInput emails, Callback<JsonElement> callback);*/

    // this method is executed from the link sent by email to remind a member to reset his password
    @POST("/members/password/reset")
    void passwordReset(@Body TypedInput resetParams, Callback<JsonElement> callback);

    @POST("/members/confirmation/send")
    void sendEmailConfirmationOnRegister(@Body TypedInput userId, Callback<JsonElement> callback);

    @POST("/members/confirmation/validate")
    void validateEmailConfirmationOnRegister(@Body TypedInput token, Callback<JsonElement> callback);

    // Todo : To delete

    @POST("/members/{id}/ambassador")
    void updateStatusMember(@Path("id") int userId, @Body TypedInput ambassador, Callback<JsonElement> callback);

    @POST("/members/{id}/deviceToken")
    void updateTokenMember(@Path("id") int userId, @Body TypedInput device_token, Callback<JsonElement> callback);

    @POST("/members/{id}/notifies")
    void updateNotifyOptionsMember(@Path("id") int userId, @Body TypedInput notifOptions, Callback<JsonElement> callback);

    @POST("/members/{id}/geolocation")
    void updateLocationMember(@Path("id") int userId, @Body TypedInput locations, Callback<JsonElement> callback);
}
