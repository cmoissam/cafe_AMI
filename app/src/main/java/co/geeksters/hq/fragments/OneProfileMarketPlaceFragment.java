package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewMarketAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.adapter.PostsAdapterOneProfile;
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
public class OneProfileMarketPlaceFragment extends Fragment {
    // ArrayList for Listview
    String accessToken;
    List<Post> postsList = new ArrayList<Post>();
    ListViewMarketAdapter adapter;
    LayoutInflater inflater;
    Member currentUser;
    static int from = 0;


    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loading;


    @ViewById(R.id.postsMarket)
    LinearLayout postsMarket;

    @ViewById(R.id.myPostsSearchForm)
    LinearLayout myPostsSearchForm;

    public void listPostForCurrentMemberService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(GlobalVariables.isCurrentMember == true) {
              // spinner.setVisibility(View.VISIBLE);
                PostService postService = new PostService(accessToken);
                postService.listPostsForMe();
                loading.setVisibility(View.VISIBLE);
            }
            else{

               //spinner.setVisibility(View.VISIBLE);
                SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
                Member member = Member.createUserFromJson(createJsonElementFromString(preferences.getString("profile_member", "")));
                PostService postService = new PostService(accessToken);
                postService.listPostsForMember(member.id);
                loading.setVisibility(View.VISIBLE);
            }
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listPostForCurrentMember() {

        //myPostsSearchForm.setBackgroundColor(Color.parseColor("#eeeeee"));
        listPostForCurrentMemberService();
    }

    @Subscribe
    public void onGetListPostsEvent(PostsEvent event) {
       // spinner.setVisibility(View.GONE);
        postsList = event.posts;
//        ArrayList<HashMap<String, String>> posts = Post.postsInfoForItem(postsList);
        GlobalVariables.replyFromMyMarket = true;
        PostsAdapterOneProfile adapter = new PostsAdapterOneProfile(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);

    }

    @Subscribe
    public void onGetDeletedPostEvent(PostEvent event) {
        for(int i=0; i<postsList.size(); i++) {
            if(postsList.get(i).id == event.post.id) {
                postsList.remove(i);
                break;
            }
        }

        PostsAdapterOneProfile adapter = new PostsAdapterOneProfile(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
        adapter.makeList();
        loading.setVisibility(View.INVISIBLE);
        if(postsList.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.INVISIBLE);
    }


    @Subscribe
    public void onGetDeletedCommentEvent(CommentsEvent event) {
          if(event.comments.size() != 0) {
//        if(GlobalVariables.onDeleteComment) {
            for(int i = 0; i < postsList.size(); i++) {
                if (postsList.get(i).id == event.comments.get(event.comments.size()-1).postId) {
//            if(postsList.get(i).id == event.comment.postId) {
//                for(int j=0; j<postsList.get(i).comments.size(); i++) {
//                    if(postsList.get(i).comments.get(j).id == event.comment.id) {
//                        postsList.get(i).comments.remove(j);
//                        break;
//                    }
                    postsList.get(i).comments.remove(event.comments.size());
                    break;
//                }
                }
            }

//            GlobalVariables.onDeleteComment = false;

//            if(PostsAdapter.lastClickedPosts.contains(event.comment.postId))
//            if(PostsAdapter.lastClickedPosts.contains(event.comments.get(event.comments.size()-1).postId)) {
//                for(int i=0; i<PostsAdapter.lastClickedPosts.size(); i++) {
//                    if(PostsAdapter.lastClickedPosts.get(i) == event.comments.get(event.comments.size()-1).postId) {
//                        PostsAdapter.lastClickedPosts.remove(i);
//                        break;
//                    }
//                }
//            }

//            PostsAdapter.lastClickedPosts = new ArrayList<Integer>();

            PostsAdapter adapter = new PostsAdapter(inflater, this.getActivity(), postsMarket, Post.orderDescPost(postsList), accessToken, currentUser);
            adapter.makeList();
          }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        accessToken = preferences.getString("access_token","").replace("\"","");

        PostsAdapter.lastClickedPosts = new ArrayList<Integer>();

        this.inflater = inflater;

        return null;
    }
}