package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class DeletePostEvent {

    Post deleted_post;

    public DeletePostEvent(Post deleted_post) {
        this.deleted_post = deleted_post;
    }
}
