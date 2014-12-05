package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class CreateHubEvent {

    public Hub created_hub;

    public CreateHubEvent(Hub created_hub) {
        this.created_hub = created_hub;
    }
}
