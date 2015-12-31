package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class PostEvent {

    public Post post;

    public PostEvent(Post post) {
        this.post = post;
    }
}
