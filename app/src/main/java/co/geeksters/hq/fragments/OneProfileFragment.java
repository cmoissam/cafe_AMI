package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.AndroidFragment;
import co.geeksters.hq.activities.AppleFragment;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.activities.MainActivity;
import co.geeksters.hq.activities.PageOneFragment;
import co.geeksters.hq.activities.PageTwoFragment;
import co.geeksters.hq.adapter.TabsAdapter;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.models.Member;

import static co.geeksters.hq.global.helpers.ParseHelper.createJsonElementFromString;

@EFragment(R.layout.fragment_one_profile)
public class OneProfileFragment extends Fragment {

    @ViewById(R.id.tabhost)
    public TabHost tabhost;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.fullName)
    TextView fullName;

    @ViewById(R.id.hubName)
    TextView hubName;

    private static final String NEW_INSTANCE_MEMBER_KEY = "member_key";
    SharedPreferences preferences;
    Member memberToDisplay;
    Member profileMember;
    //static Boolean seeProfile = false;

    public static OneProfileFragment_ newInstance(Member member) {
        //seeProfile = true;

        OneProfileFragment_ fragment = new OneProfileFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBER_KEY, member);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null)
            profileMember = (Member) getArguments().getSerializable(NEW_INSTANCE_MEMBER_KEY);

        return null;
    }

    @AfterViews
    public void setNameAndHub(){
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        if(profileMember == null) {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
            GlobalVariables.isCurrentMember = true;
        } else {
            memberToDisplay = profileMember;
            GlobalVariables.isCurrentMember = false;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profile_member", ParseHelper.createJsonStringFromModel(profileMember));
            editor.commit();
        }

        fullName.setText(memberToDisplay.fullName);
        hubName.setText(memberToDisplay.hub.name);
    }

    @AfterViews
    public void treatments() {
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                OneProfileInfoFragment_ infoFragment = (OneProfileInfoFragment_) fragmentManager.findFragmentByTag("info");
                OneProfileMarketPlaceFragment_ marketFragment = (OneProfileMarketPlaceFragment_) fragmentManager.findFragmentByTag("market");
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(infoFragment!=null) {
                    fragmentTransaction.detach(infoFragment);
                }

                /** Detaches the applefragment if exists */
                if(marketFragment!=null) {
                    fragmentTransaction.detach(marketFragment);
                }

                if(tabId.equalsIgnoreCase("info")){ /** If current tab is Info */
                    /** Create AndroidFragment and adding to fragmenttransaction */
                    fragmentTransaction.add(R.id.realtabcontent,new OneProfileInfoFragment_(), "info");
                    /** Bring to the front, if already exists in the fragmenttransaction */
                } else {	/** If current tab is Market */
                    /** Create AppleFragment and adding to fragmenttransaction */
                    fragmentTransaction.add(R.id.realtabcontent,new OneProfileMarketPlaceFragment_(), "market");
                    /** Bring to the front, if already exists in the fragmenttransaction */
                }
                fragmentTransaction.commit();
            }
        };

        /** Setting tabchangelistener for the tab */
        tabhost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecAndroid = tabhost.newTabSpec("info");
        tSpecAndroid.setIndicator("Info",getResources().getDrawable(R.drawable.add));
        tSpecAndroid.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecAndroid);

        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecApple = tabhost.newTabSpec("market");
        tSpecApple.setIndicator("Market Place",getResources().getDrawable(R.drawable.delete));
        tSpecApple.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecApple);
    }
}
