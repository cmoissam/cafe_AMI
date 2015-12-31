package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class UpdateMemberLocationEvent {
    public Member member;

    public UpdateMemberLocationEvent(Member member){
        this.member = member;
    }
}
