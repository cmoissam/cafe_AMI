package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class HubEvent {

    public Hub hub;

    public HubEvent(Hub hub) {
        this.hub = hub;
    }
}
