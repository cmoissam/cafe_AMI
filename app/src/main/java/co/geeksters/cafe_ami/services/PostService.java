package co.geeksters.cafe_ami.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.failure.UnauthorizedFailureEvent;
import co.geeksters.cafe_ami.events.success.PostEvent;
import co.geeksters.cafe_ami.events.success.PostsEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.interfaces.PostInterface;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.models.Post;
import co.geeksters.cafe_ami.models.Todo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostService extends BaseService {

    public final PostInterface api;
    public String token;

    public PostService(String token) {
        this.api = BaseService.adapterWithToken(token).create(PostInterface.class);
        this.token = token;
    }

    public void listPostsForMe() {

        this.api.listPostsForMe(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Post> posts_for_member = Post.createListPostsFromJson(responseAsArray);
                BaseApplication.post(new PostsEvent(posts_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listPostsForMember(int memberId) {

        this.api.listPostsForMember(memberId, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Post> posts_for_member = Post.createListPostsFromJson(responseAsArray);
                BaseApplication.post(new PostsEvent(posts_for_member));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void listAllPosts() {
        this.api.listAllPosts(new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                JsonArray responseAsArray = response.getAsJsonObject().get("data").getAsJsonArray();
                List<Post> posts = Post.createListPostsFromJson(responseAsArray);
                BaseApplication.post(new PostsEvent(posts));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void createPost(String token, Post post,List<Member> concernedMembers) {

        this.api.createPost(token, post.title, post.content,post.interests, Todo.arrayToString(concernedMembers), new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Post created_post = Post.createPostFromJson(response);
                BaseApplication.post(new PostEvent(created_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure

                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }


    public void deletePost(int postId) {

        this.api.deletePost(postId, "delete", this.token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Post deleted_post = Post.createPostFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new PostEvent(deleted_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                if(error == null)
                    BaseApplication.post(new UnauthorizedFailureEvent());
                else
                if(error.getResponse() == null) {
                    BaseApplication.post(new UnauthorizedFailureEvent());
                }
                else
                if(error.getResponse() != null) {
                    if (error.getResponse().getStatus() == 401) {
                        BaseApplication.post(new UnauthorizedFailureEvent());
                    }
                }
                else
                    BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}
