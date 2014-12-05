package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Interest;

/**
 * Created by soukaina on 27/11/14.
 */
public class DeleteInterestEvent {

    public Interest deleted_interest;

    public DeleteInterestEvent(Interest deleted_interest) {
        this.deleted_interest = deleted_interest;
    }
}
