package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.AndroidFragment;
import co.geeksters.hq.activities.AppleFragment;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.activities.MainActivity;
import co.geeksters.hq.activities.PageOneFragment;
import co.geeksters.hq.activities.PageTwoFragment;
import co.geeksters.hq.adapter.TabsAdapter;
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

    TabsAdapter tabsAdapter;
    SharedPreferences preferences;
    Member currentMember;

    @AfterViews
    public void setNameAndHub(){
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        fullName.setText(currentMember.fullName);
        hubName.setText(currentMember.hub.name);
    }

    @AfterViews
    public void treatments() {
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =  getActivity().getSupportFragmentManager();
                OneProfileInfoFragment_ infoFragment = (OneProfileInfoFragment_) fm.findFragmentByTag("info");
                OneProfileMarketPlaceFragment_ marketFragment = (OneProfileMarketPlaceFragment_) fm.findFragmentByTag("market");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(infoFragment!=null) {
                    ft.detach(infoFragment);
                }

                /** Detaches the applefragment if exists */
                if(marketFragment!=null) {
                    ft.detach(marketFragment);
                }

                /** If current tab is android */
                if(tabId.equalsIgnoreCase("info")){

                    //if(androidFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new OneProfileInfoFragment_(), "info");
                    //}else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                      //  ft.attach(androidFragment);
                    //}

                } else {	/** If current tab is apple */
                    //if(appleFragment==null){
                        /** Create AppleFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new OneProfileMarketPlaceFragment_(), "market");
                    //}else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                    //    ft.attach(appleFragment);
                    //}
                }
                ft.commit();
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
