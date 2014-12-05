package co.geeksters.hq.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Company {

    /**
     * Attributes
     **/

	public int id;
    public String name;
	// A Member is a part of a list of companies and each company is represented
	// by a list of members
    public ArrayList<Member> members = new ArrayList<Member>();

    /**
     * Methods
     **/

    public static Company createCompanyFromJson(JsonElement response) {
        Gson gson = new Gson();
        Company company = gson.fromJson (response, Company.class);

        return company;
    }

    public static List<Company> createListCompaniesFromJson(JSONArray response) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Member>>(){}.getType();
        List<Company> companies = gson.fromJson(response.toString(), listType);

        return companies;
    }
}
