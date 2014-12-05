package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class UpdateHubEvent {

    public Hub updated_hub;

    public UpdateHubEvent(Hub updated_hub) {
        this.updated_hub = updated_hub;
    }
}
