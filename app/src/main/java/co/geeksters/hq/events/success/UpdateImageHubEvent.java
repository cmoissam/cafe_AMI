package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class UpdateImageHubEvent {

    public Hub updated_hub;

    public UpdateImageHubEvent(Hub updated_hub) {
        this.updated_hub = updated_hub;
    }
}
