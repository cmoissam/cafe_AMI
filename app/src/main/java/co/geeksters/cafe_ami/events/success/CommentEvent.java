package co.geeksters.cafe_ami.events.success;

import co.geeksters.cafe_ami.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class CommentEvent {

    public Comment comment;

    public CommentEvent(Comment comment) {
        this.comment = comment;
    }
}
