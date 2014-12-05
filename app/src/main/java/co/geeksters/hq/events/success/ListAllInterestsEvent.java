package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListAllInterestsEvent {

    List<Interest> interests;

    public ListAllInterestsEvent(List<Interest> interests) {
        this.interests = interests;
    }
}
