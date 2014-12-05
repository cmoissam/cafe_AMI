package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class CommentPostEvent {

    Comment created_comment_for_post;

    public CommentPostEvent(Comment created_comment_for_post) {
        this.created_comment_for_post = created_comment_for_post;
    }
}
