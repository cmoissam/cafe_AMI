package co.geeksters.cafe_ami.events.success;

import java.util.List;

import co.geeksters.cafe_ami.models.Post;

/**
 * Created by soukaina on 27/11/14.
 */
public class PostsEvent {

    public List<Post> posts;

    public PostsEvent(List<Post> posts) {
        this.posts = posts;
    }
}
