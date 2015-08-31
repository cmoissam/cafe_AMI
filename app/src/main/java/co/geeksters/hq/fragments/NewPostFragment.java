package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.PostEvent;
import co.geeksters.hq.events.success.PostsEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.MemberService;
import co.geeksters.hq.services.PostService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ViewHelpers.showProgress;
import static co.geeksters.hq.models.Hub.getHubsByAlphabeticalOrder;

/**
 * Created by geeksters on 10/08/15.
 */
@EFragment(R.layout.fragment_new_post)
public class NewPostFragment extends Fragment {
    String accessToken;

    @ViewById(R.id.post_input)
    EditText postInput;

    @Click(R.id.send_button)
    public void createPost() {

        hide_keyboard(getActivity());

        if (postInput.getText().toString().length() < 3) {
            ViewHelpers.showPopup(getActivity(), "Info", "The post should contain more then 3 caracters");

        } else {

            Post post = new Post();
            post.content = postInput.getText().toString();
            post.title = "from android";

            PostService postService = new PostService(accessToken);

            postService.createPost(accessToken, post);
        }

    }
    @Subscribe
    public void onPostCreate(PostEvent event) {


        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onPostNotCreate(ConnectionFailureEvent event) {

        ViewHelpers.showPopup(getActivity(), "Warning", "You cannot create your post, try later please");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);

        getActivity().invalidateOptionsMenu();
    }

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }

    @AfterViews
    public void setPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);

        accessToken = preferences.getString("access_token", "").replace("\"", "");
    }

    // POUR SUPPRIMER LE BUTTON ADD DU MENU........
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();


        //fragment specific menu creation
    }*/

    /** Called whenever we call invalidateOptionsMenu() */


}

