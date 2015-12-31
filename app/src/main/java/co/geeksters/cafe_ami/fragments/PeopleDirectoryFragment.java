package co.geeksters.cafe_ami.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
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
import co.geeksters.cafe_ami.adapter.DirectoryAdapter;
import co.geeksters.cafe_ami.events.success.MembersEvent;
import co.geeksters.cafe_ami.events.success.MembersSearchEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.MemberService;

@EFragment(R.layout.fragment_people_directory)
public class PeopleDirectoryFragment extends Fragment {

    private static final String NEW_INSTANCE_MEMBERS_KEY = "members_key";
    DirectoryAdapter adapter;
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    String accessToken;
    List<Member> membersList = new ArrayList<Member>();
    int from = 0;
    boolean onSearch = false;

    public boolean onRefresh = false;
    public boolean noMoreMembers = false;
    public int lastPosition = 0;

    public boolean waitForSearch = false;
    public boolean firstTime = true;
    public boolean firstTimeSearch = false;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    public View footer;

    private Tracker mTracker;

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loadingLayout;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;

    @ViewById(R.id.membersSearchForm)
    LinearLayout membersSearchForm;


    @ViewById(R.id.find_by_city_or_name)
    TextView findByCityOrName;

    @ViewById(R.id.textView_no_result)
    TextView textViewNoResult;

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
        GlobalVariables.menuPart=1;
        GlobalVariables.menuDeep = 0;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);



        return null;
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.needReturnButton = false;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_directory_fragment));
    }

    @AfterViews
    public void listViewSetting(){

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        findByCityOrName.setTypeface(typeFace);
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


    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }

    public static PeopleDirectoryFragment_ newInstance(List<Member> members) {
        PeopleDirectoryFragment_ fragment = new PeopleDirectoryFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBERS_KEY, (java.io.Serializable) members);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void listAllMembersByPaginationService(){
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.listAllMembersByPaginationOrSearch(this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    public void searchForMembersByPaginationService(String search){

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.searchForMembersFromKey(search,this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listAllMembersByPagination(){
        if(GlobalVariables.backtosearch)
        {
            firstTimeSearch = false;
            firstTime = false;
            inputSearch.setText(GlobalVariables.lastSearchPeopleDirectory);
            loadingLayout.setVisibility(View.INVISIBLE);
            from = GlobalVariables.lastMemberSearchPeopleDirectory.size();
            membersList.addAll(GlobalVariables.lastMemberSearchPeopleDirectory);
            adapter = new DirectoryAdapter(getActivity(), membersList, listViewMembers);
            listViewMembers.setAdapter(adapter);
        }
        else {
            GlobalVariables.lastSearchPeopleDirectory = "";
            loadingLayout.setVisibility(View.VISIBLE);
            listAllMembersByPaginationService();
        }
    }

    @Subscribe
    public void onGetListMembersByPaginationEvent(MembersEvent event) {

        if(firstTime && firstTimeSearch) {

        }
        else {
            this.from += GlobalVariables.SEARCH_SIZE;
            waitForSearch = false;

            membersList.addAll(event.members);


            loadingLayout.setVisibility(View.INVISIBLE);

            GlobalVariables.finderList = false;
            adapter = new DirectoryAdapter(getActivity(), membersList, listViewMembers);
            listViewMembers.setAdapter(adapter);
            listViewMembers.removeFooterView(footer);


            if (event.members.size() < GlobalVariables.SEARCH_SIZE) {
                noMoreMembers = true;
            }
            if (event.members.size() == 0)
                noMoreMembers = true;


            if (adapter.isEmpty())

                emptySearch.setVisibility(View.VISIBLE);
            else
                emptySearch.setVisibility(View.INVISIBLE);

            if (onRefresh) {
                listViewMembers.setSelection(lastPosition);

            }


            onRefresh = false;
        }

        firstTime = false;

    }



    @TextChange(R.id.inputSearch)
    public void searchForMemberByPagination() {

        if(GlobalVariables.backtosearch)
        {
            GlobalVariables.backtosearch = false;
        }
        else {
            firstTimeSearch = true;
            GlobalVariables.lastSearchPeopleDirectory = inputSearch.getText().toString();

            emptySearch.setVisibility(View.INVISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);

            Runnable task = new Runnable() {
                public void run() {
                    if (!waitForSearch) {
                        waitForSearch = true;
                        from = 0;
                        membersList = new ArrayList<Member>();
                        members = new ArrayList<HashMap<String, String>>();

                        noMoreMembers = false;
                        if (!inputSearch.getText().toString().isEmpty())
                            searchForMembersByPaginationService(inputSearch.getText().toString());
                        else {
                            listAllMembersByPaginationService();
                        }
                       /* mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Meet the family")
                                .setAction("Search")
                                .setLabel(inputSearch.getText().toString())
                                .build());*/
                        }
                }
            };

            worker.schedule(task, 2, TimeUnit.SECONDS);

        }

    }

    @Subscribe
    public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {


        waitForSearch = false;

        this.from += GlobalVariables.SEARCH_SIZE;

        membersList.addAll(event.members);

        loadingLayout.setVisibility(View.INVISIBLE);

        adapter = new DirectoryAdapter(getActivity(), membersList, listViewMembers);
        listViewMembers.setAdapter(adapter);


        listViewMembers.removeFooterView(footer);

        if(adapter.isEmpty()) {
            emptySearch.setVisibility(View.VISIBLE);
        }
        else
            emptySearch.setVisibility(View.INVISIBLE);

        if(event.members.size() < GlobalVariables.SEARCH_SIZE)
            noMoreMembers = true;


        if(event.members.size() == 0)
           noMoreMembers = true;

        if(onRefresh)
        {
            listViewMembers.setSelection(lastPosition);

        }

        onRefresh = false;



    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        BaseApplication application = (BaseApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        Log.i("analytics", "Setting screen name: " + "MEET THE FAMILY");
        mTracker.setScreenName("MEET THE FAMILY");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}