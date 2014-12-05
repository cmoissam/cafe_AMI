package co.geeksters.hq.interfaces;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import co.geeksters.hq.models.Company;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface CompanyInterface {

    @GET("/companies/{id}")
    void getCompanyInfo(@Path("id") int company_id, Callback<JsonElement> callback);

    @GET("/companies")
    void listAllCompanies(Callback<JSONArray> callback);

    @POST("/companies")
    void createCompany(@Body Company company, Callback<JsonElement> callback);

    @POST("/companies/{id}")
    void updateCompany(@Path("id") int company_id, String name, Callback<JsonElement> callback);

    @POST("/companies/{id}")
    void deleteCompany(@Path("id") int company_id, Callback<JsonElement> callback);

    @GET("/companies/suggest")
    void suggestionsCompany(String search, Callback<JSONArray> callback);

}
