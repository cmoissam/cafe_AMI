package co.geeksters.cafe_ami.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.adapter.HubMembersAdapter;
import co.geeksters.cafe_ami.events.success.MembersEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Hub;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.HubService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_one_hub_members)
public class OneHubMembersFragment extends Fragment {
    private static final String NEW_INSTANCE_HUBS_KEY = "hub_key";
    HubMembersAdapter adapter;
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    List<Member> membersList = new ArrayList<Member>();
    String accessToken;
    Member currentMember;
    Hub currentHub;
    LayoutInflater inflater;

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;


    @ViewById(R.id.membersSearchForm)
    LinearLayout membersSearchForm;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loading;

    public static OneHubMembersFragment_ newInstance(Hub hub) {
        OneHubMembersFragment_ fragment = new OneHubMembersFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_HUBS_KEY, hub);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void listAllMembersOfHubService() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            HubService hubService = new HubService(accessToken);
            hubService.getHubMembers(currentHub.id);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listAllMembersByPagination(){
        listAllMembersOfHubService();
        loading.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetListMembersOfHubEvent(MembersEvent event) {
        // TODO : Delete this bloc (data for test)
        membersList = event.members;

        for(int i=0; i<membersList.size(); i++) {
            membersList.get(i).hub = currentHub;
        }

        members = Member.membersInfoForItem(getActivity(), members, membersList);

        // Adding items to listview
//        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_directory,
//                  new String[]{"picture", "fullName", "hubName"},
//                  new int[]{R.id.picture, R.id.fullName, R.id.hubName});
//
//        listViewMembers.setAdapter(adapter);
//        listViewMembers.setItemsCanFocus(false);
        loading.setVisibility(View.INVISIBLE);
        GlobalVariables.finderList = false;
        adapter = new HubMembersAdapter(inflater, membersList, listViewMembers);
        listViewMembers.setAdapter(adapter);
        ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

        if(adapter.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
        else                  emptySearch.setVisibility(View.GONE);

    }

    @AfterViews
    public void addFooterToListview() {
        listViewMembers.addFooterView(new View(getActivity()), null, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);
        this.inflater = inflater;
        currentHub = (Hub) getArguments().getSerializable(NEW_INSTANCE_HUBS_KEY);

        return null;
    }
}