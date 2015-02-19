package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.adapter.AmbassadorsAdapter;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.events.success.AmbassadorsEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.HubService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_one_hub)
public class OneHubFragment extends Fragment {
    @ViewById(R.id.oneHubScrollView)
    ScrollView oneHubScrollView;

    @ViewById(R.id.tabhost)
    TabHost tabhost;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.hubName)
    TextView hubName;

    @ViewById(R.id.headerOneHub)
    LinearLayout headerHub;

    @ViewById(R.id.ambassadors)
    LinearLayout ambassadorsLayout;

    @ViewById(R.id.ambassadorsTitle)
    TextView ambassadorsTitle;

    private static final String NEW_INSTANCE_HUB_KEY = "member_key";
    SharedPreferences preferences;
    Hub hubToDisplay;
    Member currentMember;
    String accessToken;
    LayoutInflater inflater;

    public static OneHubFragment_ newInstance(Hub hub) {
        OneHubFragment_ fragment = new OneHubFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_HUB_KEY, hub);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        if(getArguments() != null)
            hubToDisplay = (Hub) getArguments().getSerializable(NEW_INSTANCE_HUB_KEY);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        accessToken = preferences.getString("access_token","").replace("\"","");

        BaseApplication.register(this);

        return null;
    }

    public void listAllAmbassadorsOfHubService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            HubService hubService = new HubService(accessToken);
            hubService.getHubAmbassadors(hubToDisplay.id);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listAllAmbassadorsByPagination() {
        headerHub.getBackground().setAlpha(100);

        listAllAmbassadorsOfHubService();
    }

    @Subscribe
    public void onGetListAmbassadorsOfHubEvent(AmbassadorsEvent event) {

        hubName.setText(hubToDisplay.name);

        hubToDisplay.ambassadors = new ArrayList<Member>();
        hubToDisplay.ambassadors = event.members;

        if(hubToDisplay.ambassadors.size() != 0)
            ambassadorsTitle.setVisibility(View.VISIBLE);

        //TODO : set background hub image layout with the correspondant image

        AmbassadorsAdapter adapter = new AmbassadorsAdapter(this, hubToDisplay, this.inflater);
        adapter.makeList();
    }

    @AfterViews
    public void treatments() {
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
            OneHubNewsFragment_ newsFragment = (OneHubNewsFragment_) fragmentManager.findFragmentByTag("news");
            OneHubEventsFragment_ eventsFragment = (OneHubEventsFragment_) fragmentManager.findFragmentByTag("events");
            OneHubMembersFragment_ membersFragment = (OneHubMembersFragment_) fragmentManager.findFragmentByTag("members");
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(newsFragment!=null) {
                fragmentTransaction.detach(newsFragment);
            }

            if(eventsFragment!=null) {
                fragmentTransaction.detach(eventsFragment);
            }

            if(membersFragment!=null) {
                fragmentTransaction.detach(membersFragment);
            }

            if(tabId.equalsIgnoreCase("news")){ /** If current tab is Info */
                /** Create AndroidFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneHubNewsFragment_(), "news");
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else if(tabId.equalsIgnoreCase("events")){ /** If current tab is Info */
                /** Create AndroidFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneHubEventsFragment_(), "events");
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else {	/** If current tab is Market */
                /** Create AppleFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneHubMembersFragment_().newInstance(hubToDisplay), "members");
                /** Bring to the front, if already exists in the fragmenttransaction */
            }
            fragmentTransaction.commit();
        }};

        /** Setting tabchangelistener for the tab */
        tabhost.setOnTabChangedListener(tabChangeListener);

        TabHost.TabSpec tSpecNews = tabhost.newTabSpec("news");
        tSpecNews.setIndicator("News",getResources().getDrawable(R.drawable.add));
        tSpecNews.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecNews);

        TabHost.TabSpec tSpecEvents = tabhost.newTabSpec("events");
        tSpecEvents.setIndicator("Events",getResources().getDrawable(R.drawable.delete));
        tSpecEvents.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecEvents);

        TabHost.TabSpec tSpecMembers = tabhost.newTabSpec("members");
        tSpecMembers.setIndicator("Members",getResources().getDrawable(R.drawable.delete));
        tSpecMembers.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecMembers);
    }
}
