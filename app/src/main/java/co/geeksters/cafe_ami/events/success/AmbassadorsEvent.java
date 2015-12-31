package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Member;

/**
 * Created by soukaina on 03/12/14.
 */
public class AmbassadorsEvent {
    public List<Member> members;

    public AmbassadorsEvent(List<Member> members){
        this.members = members;
    }
}
