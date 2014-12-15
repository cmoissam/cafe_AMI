package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class InterestsEvent {

    public List<Interest> interests;

    public InterestsEvent(List<Interest> interests) {
        this.interests = interests;
    }
}
