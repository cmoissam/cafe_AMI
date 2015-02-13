package co.geeksters.hq.events.success;

import com.google.android.gms.internal.co;

import co.geeksters.hq.models.Comment;

/**
 * Created by soukaina on 28/11/14.
 */
public class CommentEvent {

    public Comment comment;

    public CommentEvent(Comment comment) {
        this.comment = comment;
    }
}
