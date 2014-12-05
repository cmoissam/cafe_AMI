package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListPostsForMemberEvent {

    List<Post> posts_for_member;

    public ListPostsForMemberEvent(List<Post> posts_for_member) {
        this.posts_for_member = posts_for_member;
    }
}
