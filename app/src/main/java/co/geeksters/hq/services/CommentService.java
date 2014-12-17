package co.geeksters.hq.services;

import com.google.gson.JsonElement;

import org.json.JSONArray;

import java.util.List;

import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CommentPostEvent;
import co.geeksters.hq.events.success.DeleteCommentEvent;
import co.geeksters.hq.events.success.ListCommentsForPostEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.interfaces.CommentInterface;
import co.geeksters.hq.models.Comment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentService {

    public final CommentInterface api;

    public CommentService(String token) {
        this.api = BaseService.adapterWithToken(token).create(CommentInterface.class);
    }

    public void listCommentsForPost(int post_id) {

        this.api.listCommentsForPost(post_id, new Callback<JSONArray>() {

            @Override
            public void success(JSONArray response, Response rawResponse) {
                List<Comment> comments_for_post = Comment.createListCommentsFromJson(response);
                BaseApplication.post(new ListCommentsForPostEvent(comments_for_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void commentPost(int post_id, Comment comment) {

        this.api.commentPost(post_id, comment, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Comment created_comment_for_post = Comment.createCommentFromJson(response);
                BaseApplication.post(new CommentPostEvent(created_comment_for_post));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

    public void deleteComment(int comment_id) {

        this.api.deleteComment(comment_id, new Callback<JsonElement>() {

            @Override
            public void success(JsonElement response, Response rawResponse) {
                Comment deleted_comment = Comment.createCommentFromJson(response);
                BaseApplication.post(new DeleteCommentEvent(deleted_comment));
            }

            @Override
            public void failure(RetrofitError error) {
                // popup to inform the current user of the failure
                BaseApplication.post(new ConnectionFailureEvent());
            }
        });
    }

}
