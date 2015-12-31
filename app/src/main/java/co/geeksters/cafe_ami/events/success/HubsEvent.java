package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */
public class HubsEvent {

    public List<Hub> hubs;

    public HubsEvent(List<Hub> hubs) {
        this.hubs = hubs;
    }
}
