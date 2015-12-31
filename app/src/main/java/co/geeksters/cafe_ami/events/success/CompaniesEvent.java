package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Company;

/**
 * Created by soukaina on 28/11/14.
 */
public class CompaniesEvent {

    public List<Company> companies;

    public CompaniesEvent(List<Company> companies) {
        this.companies = companies;
    }
}
