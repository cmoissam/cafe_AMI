package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewMarketAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.events.success.CommentEvent;
import co.geeksters.hq.events.success.CommentsEvent;
import co.geeksters.hq.events.success.PostEvent;
import co.geeksters.hq.events.success.PostsEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.PostService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_one_profile_market_place)
public class AllMarketPlaceFragment extends Fragment {
    // ArrayList for Listview
    String accessToken;
    List<Post> postsList = new ArrayList<Post>();
    ListViewMarketAdapter adapter;
    LayoutInflater inflater;
    Member currentUser;

    @ViewById(R.id.marketProfileProgress)
    ProgressBar membersProgress;

    @ViewById(R.id.progressBar)
    ProgressBar spinner;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    @ViewById(R.id.postsMarket)
    LinearLayout postsMarket;

    @ViewById(R.id.myPostsSearchForm)
    LinearLayout myPostsSearchForm;

    public void listAllPostService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            spinner.setVisibility(View.VISIBLE);
            PostService postService = new PostService(accessToken);
            postService.listAllPosts();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listPostForCurrentMember() {

        myPostsSearchForm.setBackgroundColor(Color.parseColor("#eeeeee"));

        listAllPostService();
    }

    @Subscribe
    public void onGetListPostsEvent(PostsEvent event) {
        spinner.setVisibility(View.GONE);
        postsList = event.posts;
//        ArrayList<HashMap<String, String>> posts = Post.postsInfoForItem(postsList);
        GlobalVariables.replyFromMyMarket = false;
        GlobalVariables.replyToAll = true;

        PostsAdapter adapter = new PostsAdapter(inflater, this, postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        if(GlobalVariables.notifiyedByPost) {
            Post notifiedPost = new Post();
            for (int i = 0; i < event.posts.size(); i++) {
                if (event.posts.get(i).id == GlobalVariables.notificationPostId) {
                    notifiedPost = event.posts.get(i);
                    break;
                }
            }
            GlobalVariables.onReply = true;
            GlobalVariables.notifiyedByPost = false;
            GlobalVariables.notificationPostId = -1;

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ReplyMarketFragment_().newInstance(notifiedPost.id, notifiedPost.comments);
            fragmentTransaction.replace(R.id.contentFrame, fragment);
            fragmentTransaction.commit();
        }
    }

    @Subscribe
    public void onGetDeletedPostEvent(PostEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.post.id) {
                postsList.remove(i);
                break;
            }
        }

        PostsAdapter adapter = new PostsAdapter(inflater, this, postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
    }


    @Subscribe
    public void onGetDeletedCommentEvent(CommentEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.comment.postId) {
                for ( int j = 0 ; j<postsList.get(i).comments.size(); j++){
                    if (postsList.get(i).comments.get(j).id == event.comment.id)
                    {
                        postsList.get(i).comments.remove(j);
                        break;
                    }
                }

            }
        }
        PostsAdapter adapter = new PostsAdapter(inflater, this, postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        PostsAdapter.lastClickedPosts = new ArrayList<Integer>();

        this.inflater = inflater;

        return null;
    }
}