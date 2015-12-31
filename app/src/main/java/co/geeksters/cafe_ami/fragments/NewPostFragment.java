package co.geeksters.cafe_ami.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.adapter.TodoAdapter;
import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.success.MembersEvent;
import co.geeksters.cafe_ami.events.success.MembersSearchEvent;
import co.geeksters.cafe_ami.events.success.PostEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.models.Post;
import co.geeksters.cafe_ami.services.MemberService;
import co.geeksters.cafe_ami.services.PostService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by geeksters on 10/08/15.
 */
@EFragment(R.layout.fragment_new_post)
public class NewPostFragment extends Fragment {

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

    public boolean onRefresh = false;
    public boolean noMoreMembers = false;

    public boolean waitForSearch = false;
    public boolean firstTime = true;
    public boolean firstTimeSearch = false;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    public int lastPosition = 0;

    public View footer;

    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;


    @ViewById(R.id.membersSearchForm)
    LinearLayout membersSearchForm;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loading;

    @ViewById(R.id.find_by_city_or_name)
    TextView findByCityOrName;

    @ViewById(R.id.textView_no_result)
    TextView textViewNoResult;
    TodoAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    List<Member> concernedMembers = new ArrayList<Member>();

    String accessToken;
    List<Member> membersList = new ArrayList<Member>();
    int from = 0;
    boolean onSearch = false;


    public void listAllMembersByPaginationService(){
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.listAllMembersByPaginationForTodo(this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    public void searchForMembersByPaginationService(String search){
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

            MemberService memberService = new MemberService(accessToken);
            memberService.searchForMembersForTodo(search, this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection), true);
        }
    }

