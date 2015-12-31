package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Member;

/**
 * Created by soukaina on 27/11/14.
 */
public class LoginEvent {

    public String accessToken;
    public Member member;

    public LoginEvent(String accessToken, Member member) {
        this.accessToken = accessToken;
        this.member = member;
    }
}
