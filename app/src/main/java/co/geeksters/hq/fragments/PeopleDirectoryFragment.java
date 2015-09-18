package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import java.util.HashMap;
import java.util.List;
import co.geeksters.hq.R;
import co.geeksters.hq.adapter.DirectoryAdapter;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.MembersSearchEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

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

    public View footer;

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
                            listViewMembers.addFooterView(footer, null, false);
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
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    public void searchForMembersByPaginationService(String search){
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

            MemberService memberService = new MemberService(accessToken);
            memberService.searchForMembersFromKey(search,this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listAllMembersByPagination(){

        loadingLayout.setVisibility(View.VISIBLE);
        listAllMembersByPaginationService();

    }

    @Subscribe
    public void onGetListMembersByPaginationEvent(MembersEvent event) {
        this.from += GlobalVariables.SEARCH_SIZE;


        membersList.addAll(event.members);

        members = Member.membersInfoForItem(getActivity(), members, membersList);

        loadingLayout.setVisibility(View.INVISIBLE);
        listViewMembers.removeFooterView(footer);
        GlobalVariables.finderList = false;
        adapter = new DirectoryAdapter(getActivity(), membersList, listViewMembers);
        listViewMembers.setAdapter(adapter);
        onRefresh = false;

        if(members.size() < GlobalVariables.SEARCH_SIZE){
            noMoreMembers = true;
        }



        if(adapter.isEmpty())
            emptySearch.setVisibility(View.VISIBLE);
        else
            emptySearch.setVisibility(View.INVISIBLE);
    }



    @TextChange(R.id.inputSearch)
    public void searchForMemberByPagination() {
        from = 0;
        membersList = new ArrayList<Member>();
        members = new ArrayList<HashMap<String, String>>();

        loadingLayout.setVisibility(View.VISIBLE);
        if(!inputSearch.getText().toString().isEmpty())
            searchForMembersByPaginationService(inputSearch.getText().toString());
        else {
            listAllMembersByPaginationService();
        }

    }

    @Subscribe
    public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {

        this.from += GlobalVariables.SEARCH_SIZE;


        membersList.addAll(event.members);

        loadingLayout.setVisibility(View.INVISIBLE);


        listViewMembers.removeFooterView(footer);
        adapter = new DirectoryAdapter(getActivity(), membersList, listViewMembers);
        listViewMembers.setAdapter(adapter);
        onRefresh = false;

        if(adapter.isEmpty()) {
            emptySearch.setVisibility(View.VISIBLE);
        }
        else
            emptySearch.setVisibility(View.INVISIBLE);

        if(event.members.size() < GlobalVariables.SEARCH_SIZE)
            noMoreMembers = true;


        if(event.members.size() == 0)
           noMoreMembers = true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);
        return null;
    }
}