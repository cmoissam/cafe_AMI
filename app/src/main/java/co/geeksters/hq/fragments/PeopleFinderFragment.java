package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GPSTrackerHelpers;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_people_finder)
public class PeopleFinderFragment extends Fragment {

    @ViewById(R.id.tabhost)
    TabHost tabhost;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String accessToken;
    Member currentMember;
    List<Member> membersList = new ArrayList<Member>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return null;
    }

    @AfterViews
    public void treatments() {
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                PeopleFinderRadarFragment_ radarFragment = (PeopleFinderRadarFragment_) fragmentManager.findFragmentByTag("radar");
                PeopleFinderListFragment_ listFragment = (PeopleFinderListFragment_) fragmentManager.findFragmentByTag("list");
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(radarFragment!=null) {
                    fragmentTransaction.detach(radarFragment);
                }

                /** Detaches the applefragment if exists */
                if(listFragment!=null) {
                    fragmentTransaction.detach(listFragment);
                }

                if(tabId.equalsIgnoreCase("radar")){ /** If current tab is Info */
                    /** Create AndroidFragment and adding to fragmenttransaction */
                    GlobalVariables.afterViewsRadar = true;
                    fragmentTransaction.add(R.id.realtabcontent, new PeopleFinderRadarFragment_(), "radar");
                    /** Bring to the front, if already exists in the fragmenttransaction */
                } else {	/** If current tab is Market */
                    /** Create AppleFragment and adding to fragmenttransaction */
                    fragmentTransaction.add(R.id.realtabcontent, new PeopleFinderListFragment_(), "list");
                    /** Bring to the front, if already exists in the fragmenttransaction */
                }
                fragmentTransaction.commit();
            }};

        /** Setting tabchangelistener for the tab */
        tabhost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecAndroid = tabhost.newTabSpec("radar");
        tSpecAndroid.setIndicator("Radar",getResources().getDrawable(R.drawable.add));
        tSpecAndroid.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecAndroid);

        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecApple = tabhost.newTabSpec("list");
        tSpecApple.setIndicator("List",getResources().getDrawable(R.drawable.delete));
        tSpecApple.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecApple);
    }
}
