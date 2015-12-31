package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class CommentsEvent {

    public List<Comment> comments;

    public CommentsEvent(List<Comment> comments) {
        this.comments = comments;
    }
}
