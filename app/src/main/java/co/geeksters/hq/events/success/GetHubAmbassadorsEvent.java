package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 27/11/14.
 */
public class GetHubAmbassadorsEvent {

    public List<Member> ambassadors_of_hub;

    public GetHubAmbassadorsEvent(List<Member> ambassadors_of_hub) {
        this.ambassadors_of_hub = ambassadors_of_hub;
    }
}
