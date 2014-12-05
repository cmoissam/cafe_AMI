package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class MembersEvent {
    public List<Member> members;

    public MembersEvent(List<Member> members){
        this.members = members;
    }
}
