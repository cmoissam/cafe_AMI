package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.ReplyMarketFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.PostService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by soukaina on 04/02/15.
 */
public class PostsAdapter {

    Fragment context;
    private List<Post> postList;
    String accessToken;
    LinearLayout llList;
    LayoutInflater inflater;
    Member currentUser;
    public static List<Integer> lastClickedPosts = new ArrayList<Integer>();

    public PostsAdapter(LayoutInflater inflater, Fragment fragment, LinearLayout llList, List<Post> postList, String accessToken, Member currentUser) {
        this.context = fragment;
        this.postList = postList;
        this.accessToken = accessToken;
        this.llList = llList;
        this.inflater = inflater;
        this.currentUser = currentUser;
    }

    public void makeList() {
        llList.removeAllViews();

        for(int i = 0 ; i < postList.size(); i++) {
            final int index = i;

            final LinearLayout childView = (LinearLayout)inflater.inflate(R.layout.list_item_post, null, false);
            TextView postTextView = (TextView)childView.findViewById(R.id.post);
            postTextView.setText(postList.get(i).content);
            final LinearLayout commentDisplay = (LinearLayout) childView.findViewById(R.id.commentDisplay);
            Button reply = (Button) childView.findViewById(R.id.reply);
            final LinearLayout commentsLayout = (LinearLayout) childView.findViewById(R.id.commentsLayout);
            ImageView picture = (ImageView) childView.findViewById(R.id.picture);


            ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture, postList.get(i).member.image);

            TextView fullName = (TextView) childView.findViewById(R.id.fullName);
            fullName.setText(postList.get(i).member.fullName);
            TextView datePost = (TextView) childView.findViewById(R.id.datePost);
            datePost.setText(postList.get(i).createdAt);

//            if(lastClickedPosts.contains(index)) {
//                GlobalVariables.onClickComment = true;
//
//                for(int j=0; j<lastClickedPosts.size(); j++) {
//                    if(lastClickedPosts.get(j) == index) {
//                        lastClickedPosts.remove(j);
//                        break;
//                    }
//                }
//            } else {
//                GlobalVariables.onClickComment = false;
//            }
//
//            if(GlobalVariables.onClickComment) {
//                commentDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
//                commentsLayout.setVisibility(View.GONE);
//            } else {
//                commentDisplay.setBackgroundColor(Color.parseColor("#eeeeee"));
//                commentsLayout.setVisibility(View.VISIBLE);
//                lastClickedPosts.add(index);
//                CommentsAdapter adapter = new CommentsAdapter(context.getActivity(), postList.get(index).comments, childView, accessToken);
//                adapter.makeList();
//            }

            if(postList.get(i).comments.size() != 0) {
                TextView commentSizeTextView = (TextView)childView.findViewById(R.id.commentsSize);
                commentSizeTextView.setText(postList.get(i).comments.size() + " comments");

                commentDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lastClickedPosts.contains(index)) {
                            GlobalVariables.onClickComment = true;

                            for(int i=0; i<lastClickedPosts.size(); i++) {
                                if(lastClickedPosts.get(i) == index) {
                                    lastClickedPosts.remove(i);
                                    break;
                                }
                            }
                        } else {
                            GlobalVariables.onClickComment = false;
                        }

                        if(GlobalVariables.onClickComment) {
                            commentDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                            commentsLayout.setVisibility(View.GONE);
                        } else {
                            commentDisplay.setBackgroundColor(Color.parseColor("#eeeeee"));
                            commentsLayout.setVisibility(View.VISIBLE);
                            lastClickedPosts.add(index);
                            CommentsAdapter adapter = new CommentsAdapter(context.getActivity(), postList.get(index).comments, childView, accessToken);
                            adapter.makeList();
                        }
                    }
                });
            } else
                commentDisplay.setVisibility(View.GONE);

            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.onReply = true;
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new ReplyMarketFragment_().newInstance(postList.get(index).id, postList.get(index).comments);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            ImageView deletePost = (ImageView)childView.findViewById(R.id.deletePost);

            if(postList.get(i).member.id == currentUser.id) {
                deletePost.setVisibility(View.VISIBLE);
            }
            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostService postService = new PostService(accessToken);
                    postService.deletePost(postList.get(index).id);
                }
            });

            llList.addView(childView);
        }
    }


}