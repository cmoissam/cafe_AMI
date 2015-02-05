package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.services.HubService;

@EFragment(R.layout.fragment_hubs)
public class HubsFragment extends Fragment {

    // Listview Adapter
    ListViewHubAdapter adapterForHubList;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> hubs = new ArrayList<HashMap<String, String>>();
    String accessToken;
    List<Hub> hubsList = new ArrayList<Hub>();
    List<Hub> lastHubs = new ArrayList<Hub>();
    SharedPreferences.Editor editor;

    // List view
    @ViewById(R.id.list_view_hubs)
    ListView listViewHubs;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    /*@ViewById(R.id.displayAll)
    TextView displayAll;*/

    public void listAllHubsService(){
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            HubService hubService = new HubService(accessToken);
            hubService.listAllHubs();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listAllHubs(){
        listAllHubsService();
    }

    @Subscribe
    public void onGetListHubsEvent(HubsEvent event) {
        lastHubs = Hub.getLastSavedHubs(getActivity(), event.hubs);
        hubsList = Hub.concatenateTwoListsOfHubs(lastHubs, event.hubs);

        adapterForHubList = new ListViewHubAdapter(getActivity(), hubsList, lastHubs, listViewHubs);
        listViewHubs.setAdapter(adapterForHubList);

        ViewHelpers.setListViewHeightBasedOnChildren(listViewHubs);
    }

    @AfterViews
    public void addFooterToListview() {
        listViewHubs.addFooterView(new View(getActivity()), null, true);
    }

    @TextChange(R.id.inputSearch)
    public void searchForHub() {
        //displayAll.setVisibility(View.GONE);

        hubs = new ArrayList<HashMap<String, String>>();
        hubs = Hub.hubsInfoForItem(hubs, hubsList, 0, hubsList.size());

        final SimpleAdapter adapterToSearch = new SimpleAdapter(getActivity().getBaseContext(), hubs, R.layout.list_item_hub,
                new String[]{"hubName", "membersNumber"},
                new int[]{R.id.hubName, R.id.membersNumber});

        if(inputSearch.getText().toString().isEmpty()) {
            listViewHubs.setAdapter(adapterForHubList);
            ViewHelpers.setListViewHeightBasedOnChildren(listViewHubs);

            //displayAll.setVisibility(View.VISIBLE);
        } else {
            adapterToSearch.getFilter().filter(inputSearch.getText(), new Filter.FilterListener() {
                public void onFilterComplete(int count) {
                    if (adapterToSearch.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
                    else emptySearch.setVisibility(View.GONE);

                    // set listView with searched element
                    listViewHubs.setAdapter(adapterToSearch);
                    ViewHelpers.setListViewHeightBasedOnChildren(listViewHubs);
                }
            });
        }
    }

    @Click(R.id.clearContent)
    public void clearSearchInput() {
        inputSearch.setText("");
        emptySearch.setVisibility(View.GONE);
    }

    /*@Click(R.id.displayAll)
    public void displayAllMembers() {
        this.from = 10;
        this.to = hubsList.size();

        hubs = Hub.hubsInfoForItem(hubs, hubsList, this.from, this.to);

        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), hubs, R.layout.list_item_hub,
                new String[]{"hubName", "membersNumber"},
                new int[]{R.id.hubName, R.id.membersNumber});

        listViewHubs.setAdapter(adapter);
        listViewHubs.setItemsCanFocus(false);

        //displayAll.setVisibility(View.GONE);

        ViewHelpers.setListViewHeightBasedOnChildren(listViewHubs);

        this.from = 0;
        this.to = 10;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();

        BaseApplication.register(this);

        return null;
    }
}