package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class GetInterestInfoEvent {

    public Interest interest;

    public GetInterestInfoEvent(Interest interest) {
        this.interest = interest;
    }
}
