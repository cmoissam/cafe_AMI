package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_market_place)
public class MarketPlaceFragment extends Fragment {
    @ViewById(R.id.tabhost)
    TabHost tabhost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalVariables.MENU_POSITION = 4;

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
            AllMarketPlaceFragment_ infoFragment = (AllMarketPlaceFragment_) fragmentManager.findFragmentByTag("all");
            MeMarketPlaceFragment_ marketFragment = (MeMarketPlaceFragment_) fragmentManager.findFragmentByTag("me");
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            /** Detaches the androidfragment if exists */
            if(infoFragment!=null) {
                fragmentTransaction.detach(infoFragment);
            }

            /** Detaches the applefragment if exists */
            if(marketFragment!=null) {
                fragmentTransaction.detach(marketFragment);
            }

            if(tabId.equalsIgnoreCase("all")){ /** If current tab is Info */
                /** Create AndroidFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new AllMarketPlaceFragment_(), "all");
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else {	/** If current tab is Market */
                /** Create AppleFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new MeMarketPlaceFragment_(), "me");
                /** Bring to the front, if already exists in the fragmenttransaction */
            }
            fragmentTransaction.commit();
        }};

        /** Setting tabchangelistener for the tab */
        tabhost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecAndroid = tabhost.newTabSpec("all");
        tSpecAndroid.setIndicator("All",getResources().getDrawable(R.drawable.add));
        tSpecAndroid.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecAndroid);

        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecApple = tabhost.newTabSpec("me");
        tSpecApple.setIndicator("Me",getResources().getDrawable(R.drawable.delete));
        tSpecApple.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecApple);

//        tabhost.setCurrentTab(defaultIndex);
    }
}
