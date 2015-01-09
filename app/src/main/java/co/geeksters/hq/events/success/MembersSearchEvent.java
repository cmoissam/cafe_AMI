package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class MembersSearchEvent {
    public List<Member> members;

    public MembersSearchEvent(List<Member> members){
        this.members = members;
    }
}
