package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import co.geeksters.hq.R;
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
    // Listview Adapter
    SimpleAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    String accessToken;
    List<Member> membersList = new ArrayList<Member>();
    int from = 0;

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;

    @ViewById(R.id.membersProgress)
    ProgressBar membersProgress;

    @ViewById(R.id.membersSearchForm)
    LinearLayout membersSearchForm;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    @ViewById(R.id.displayAll)
    TextView displayAll;

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
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
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    public void searchForMembersByPaginationService(String search){
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.suggestionMember(search);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listAllMembersByPagination(){
        listAllMembersByPaginationService();
    }

    @Subscribe
    public void onGetListMembersByPaginationEvent(MembersEvent event) {
        this.from += GlobalVariables.SEARCH_SIZE;

        membersList = event.members;

        members = Member.membersInfoForItem(getActivity(), members, membersList);

        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_directory,
                new String[]{"picture", "fullName", "hubName"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName});

        listViewMembers.setAdapter(adapter);
        listViewMembers.setItemsCanFocus(false);

        if(members.size() < GlobalVariables.SEARCH_SIZE)
            displayAll.setVisibility(View.GONE);
        else
            displayAll.setVisibility(View.VISIBLE);

        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);
    }

    @AfterViews
    public void addFooterToListview() {
        listViewMembers.addFooterView(new View(getActivity()), null, true);
    }

    @ItemClick(R.id.list_view_members)
    public void setItemClickOnListViewMembers(int position){
        GlobalVariables.directory = true;
        GlobalVariables.isMenuOnPosition = false;
        GlobalVariables.MENU_POSITION = 5;
        GlobalVariables.isMenuOnPosition = false;

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(membersList.get(position));
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @TextChange(R.id.inputSearch)
    public void searchForMemberByPagination() {
        if(!inputSearch.getText().toString().isEmpty())
            searchForMembersByPaginationService(inputSearch.getText().toString());
        else {
            from = 0;
            members = new ArrayList<HashMap<String, String>>();
            listAllMembersByPaginationService();
        }

        /*adapter.getFilter().filter(inputSearch.getText(), new Filter.FilterListener() {
            public void onFilterComplete(int count) {
                if(adapter.isEmpty()) {
                    emptySearch.setVisibility(View.VISIBLE);
                }
                else emptySearch.setVisibility(View.GONE);

                ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);
            }
        });*/
    }

    @Subscribe
    public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {
        members = new ArrayList<HashMap<String, String>>();

        membersList = event.members;

        members = Member.membersInfoForItem(getActivity(), members, membersList);

        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_directory,
                new String[]{"picture", "fullName", "hubName"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName});

        listViewMembers.setAdapter(adapter);
        listViewMembers.setItemsCanFocus(false);

        if(adapter.isEmpty()) {
            emptySearch.setVisibility(View.VISIBLE);
        }
        else
            emptySearch.setVisibility(View.GONE);

        if(members.size() < GlobalVariables.SEARCH_SIZE)
            displayAll.setVisibility(View.GONE);
        else
            displayAll.setVisibility(View.VISIBLE);

        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);
    }

    @Click(R.id.clearContent)
    public void clearSearchInput() {
        inputSearch.setText("");
    }

    @Click(R.id.displayAll)
    public void displayAllMembers() {
        listAllMembersByPaginationService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //membersList = (List<Member>) getArguments().getSerializable(NEW_INSTANCE_MEMBERS_KEY);
        BaseApplication.register(this);
        return null;
    }
}