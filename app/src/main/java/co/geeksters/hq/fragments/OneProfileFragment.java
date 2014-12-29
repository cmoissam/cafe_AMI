package co.geeksters.hq.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

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

@EFragment(R.layout.fragment_one_profile)
public class OneProfileFragment extends Fragment {

    @ViewById(R.id.tabhost)
    public TabHost tabhost;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    private MainActivity.TabsAdapter tabsAdapter;
    // Tab titles
    private String[] tabs = { "Info", "Market Place" };

    @AfterViews
    public void treatments() {
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =  getActivity().getSupportFragmentManager();
                OneProfileInfoFragment_ androidFragment = (OneProfileInfoFragment_) fm.findFragmentByTag("info");
                OneProfileMarketPlaceFragment_ appleFragment = (OneProfileMarketPlaceFragment_) fm.findFragmentByTag("market");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(androidFragment!=null) {
                    ft.detach(androidFragment);
                }

                /** Detaches the applefragment if exists */
                if(appleFragment!=null) {
                    ft.detach(appleFragment);
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

                }else{	/** If current tab is apple */
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
