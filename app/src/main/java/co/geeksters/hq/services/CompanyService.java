package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateCompanyEvent;
import co.geeksters.hq.events.success.DeleteCompanyEvent;
import co.geeksters.hq.events.success.GetCompanyInfoEvent;
import co.geeksters.hq.events.success.ListAllCompaniesEvent;
import co.geeksters.hq.events.success.SuggestionCompanyEvent;
import co.geeksters.hq.events.success.UpdateCompanyEvent;
import co.geeksters.hq.global.BaseApplication;
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

        this.api.listAllCompanies(new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Company> companies = Company.createListCompaniesFromJson(response);
                BaseApplication.getEventBus().post(new ListAllCompaniesEvent(companies));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void getCompanyInfo(int company_id) {

        this.api.getCompanyInfo(company_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Company company = Company.createCompanyFromJson(response);
                BaseApplication.getEventBus().post(new GetCompanyInfoEvent(company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void createCompany(Company company) {

        this.api.createCompany(company, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Company created_company = Company.createCompanyFromJson(response);
                BaseApplication.getEventBus().post(new CreateCompanyEvent(created_company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void updateCompany(int company_id, String name) {

        this.api.updateCompany(company_id, name, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Company updated_company = Company.createCompanyFromJson(response);
                BaseApplication.getEventBus().post(new UpdateCompanyEvent(updated_company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteCompany(int company_id) {

        this.api.deleteCompany(company_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Company deleted_company = Company.createCompanyFromJson(response);
                BaseApplication.getEventBus().post(new DeleteCompanyEvent(deleted_company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }

    public void suggestionsCompany(String search) {

        this.api.suggestionsCompany(search, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Company> suggestion_company = Company.createListCompaniesFromJson(response);
                BaseApplication.getEventBus().post(new SuggestionCompanyEvent(suggestion_company));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.getEventBus().post(new ConnectionFailureEvent());
            }
        });
    }
}
