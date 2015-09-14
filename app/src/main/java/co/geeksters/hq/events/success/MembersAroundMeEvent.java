package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class MembersAroundMeEvent {
    public List<Member> members;

    public MembersAroundMeEvent(List<Member> members){
        this.members = members;
    }
}
