package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Company;

/**
 * Created by soukaina on 28/11/14.
 */
public class UpdateCompanyEvent {

    public Company updated_company;

    public UpdateCompanyEvent(Company updated_company) {
        this.updated_company = updated_company;
    }
}
