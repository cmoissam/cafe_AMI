package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.junit.runner.Describable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

@EFragment(R.layout.fragment_people_directory)
public class PeopleDirectoryFragment extends Fragment {

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

    // Listview Adapter
    SimpleAdapter adapter;

    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();

    String accessToken;

    @AfterViews
    public void setPreferences(){
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");
    }

    @AfterViews
    public void setListMembersByPagination(){

    }

    @AfterViews
    public void setListMembersContent(){
        HashMap<String, String> map;

        for(int i = 0; i < 10; i++) {
            map = new HashMap<String, String>();

            /*if()
            map.put("picture", "");
            else*/
                map.put("picture", String.valueOf(R.drawable.no_image_member));
            map.put("fullName", "Soukaina");
            map.put("hubName", "Hub Marrakech");

            members.add(map);
        }
    }

    @AfterViews
    public void setListMembersAdapter(){
        // Adding items to listview
        adapter = new SimpleAdapter(getActivity().getBaseContext(), members, R.layout.list_item_people_directory,
                new String[]{"picture", "fullName", "hubName"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName});
        //adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_people_directory, R.id.product_name, products);
        listViewMembers.setAdapter(adapter);
    }

    @TextChange(R.id.inputSearch)
    public void filterListMembers(){
        adapter.getFilter().filter(inputSearch.getText());
        if(adapter.isEmpty())
            emptySearch.setVisibility(View.VISIBLE);
    }

    @Click(R.id.clearContent)
    public void clearSearchInput(){
        inputSearch.setText("");
    }

    /*public static Fragment newInstance(List<Member> members) {
        PeopleDirectoryFragment_ fragment = new PeopleDirectoryFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable("members", (java.io.Serializable) members);
        fragment.setArguments(bundle);

        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //members = (ArrayList<HashMap<String, String>>) getArguments().getSerializable("members");

        return null;
    }
}
