package co.geeksters.hq.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CommentEvent;
import co.geeksters.hq.events.success.CommentsEvent;
import co.geeksters.hq.events.success.CommentsEventOnReplay;
import co.geeksters.hq.events.success.DeleteCommentEvent;
import co.geeksters.hq.events.success.PostEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.CommentInterface;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.Path;

public class CommentService {

    public final CommentInterface api;
    public String token;

    public CommentService(String token) {
        this.api = BaseService.adapterWithToken(token).create(CommentInterface.class);
        this.token = token;
    }

    public void listCommentsForPost(int post_id) {

        this.api.listCommentsForPost(post_id, new Callback<JsonArray>() {

            @Override
            public void success(JsonArray response, Response rawResponse) {
                List<Comment> comments_for_post = Comment.createListCommentsFromJson(response, null);
                List invertedList = new ArrayList();
                for (int i = comments_for_post.size() - 1; i >= 0; i--) {
                    invertedList.add(comments_for_post.get(i));
                }

                comments_for_post.clear();
                comments_for_post.addAll(invertedList);
                BaseApplication.post(new CommentsEvent(comments_for_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void commentPost(int post_id, final Comment comment, final Member currentMember) {

        this.api.commentPost(post_id, this.token, comment.text, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                List<Comment> created_comment_for_post = Comment.createListCommentsFromJson(response.getAsJsonObject().get("data").getAsJsonArray(), currentMember);
                List invertedList = new ArrayList();
                for (int i = created_comment_for_post.size() - 1; i >= 0; i--) {
                    invertedList.add(created_comment_for_post.get(i));
                }

                created_comment_for_post.clear();
                created_comment_for_post.addAll(invertedList);
                BaseApplication.post(new CommentsEventOnReplay(created_comment_for_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    //    TODO : return just one comment (the deleted one) -> CommentEvent
    public void deleteComment(int postId, int commentId) {

//        @Path("id") int postId, @Path("comment") int commentId, @Field("_method") String method, @Field("access_token") String token
        this.api.deleteComment(postId, commentId, "delete", token, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Comment deleted_comment = Comment.getCommentFromJson(response.getAsJsonObject().get("data"));
                BaseApplication.post(new CommentEvent(deleted_comment));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }
}