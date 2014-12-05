package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListAllHubsEvent {

    public List<Hub> hubs;

    public ListAllHubsEvent(List<Hub> hubs) {
        this.hubs = hubs;
    }
}
