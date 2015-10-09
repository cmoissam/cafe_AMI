package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.junit.After;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.DirectoryAdapter;
import co.geeksters.hq.adapter.FinderListAdapter;
import co.geeksters.hq.events.success.MembersAroundMeEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.RefreshRadarEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_people_finder_list)
public class PeopleFinderListFragment extends Fragment {
    // Listview Adapter
    FinderListAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members;
    String accessToken;
    Member currentMember;

    List<Member> membersList = new ArrayList<Member>();
    static String MEMBERS_AROUND_ME_KEY = "members_around_me";

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;


    @ViewById(R.id.peopleListForm)
    LinearLayout peopleListForm;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.turn_location_layout)
    RelativeLayout turnLocationLayout;

    @Subscribe
    public void onRefreshRadarEvent(RefreshRadarEvent event){

        if (currentMember.radarVisibility) {
            listViewMembers.setVisibility(View.VISIBLE);
            turnLocationLayout.setVisibility(View.INVISIBLE);
            emptySearch.setVisibility(View.INVISIBLE);

        if(GlobalVariables.listRadarLock) {
          GlobalVariables.listRadarLock = false;
            if (GeneralHelpers.isInternetAvailable(getActivity())) {
                MemberService memberService = new MemberService(accessToken);
                memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            }
        }
        } else {
            listViewMembers.setVisibility(View.INVISIBLE);
            turnLocationLayout.setVisibility(View.VISIBLE);
            emptySearch.setVisibility(View.INVISIBLE);
        }
    }

    @AfterViews
    public void checkRadarActivation() {
        if (!currentMember.radarVisibility) {
            listViewMembers.setVisibility(View.INVISIBLE);
            turnLocationLayout.setVisibility(View.VISIBLE);
        } else {
            listViewMembers.setVisibility(View.VISIBLE);
            turnLocationLayout.setVisibility(View.INVISIBLE);
            displayMembersAroundMeOnList();
        }
    }

    @Subscribe
    public void onGetListMembersAroundMeEvent(MembersAroundMeEvent event) {

        GlobalVariables.membersAroundMe = new ArrayList<Member>();
        GlobalVariables.membersAroundMe.addAll(event.members);

        displayMembersAroundMeOnList();
    }

    public void displayMembersAroundMeOnList() {
        membersList = Member.orderMembersByDescDistance(GlobalVariables.membersAroundMe);

        members = new ArrayList<HashMap<String, String>>();
        members = Member.membersInfoForItemByDistance(getActivity(), members, membersList);

//        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_list,
//                new String[]{"picture", "fullName", "hubName", "distance"},
//                new int[]{R.id.picture, R.id.fullName, R.id.hubName, R.id.distance});
//        listViewMembers.setAdapter(adapter);

        adapter = new FinderListAdapter(getActivity(), membersList, listViewMembers);
        listViewMembers.setAdapter(adapter);
//        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

        if(adapter.isEmpty()) {
            emptySearch.setVisibility(View.VISIBLE);
            turnLocationLayout.setVisibility(View.INVISIBLE);
            listViewMembers.setVisibility(View.INVISIBLE);
        }
        else{
            emptySearch.setVisibility(View.INVISIBLE);
        turnLocationLayout.setVisibility(View.INVISIBLE);
        listViewMembers.setVisibility(View.VISIBLE);
        }

        GlobalVariables.listRadarLock = true;
    }

    @ItemClick(R.id.list_view_members)
    public void setItemClickOnListViewMembers(int position) {
        GlobalVariables.finderList = true;
        GlobalVariables.isMenuOnPosition = false;
        GlobalVariables.MENU_POSITION = 5;

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(membersList.get(position), 0);
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);
        GlobalVariables.finderList = true;
        GlobalVariables.listRadarLock = true;

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApplication.unregister(this);
    }
}