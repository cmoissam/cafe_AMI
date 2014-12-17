package co.geeksters.hq.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CompaniesEvent;
import co.geeksters.hq.events.success.CompanyEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.interfaces.CompanyInterface;
import co.geeksters.hq.models.Company;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CompanyService {

    public final CompanyInterface api;

    public CompanyService(String token) {
        this.api = BaseService.adapterWithToken(token).create(CompanyInterface.class);
    }

    public void listAllCompanies() {

        this.api.listAllCompanies(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Company> companies = Company.createListCompaniesFromJson(responseAsArray);
                BaseApplication.post(new CompaniesEvent(companies));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void getCompanyInfo(int companyId) {

        this.api.getCompanyInfo(companyId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonElement responseAsJson = response.getAsJsonObject().get("data");
                Company company = Company.createCompanyFromJson(responseAsJson);
                BaseApplication.post(new CompanyEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createCompany(Company company) {

        this.api.createCompany(ParseHelper.createTypedInputFromModel(company), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonElement responseAsJson = response.getAsJsonObject().get("data");
                Company company = Company.createCompanyFromJson(responseAsJson);
                BaseApplication.post(new CompanyEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateCompany(int companyId, Company company) {

        this.api.updateCompany(companyId, ParseHelper.createTypedInputFromModelByMethod(company, "put"), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonElement responseAsJson = response.getAsJsonObject().get("data");
                Company company = Company.createCompanyFromJson(responseAsJson);
                BaseApplication.post(new CompanyEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteCompany(int companyId) {

        this.api.deleteCompany(companyId, ParseHelper.createTypedInputFromOneKeyValue("_method", "delete"), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonElement responseAsJson = response.getAsJsonObject().get("data");
                Company company = Company.createCompanyFromJson(responseAsJson);
                BaseApplication.post(new CompanyEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestCompanies(String search) {

        this.api.suggestCompanies(search, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Company> companies = Company.createListCompaniesFromJson(responseAsArray);
                BaseApplication.post(new CompaniesEvent(companies));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
