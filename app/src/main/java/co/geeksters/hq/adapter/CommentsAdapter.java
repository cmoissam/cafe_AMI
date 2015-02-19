package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.services.CommentService;
import co.geeksters.hq.services.PostService;

/**
 * Created by soukaina on 04/02/15.
 */
public class CommentsAdapter {

    Context context;
    List<Comment> commentList;
    LinearLayout childView;
    String accessToken;

    public CommentsAdapter(Context context, List<Comment> commentList, LinearLayout childView, String accessToken) {
        this.context = context;
        this.commentList = commentList;
        this.childView = childView;
        this.accessToken = accessToken;
    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout llList = (LinearLayout) childView.findViewById(R.id.commentsLayout);

        llList.removeAllViews();

        for(int i = 0 ; i < commentList.size() ; i++) {
            View childView = inflater.inflate(R.layout.list_item_comment, null); //same layout you gave to the adapter

            TextView fullNameTextView = (TextView)childView.findViewById(R.id.fullName);
            fullNameTextView.setText(commentList.get(i).member.fullName);

            TextView commentTextView = (TextView)childView.findViewById(R.id.comment);
            commentTextView.setText(commentList.get(i).text);

            TextView date = (TextView)childView.findViewById(R.id.date);
            date.setText(commentList.get(i).createdAt);

            ImageView picture = (ImageView)childView.findViewById(R.id.picture);
            if(commentList.get(i).member.image != null && commentList.get(i).member.image.startsWith("http://"))
                ViewHelpers.setImageViewBackgroundFromURL(context, picture, commentList.get(i).member.image);

            ImageView deleteComment = (ImageView)childView.findViewById(R.id.deleteComment);
            final int index = i;
            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.onDeleteComment = true;
                    CommentService commentService = new CommentService(accessToken);
                    commentService.deleteComment(commentList.get(index).postId, commentList.get(index).id);
                    GlobalVariables.commentClickedIndex = index;
                }
            });

            llList.addView(childView,0);
        }
    }
}