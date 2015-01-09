package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class SaveMemberEvent {
    public Member member;

    public SaveMemberEvent(Member member){
        this.member = member;
    }
}
