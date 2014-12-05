package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class DeleteHubEvent {

    public Hub deleted_hub;

    public DeleteHubEvent(Hub deleted_hub) {
        this.deleted_hub = deleted_hub;
    }
}
