package co.geeksters.hq.events.success;

import co.geeksters.hq.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class DeleteCommentEvent {

    public Comment deleted_comment;

    public DeleteCommentEvent(Comment deleted_comment) {
        this.deleted_comment = deleted_comment;
    }
}
