package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreatePostEvent;
import co.geeksters.hq.events.success.DeletePostEvent;
import co.geeksters.hq.events.success.ListAllPostsEvent;
import co.geeksters.hq.events.success.ListPostsForMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.PostInterface;
import co.geeksters.hq.models.Post;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostService extends BaseService {

    public final PostInterface api;

    public PostService(String token) {
        this.api = BaseService.adapterWithToken(token).create(PostInterface.class);
    }

    public void listPostsForMember(int user_id, String size) {

        this.api.listPostsForMember(user_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Post> posts_for_member = Post.createListPostsFromJson(response);
                BaseApplication.post(new ListPostsForMemberEvent(posts_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllPosts(String order, String from, String size) {

        this.api.listAllPosts(order, from, size, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Post> posts = Post.createListPostsFromJson(response);
                BaseApplication.post(new ListAllPostsEvent(posts));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createPost(int user_id, Post post) {

        this.api.createPost(user_id, post, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Post created_post = Post.createPostFromJson(response);
                BaseApplication.post(new CreatePostEvent(created_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deletePost(int post_id) {

        this.api.deletePost(post_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Post deleted_post = Post.createPostFromJson(response);
                BaseApplication.post(new DeletePostEvent(deleted_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
