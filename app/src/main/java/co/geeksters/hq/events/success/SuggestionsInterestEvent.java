package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class SuggestionsInterestEvent {

    public List<Interest> interests;

    public SuggestionsInterestEvent(List<Interest> interests) {
        this.interests = interests;
    }
}
