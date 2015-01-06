package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.models.Member;

@EFragment(R.layout.fragment_people_directory)
public class PeopleDirectoryFragment extends Fragment {

    private static final String NEW_INSTANCE_MEMBERS_KEY = "members_key";
    // Listview Adapter
    SimpleAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    String accessToken;
    List<Member> membersList = new ArrayList<Member>();

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

    public static PeopleDirectoryFragment_ newInstance(List<Member> members) {
        PeopleDirectoryFragment_ fragment = new PeopleDirectoryFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBERS_KEY, (java.io.Serializable) members);
        fragment.setArguments(bundle);

        return fragment;
    }

    @AfterViews
    public void addFooterToListview() {
        listViewMembers.addFooterView(new View(getActivity()), null, true);
    }

    @AfterViews
    public void setPreferences(){
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");
    }

    @AfterViews
    public void setListMembersContent(){
        HashMap<String, String> map;

        for(int i = 0; i < membersList.size(); i++) {
            map = new HashMap<String, String>();

            /*if(!membersList.get(i).image.equals(""))
                map.put("picture", membersList.get(i).image);
            else*/
                map.put("picture", String.valueOf(R.drawable.no_image_member));
            map.put("fullName", membersList.get(i).fullName);
            map.put("hubName", membersList.get(i).hub.name);

            members.add(map);
        }
    }

    @AfterViews
    public void setListMembersAdapter(){
        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_directory,
                new String[]{"picture", "fullName", "hubName"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName});

        listViewMembers.setAdapter(adapter);
        listViewMembers.setItemsCanFocus(false);
    }

    @ItemClick(R.id.list_view_members)
    public void setItemClickOnListViewMembers(int position){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(membersList.get(position));
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @TextChange(R.id.inputSearch)
    public void filterListMembers() {
        adapter.getFilter().filter(inputSearch.getText(), new Filter.FilterListener() {
            public void onFilterComplete(int count) {
                Log.d("Size adapter : ","" + adapter.getCount());
                if(adapter.isEmpty()) emptySearch.setVisibility(View.VISIBLE);
                else emptySearch.setVisibility(View.GONE);
            }
        });
    }

    @Click(R.id.clearContent)
    public void clearSearchInput(){
        inputSearch.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        membersList = (List<Member>) getArguments().getSerializable(NEW_INSTANCE_MEMBERS_KEY);

        return null;
    }
}
