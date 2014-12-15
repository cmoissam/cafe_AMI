package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Company;

/**
 * Created by soukaina on 28/11/14.
 */
public class CompanyEvent {

    public Company company;

    public CompanyEvent(Company company) {
        this.company = company;
    }
}
