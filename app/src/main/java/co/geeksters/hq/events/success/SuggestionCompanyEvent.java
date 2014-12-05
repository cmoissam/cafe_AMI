package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Company;

/**
 * Created by soukaina on 28/11/14.
 */
public class SuggestionCompanyEvent {

    public List<Company> suggestion_company;

    public SuggestionCompanyEvent(List<Company> suggestion_company) {
        this.suggestion_company = suggestion_company;
    }
}
