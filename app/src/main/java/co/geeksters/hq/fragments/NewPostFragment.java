package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.PostEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.services.PostService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by geeksters on 10/08/15.
 */
@EFragment(R.layout.fragment_new_post)
public class NewPostFragment extends Fragment {
    String accessToken;

    @ViewById(R.id.post_input)
    EditText postInput;
    @ViewById(R.id.picture)
    ImageView picture;
    @ViewById(R.id.fullName)
    TextView fullname;
    @ViewById(R.id.datePost)
    TextView daatePost;
    @ViewById(R.id.send_button)
    Button sendButton;
    @ViewById(R.id.addButtonInterest)
    ImageButton addButtonInterest;
    @ViewById(R.id.added_interests)
    EditText addedInterests;
    @ViewById(R.id.interest)
    EditText interest;


    @Click(R.id.send_button)
    public void createPost() {

        hide_keyboard(getActivity());

        if (postInput.getText().toString().length() < 3) {
            ViewHelpers.showPopup(getActivity(), "info",getResources().getString(R.string.post_error),false);

        } else {

            sendButton.setEnabled(false);

            Post post = new Post();
            post.content = postInput.getText().toString();
            post.title = "from android";
            post.interests = addedInterests.getText().toString();

            PostService postService = new PostService(accessToken);

            postService.createPost(accessToken, post);
        }

    }
    @Click(R.id.addButtonInterest)
    public void addIterests() {

        addedInterests.setText(addedInterests.getText().toString()+" #"+interest.getText().toString());
        interest.setText("");
    }



    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.needReturnButton = true;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle("OPPORTUNITY");
    }
    @Subscribe
    public void onPostCreate(PostEvent event) {

        sendButton.setEnabled(true);
        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_left,R.anim.anim_exit_right);

        fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onPostNotCreate(ConnectionFailureEvent event) {
        GlobalVariables.MENU_POSITION = 10;
        sendButton.setEnabled(true);
        ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
        GlobalVariables.menuDeep = 1;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
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
        Member currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, currentUser.image);
        fullname.setText(currentUser.fullName);
        postInput.setTypeface(typeFace);
        fullname.setTypeface(typeFace);
        daatePost.setTypeface(typeFace);
        sendButton.setTypeface(typeFace);
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

