package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class UpdateMemberLocationEvent {
    public Member member;

    public UpdateMemberLocationEvent(Member member){
        this.member = member;
    }
}
