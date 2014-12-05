package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 27/11/14.
 */

public class ListHubsForMemberEvent {

    public List<Hub> hubs_for_member;

    public ListHubsForMemberEvent(List<Hub> hubs_for_member) {
        this.hubs_for_member = hubs_for_member;
    }
}
