package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class MemberEvent {
    public Member member;

    public MemberEvent(Member member){
        this.member = member;
    }
}
