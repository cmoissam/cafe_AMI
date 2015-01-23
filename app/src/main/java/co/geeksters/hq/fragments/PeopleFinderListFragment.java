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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.MembersEvent;
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
    SimpleAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members;
    String accessToken;
    Member currentMember;
    List<Member> membersList = new ArrayList<Member>();
    static String MEMBERS_AROUND_ME_KEY = "members_around_me";

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    @ViewById(R.id.peopleListProgress)
    ProgressBar peopleListProgress;

    @ViewById(R.id.peopleListForm)
    LinearLayout peopleListForm;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    @AfterViews
    public void listAllMembersAroundMeService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

//    @Subscribe
//    public void onSaveLocationMemberEvent(SaveMemberEvent event) {
//        // save the current Member
//        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
//        editor.commit();
//
//        listAllMembersAroundMeService();
//    }

    @Subscribe
    public void onGetListMembersAroundMeEvent(MembersEvent event) {
        membersList = GlobalVariables.membersAroundMe;
        displayMembersAroundMeOnList();
    }

    public void displayMembersAroundMeOnList() {
        membersList = Member.orderMembersByDescDistance(membersList);
        members = new ArrayList<HashMap<String, String>>();
        members = Member.membersInfoForItemByDistance(getActivity(), members, membersList);

        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_list,
                new String[]{"picture", "fullName", "hubName", "distance"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName, R.id.distance});

        listViewMembers.setAdapter(adapter);
        listViewMembers.setItemsCanFocus(false);

        if(adapter.isEmpty())
            emptySearch.setVisibility(View.VISIBLE);
        else
            emptySearch.setVisibility(View.GONE);

        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);
    }

    @ItemClick(R.id.list_view_members)
    public void setItemClickOnListViewMembers(int position) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(membersList.get(position));
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null)
            membersList = (List<Member>) getArguments().getSerializable(MEMBERS_AROUND_ME_KEY);

        BaseApplication.register(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return null;
    }
}
