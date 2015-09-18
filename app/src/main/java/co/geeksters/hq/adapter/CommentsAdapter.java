package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.CommentService;
import co.geeksters.hq.services.PostService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by soukaina on 04/02/15.
 */
public class CommentsAdapter {

    Context context;
    List<Comment> commentList;
    LinearLayout childView;
    String accessToken;
    SharedPreferences preferences;
    Member currentUser;

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

//            order by date created at .....
//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//
//                Date date = formatter.parse(commentList.get(i).createdAt);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            View childView = inflater.inflate(R.layout.list_item_comment, null); //same layout you gave to the adapter

            TextView fullNameTextView = (TextView)childView.findViewById(R.id.fullName);
            fullNameTextView.setText(commentList.get(i).member.fullName);

            TextView commentTextView = (TextView)childView.findViewById(R.id.comment);
            commentTextView.setText(commentList.get(i).text);

            Typeface typeFace=Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
            fullNameTextView.setTypeface(typeFace);
            commentTextView.setTypeface(typeFace);

/*            TextView date = (TextView)childView.findViewById(R.id.date);
            date.setText(commentList.get(i).createdAt);*/

            ImageView picture = (ImageView)childView.findViewById(R.id.picture);

            ViewHelpers.setImageViewBackgroundFromURL(context, picture, commentList.get(i).member.image);

            ImageView deleteComment = (ImageView)childView.findViewById(R.id.deleteComment);
            preferences = context.getSharedPreferences("CurrentUser", context.MODE_PRIVATE);
            currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
            if(commentList.get(i).member.id == currentUser.id) {
                deleteComment.setVisibility(View.VISIBLE);
            }
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