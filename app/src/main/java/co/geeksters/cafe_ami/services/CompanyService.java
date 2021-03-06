package co.geeksters.cafe_ami.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.success.CompaniesEvent;
import co.geeksters.cafe_ami.events.success.CompanyEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.helpers.ParseHelpers;
import co.geeksters.cafe_ami.interfaces.CompanyInterface;
import co.geeksters.cafe_ami.models.Company;
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

        this.api.createCompany(ParseHelpers.createTypedInputFromModel(company), new Callback<JsonElement>() {

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

        this.api.updateCompany(companyId, ParseHelpers.createTypedInputFromModelByMethod(company, "put"), new Callback<JsonElement>() {

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

        this.api.deleteCompany(companyId, ParseHelpers.createTypedInputFromOneKeyValue("_method", "delete"), new Callback<JsonElement>() {

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
