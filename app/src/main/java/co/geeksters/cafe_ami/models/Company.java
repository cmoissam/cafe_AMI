package co.geeksters.cafe_ami.models;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Company implements Serializable{

    /**
     * Attributes
     **/

	public int id;
    public String name = "";
	// A Member is a part of a list of companies and each company is represented
	// by a list of members
    public ArrayList<Member> members = new ArrayList<Member>();

    /**
     * Methods
     **/

    public static Company createCompanyFromJson(JsonElement response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        if(response.getAsJsonObject().get("members") != null) {
            response.getAsJsonObject().add("members", response.getAsJsonObject().get("members").getAsJsonArray());
        }

        Company company = gson.fromJson (response, Company.class);

        for (int i = 0; i< company.members.size(); i++){
            company.members.get(i).setSocialIdAndHubId(response.getAsJsonObject().get("members").getAsJsonArray().get(i));
        }

        return company;
    }

    public static List<Company> createListCompaniesFromJson(JsonArray response) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        /*Type listType = new TypeToken<List<Company>>(){}.getType();
        List<Company> companies = gson.fromJson(response.toString(), listType);*/

        List<Company> companies = new ArrayList<Company>();

        for (int i = 0; i< response.size(); i++) {
            Company company = createCompanyFromJson(response.get(i));
            companies.add(company);
        }

        return companies;
    }
}