    @Click(R.id.send_button)
    public void createPost() {

        hide_keyboard(getActivity());

        if (postInput.getText().toString().length() < 3) {
            ViewHelpers.showPopup(getActivity(), "info", getResources().getString(R.string.post_error), false);

        } else {

            sendButton.setEnabled(false);

            Post post = new Post();
            post.content = postInput.getText().toString();
            post.title = "from android";
            post.interests = addedInterests.getText().toString();

            PostService postService = new PostService(accessToken);

            postService.createPost(accessToken, post, adapter.concernedMembers);
        }

    }
    @Click(R.id.addButtonInterest)
    public void addIterests() {

        if(interest.getText().toString().length()>0) {
            addedInterests.setText(addedInterests.getText().toString() + " #" + interest.getText().toString());
            interest.setText("");
        }
        else{
            interest.setError(getString(R.string.error_field_required));
        }
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


        postInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    postInput.setMaxHeight(50*(int)GlobalVariables.d);
                }
                if (hasFocus) {
                    postInput.setMaxHeight(100*(int)GlobalVariables.d);
                }
            }
        });


        accessToken = preferences.getString("access_token", "").replace("\"", "");
        Member currentUser = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));



    if(PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).getBoolean("visit_info_add_post",true)) {


        PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).edit().putBoolean("visit_info_add_post", false).commit();
        LayoutInflater inflater = GlobalVariables.activity.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.pop_up_info_opportunity, null);

        ImageView cancelImage = (ImageView) dialoglayout.findViewById(R.id.cancel_popup);
        TextView infoText = (TextView) dialoglayout.findViewById(R.id.popup_info_text);

        infoText.setText("When creating an Opportunity, you can tag specific people or by interest, and they'll get a handy notification.");

        Typeface typeFace = Typeface.createFromAsset(GlobalVariables.activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        infoText.setTypeface(typeFace);

        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVariables.activity);
        builder.setView(dialoglayout);
        builder.setCancelable(true);
        final AlertDialog ald = builder.show();
        ald.setCancelable(true);

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ald.dismiss();
            }
        });

        }


        Typeface typeFace =Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, currentUser.image);
        fullname.setText(currentUser.fullName);
        postInput.setTypeface(typeFace);
        fullname.setTypeface(typeFace);
        daatePost.setTypeface(typeFace);
        sendButton.setTypeface(typeFace);

        listAllMembersByPaginationService();
        loading.setVisibility(View.VISIBLE);
        listViewMembers.setVisibility(View.INVISIBLE);

    }
    @Subscribe
    public void onGetListMembersByPaginationEvent(MembersEvent event) {


        if(firstTime && firstTimeSearch) {

        }
        else {
            this.from += GlobalVariables.SEARCH_SIZE;
            waitForSearch = false;
            loading.setVisibility(View.INVISIBLE);
            listViewMembers.setVisibility(View.VISIBLE);

            membersList.addAll(event.members);

            members = Member.membersInfoForItem(getActivity(), members, membersList);

            GlobalVariables.finderList = false;
            adapter = new TodoAdapter(getActivity(), membersList, listViewMembers, concernedMembers);
            listViewMembers.setAdapter(adapter);

            ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

            listViewMembers.removeFooterView(footer);


            if (members.size() < GlobalVariables.SEARCH_SIZE) {
                noMoreMembers = true;
            }
            if (event.members.size() == 0)
                noMoreMembers = true;


            if (adapter.isEmpty())
                emptySearch.setVisibility(View.VISIBLE);
            else
                emptySearch.setVisibility(View.INVISIBLE);


            if (onRefresh) {
                //TODO scroll to end of list
                listViewMembers.setSelection(lastPosition);
            }
            onRefresh = false;
        }
        firstTime = false;
    }

    @TextChange(R.id.inputSearch)
    public void searchForMemberByPagination() {


        emptySearch.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        listViewMembers.setVisibility(View.INVISIBLE);
        firstTimeSearch = true;

        Runnable task = new Runnable() {
            public void run() {
                if (!waitForSearch) {
                    waitForSearch = true;
                    from = 0;
                    membersList = new ArrayList<Member>();
                    members = new ArrayList<HashMap<String, String>>();

                    noMoreMembers = false;
                    if (!inputSearch.getText().toString().isEmpty()) {
                        searchForMembersByPaginationService(inputSearch.getText().toString());
                        listViewMembers.setVisibility(View.INVISIBLE);
                        loading.setVisibility(View.VISIBLE);
                    }
                    else {
                        listAllMembersByPaginationService();
                        loading.setVisibility(View.VISIBLE);
                        listViewMembers.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };

        worker.schedule(task, 2, TimeUnit.SECONDS);

    }

    @AfterViews
    public void listViewSetting(){


        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        //findByCityOrName.setTypeface(typeFace);
        inputSearch.setTypeface(typeFace);
        textViewNoResult.setTypeface(typeFace);

        footer = getActivity().getLayoutInflater().inflate(R.layout.refresh_list_view, null);

        listViewMembers.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    if (!onRefresh) {
                        if (!noMoreMembers) {
                            onRefresh = true;
                            if (!inputSearch.getText().toString().isEmpty())
                                searchForMembersByPaginationService(inputSearch.getText().toString());

                            else {
                                listAllMembersByPaginationService();
                            }
                            lastPosition = listViewMembers.getLastVisiblePosition();
                            listViewMembers.addFooterView(footer);
                        }
                    }
                }
            }
        });


    }



    @Subscribe
    public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {

        this.from += GlobalVariables.SEARCH_SIZE;


        loading.setVisibility(View.INVISIBLE);
        listViewMembers.setVisibility(View.VISIBLE);
        membersList.addAll(event.members);

        adapter = new TodoAdapter(getActivity(), membersList, listViewMembers,concernedMembers);
        listViewMembers.setAdapter(adapter);
        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

        listViewMembers.removeFooterView(footer);

        if(adapter.isEmpty()) {
            emptySearch.setVisibility(View.VISIBLE);
        }
        else
            emptySearch.setVisibility(View.INVISIBLE);
        if(onRefresh)
        {
            //TODO scroll to end of list
            listViewMembers.setSelection(lastPosition);
        }

        onRefresh = false;

        if(members.size() < GlobalVariables.SEARCH_SIZE){
            noMoreMembers = true;
        }
        if(event.members.size() == 0)
            noMoreMembers = true;

        waitForSearch = false;

    }


}

