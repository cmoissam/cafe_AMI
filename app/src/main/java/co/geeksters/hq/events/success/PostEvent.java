package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class PostEvent {

    public Post post;

    public PostEvent(Post post) {
        this.post = post;
    }
}
