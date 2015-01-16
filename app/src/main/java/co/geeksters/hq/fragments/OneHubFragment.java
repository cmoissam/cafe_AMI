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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;

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

    public static OneHubFragment_ newInstance(Hub hub) {
        OneHubFragment_ fragment = new OneHubFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_HUB_KEY, hub);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null)
            hubToDisplay = (Hub) getArguments().getSerializable(NEW_INSTANCE_HUB_KEY);

        return null;
    }

    @AfterViews
    public void setNameAndHub() {
        headerHub.getBackground().setAlpha(100);

        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        hubName.setText(hubToDisplay.name);

        // TODO : Delete this bloc (test information)
        if(hubToDisplay.ambassadors.size() == 0){
            hubToDisplay.ambassadors = new ArrayList<Member>();
            hubToDisplay.ambassadors.add(Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", ""))));
            hubToDisplay.ambassadors.add(Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", ""))));
            hubToDisplay.ambassadors.add(Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", ""))));
        }

        if(hubToDisplay.ambassadors.size() == 0)
            ambassadorsTitle.setVisibility(View.GONE);

        for (int i=0; i<hubToDisplay.ambassadors.size(); i++) {
            ImageView imageAmbassador = new ImageView(getActivity().getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            imageAmbassador.setLayoutParams(layoutParams);
            //if(hubToDisplay.ambassadors.get(i).image.isEmpty())
                imageAmbassador.setImageResource(R.drawable.no_image_ambassador);
            // from cloudinary
            /*else
                imageAmbassador.setImageResource();*/
            ambassadorsLayout.addView(imageAmbassador);

            final int index = i;
            imageAmbassador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(hubToDisplay.ambassadors.get(index));
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
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
