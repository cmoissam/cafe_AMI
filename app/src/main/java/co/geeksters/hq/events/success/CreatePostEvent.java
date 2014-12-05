package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class CreatePostEvent {

    Post created_post;

    public CreatePostEvent(Post created_post) {
        this.created_post = created_post;
    }
}
