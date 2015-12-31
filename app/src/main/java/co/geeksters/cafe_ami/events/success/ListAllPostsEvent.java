package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class ListAllPostsEvent {

    List<Post> posts;

    public ListAllPostsEvent(List<Post> posts) {
        this.posts = posts;
    }
}
