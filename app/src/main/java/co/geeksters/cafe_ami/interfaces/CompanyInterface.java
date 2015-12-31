package co.geeksters.cafe_ami.interfaces;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface CompanyInterface {

    @GET("/companies/{id}")
    void getCompanyInfo(@Path("id") int company_id, Callback<JsonElement> callback);

    @GET("/companies")
    void listAllCompanies(Callback<JsonElement> callback);

    @POST("/companies")
    void createCompany(@Body TypedInput company, Callback<JsonElement> callback);

    @POST("/companies/{id}")
    void updateCompany(@Path("id") int company_id, @Body TypedInput company, Callback<JsonElement> callback);

    @POST("/companies/{id}")
    void deleteCompany(@Path("id") int company_id, @Body TypedInput company, Callback<JsonElement> callback);

    @GET("/company/suggest")
    void suggestCompanies(@Query("string") String search, Callback<JsonElement> callback);

}
