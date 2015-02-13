package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
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
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.ReplyMarketFragment;
import co.geeksters.hq.fragments.ReplyMarketFragment_;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.PostService;

/**
 * Created by soukaina on 04/02/15.
 */
public class PostsAdapter {

    Fragment context;
    private List<Post> postList;
    static boolean onClickComment = false;
    String accessToken;

    public PostsAdapter(Fragment fragment, List<Post> postList, String accessToken) {
        context = fragment;
        this.postList = postList;
        this.accessToken = accessToken;
    }

    public void makeList() {
        final LayoutInflater inflater = LayoutInflater.from(context.getActivity());

        final LinearLayout llList = (LinearLayout) context.getActivity().findViewById(R.id.postsMarket);

        llList.removeAllViews();

        for(int i = 0 ; i < postList.size() ; i++) {
            final LinearLayout childView = (LinearLayout)inflater.inflate(R.layout.list_item_post, null, false);
            TextView postTextView = (TextView)childView.findViewById(R.id.post);
            postTextView.setText(postList.get(i).content);
            final LinearLayout commentDisplay = (LinearLayout) childView.findViewById(R.id.commentDisplay);
            Button reply = (Button) childView.findViewById(R.id.reply);
            final LinearLayout commentsLayout = (LinearLayout) childView.findViewById(R.id.commentsLayout);

            if(postList.get(i).comments.size() != 0) {
                TextView commentSizeTextView = (TextView)childView.findViewById(R.id.commentsSize);
                commentSizeTextView.setText(postList.get(i).comments.size() + " comments");

                final int index = i;
                commentDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onClickComment) {
                            commentDisplay.setBackgroundColor(Color.parseColor("#ffffff"));
                            commentsLayout.setVisibility(View.GONE);
                            onClickComment = false;
                        } else {
                            onClickComment = true;
                            commentDisplay.setBackgroundColor(Color.parseColor("#eeeeee"));
                            commentsLayout.setVisibility(View.VISIBLE);
                            CommentsAdapter adapter = new CommentsAdapter(context.getActivity(), postList.get(index).comments, childView);
                            adapter.makeList();
                        }
                    }
                });
            } else
                commentDisplay.setVisibility(View.GONE);

            final int index = i;
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new ReplyMarketFragment_().newInstance(postList.get(index).id, postList.get(index).comments);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            ImageView deletePost = (ImageView)childView.findViewById(R.id.deletePost);
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