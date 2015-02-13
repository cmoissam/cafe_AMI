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
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;

/**
 * Created by soukaina on 04/02/15.
 */
public class CommentsAdapter {

    Context context;
    List<Comment> commentList;
    LinearLayout childView;

    public CommentsAdapter(Context c, List<Comment> commentList, LinearLayout childView) {
        context = c;
        this.commentList = commentList;
        this.childView = childView;
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

            llList.addView(childView,0);
        }
    }
}