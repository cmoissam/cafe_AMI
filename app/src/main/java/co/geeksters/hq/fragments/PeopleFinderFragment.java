package co.geeksters.hq.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
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
import co.geeksters.hq.events.success.RefreshRadarEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.events.success.UpdateMemberLocationEvent;
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

    @ViewById(R.id.switchRadarActivation)
    Switch switchRadarActivation;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String accessToken;
    Member currentMember;
    String tabIds = "radar";


    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
        GlobalVariables.inRadarFragement = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return null;
    }

    public void updateLocationAndVisibility(){
        if (!GeneralHelpers.isGPSEnabled(getActivity())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            verifyGpsActivation();
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            verifyGpsActivation();
        }
    }

    public void verifyGpsActivation() {
        GPSTrackerHelpers gps = new GPSTrackerHelpers(getActivity());

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Member updatedMember = currentMember;

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // update longitude latitude
            updatedMember.longitude = (float) longitude;
            updatedMember.latitude  = (float) latitude;
        } else {
            ViewHelpers.buildAlertMessageNoGps(getActivity());
        }

        if (GeneralHelpers.isInternetAvailable(getActivity())) {
            GlobalVariables.updatePositionFromRadar = true;
            MemberService memberService = new MemberService(accessToken);
            updatedMember.radarVisibility = true;
            memberService.updateMember(currentMember.id, updatedMember);
            GlobalVariables.updatePosition = true;
            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 1;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    public void updateVisibility() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Member updatedMember = currentMember;

        if (GeneralHelpers.isInternetAvailable(getActivity())) {
            GlobalVariables.updatePositionFromRadar = true;
            MemberService memberService = new MemberService(accessToken);
            updatedMember.radarVisibility = false;
            memberService.updateMember(currentMember.id, updatedMember);
            GlobalVariables.updatePosition = true;
            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 1;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        GlobalVariables.inRadarFragement = false;
    }

    @AfterViews
    public void switchTreatments() {
        if (currentMember.radarVisibility)
            switchRadarActivation.setChecked(true);
        else
            switchRadarActivation.setChecked(false);

        //attach a listener to check for changes in state
        switchRadarActivation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateLocationAndVisibility();
                } else {
                    updateVisibility();
                }
            }
        });
    }

    @Subscribe
    public void onSaveLocationMemberEvent(UpdateMemberLocationEvent event) {
        if (GlobalVariables.updatePositionFromRadar) {
            GlobalVariables.updatePositionFromRadar = false;
            editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
            editor.commit();
            afterSwitchTreatments();
        }
    }

    public void afterSwitchTreatments(){
        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        PeopleFinderRadarFragment_ radarFragment = (PeopleFinderRadarFragment_) fragmentManager.findFragmentByTag("radar");
        PeopleFinderListFragment_ listFragment = (PeopleFinderListFragment_) fragmentManager.findFragmentByTag("list");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        /** Detaches the androidfragment if exists */
        if(radarFragment!=null) {
            fragmentTransaction.detach(radarFragment);
            //fragmentTransaction.remove(radarFragment);
        }

        /** Detaches the applefragment if exists */
        if(listFragment!=null) {
            fragmentTransaction.detach(listFragment);
            //fragmentTransaction.remove(listFragment);
        }

        if(tabIds.equalsIgnoreCase("radar")){ /** If current tab is Info */
            /** Create AndroidFragment and adding to fragmenttransaction */
            GlobalVariables.afterViewsRadar = true;
            fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderRadarFragment_(), "radar");
            /** Bring to the front, if already exists in the fragmenttransaction */
        } else {    /** If current tab is Market */
            /** Create AppleFragment and adding to fragmenttransaction */
            fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderListFragment_(), "list");
            /** Bring to the front, if already exists in the fragmenttransaction */
        }

        fragmentTransaction.commit();

    }

    @AfterViews
    public void treatments(){
        tabhost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                tabIds = tabId;
                android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
                PeopleFinderRadarFragment_ radarFragment = (PeopleFinderRadarFragment_) fragmentManager.findFragmentByTag("radar");
                PeopleFinderListFragment_ listFragment = (PeopleFinderListFragment_) fragmentManager.findFragmentByTag("list");
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(radarFragment!=null) {
                    fragmentTransaction.detach(radarFragment);
                    //fragmentTransaction.remove(radarFragment);
                }

                /** Detaches the applefragment if exists */
                if(listFragment!=null) {
                    fragmentTransaction.detach(listFragment);
                    //fragmentTransaction.remove(listFragment);
                }

                if(tabId.equalsIgnoreCase("radar")){ /** If current tab is Info */
                    /** Create AndroidFragment and adding to fragmenttransaction */
                    GlobalVariables.afterViewsRadar = true;
                    fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderRadarFragment_(), "radar");

                    /** Bring to the front, if already exists in the fragmenttransaction */
                } else {    /** If current tab is Market */
                    /** Create AppleFragment and adding to fragmenttransaction */
                    fragmentTransaction.replace(R.id.realtabcontent, new PeopleFinderListFragment_(), "list");
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
        tSpecApple.setIndicator("List", getResources().getDrawable(R.drawable.delete));
        tSpecApple.setContent(new DummyTabContent(getActivity().getBaseContext()));
        tabhost.addTab(tSpecApple);
    }
}