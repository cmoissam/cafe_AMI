package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.adapter.ListViewMarketAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.MembersSearchEvent;
import co.geeksters.hq.events.success.PostsEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.MemberService;
import co.geeksters.hq.services.PostService;

@EFragment(R.layout.fragment_market_place)
public class OneProfileMarketPlaceFragment extends Fragment {
    // ArrayList for Listview
    String accessToken;
    List<Post> postsList = new ArrayList<Post>();
    ListViewMarketAdapter adapter;

    static int from = 0;

    // List view
//    @ViewById(R.id.list_markets_profile)
//    ListView listMarketsProfile;

    @ViewById(R.id.marketProfileProgress)
    ProgressBar membersProgress;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    public void listPostForCurrentMemberService() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            PostService postService = new PostService(accessToken);
            postService.listPostsForMember();
//            postService.listAllPosts();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listPostForCurrentMember() {
        listPostForCurrentMemberService();
    }

    @Subscribe
    public void onGetListPostsEvent(PostsEvent event) {
        postsList = event.posts;

        ArrayList<HashMap<String, String>> posts = Post.postsInfoForItem(postsList);

        PostsAdapter adapter = new PostsAdapter(getActivity(), posts);
        adapter.makeList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        return null;
    }
}