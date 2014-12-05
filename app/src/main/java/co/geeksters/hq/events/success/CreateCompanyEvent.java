package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Company;

/**
 * Created by soukaina on 28/11/14.
 */
public class CreateCompanyEvent {

    public Company company;

    public CreateCompanyEvent(Company company) {
        this.company = company;
    }
}
