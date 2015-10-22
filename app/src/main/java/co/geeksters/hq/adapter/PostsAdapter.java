package co.geeksters.hq.adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.ReplyMarketFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.PostService;

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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            try {
                date = format.parse(postList.get(i).createdAt);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat formatToShow = new SimpleDateFormat("EEEE d MMMM HH:mm");
            formatToShow.setTimeZone(tz);
            datePost.setText(formatToShow.format(date));

            Typeface typeFace=Typeface.createFromAsset(context.getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
            fullName.setTypeface(typeFace);
            datePost.setTypeface(typeFace);
            postTextView.setTypeface(typeFace);

            if(postList.get(i).comments.size() != 0) {
                TextView commentSizeTextView = (TextView)childView.findViewById(R.id.commentsSize);
                commentSizeTextView.setText(postList.get(i).comments.size() + " comments");

                commentDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            if (lastClickedPosts.contains(index)) {
                                GlobalVariables.onClickComment = true;

                                for (int i = 0; i < lastClickedPosts.size(); i++) {
                                    if (lastClickedPosts.get(i) == index) {
                                        lastClickedPosts.remove(i);
                                        break;
                                    }
                                }
                            } else {
                                GlobalVariables.onClickComment = false;
                            }

                            if (GlobalVariables.onClickComment) {
                                commentsLayout.setVisibility(View.GONE);
                            } else {
                                commentsLayout.setVisibility(View.VISIBLE);
                                lastClickedPosts.add(index);
                                CommentsAdapter adapter = new CommentsAdapter(context.getActivity(), postList.get(index).comments, childView, accessToken);
                                adapter.makeList();
                            }

                            GlobalVariables.commentClicked = false;
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
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
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