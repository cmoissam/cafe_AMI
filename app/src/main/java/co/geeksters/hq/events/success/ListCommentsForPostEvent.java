package co.geeksters.hq.events.success;

import java.util.List;

import co.geeksters.hq.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class ListCommentsForPostEvent {

    public List<Comment> comments_for_post;

    public ListCommentsForPostEvent(List<Comment> comments_for_post) {
        this.comments_for_post = comments_for_post;
    }
}
