package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class CreateInterestEvent {

    public Interest interest;

    public CreateInterestEvent(Interest interest) {
        this.interest = interest;
    }
}
