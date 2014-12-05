package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Company;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListAllCompaniesEvent {

    public List<Company> companies;

    public ListAllCompaniesEvent(List<Company> companies) {
        this.companies = companies;
    }
}
