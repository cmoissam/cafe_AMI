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

@EFragment(R.layout.fragment_one_profile)
public class OneProfileFragment extends Fragment {
    @ViewById(R.id.oneProfileScrollView)
    ScrollView oneProfileScrollView;

    @ViewById(R.id.tabhost)
    TabHost tabhost;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.fullName)
    TextView fullName;

    @ViewById(R.id.hubName)
    TextView hubName;

    @ViewById(R.id.picture)
    ImageView picture;

    @ViewById(R.id.personalInformation)
    LinearLayout personalInformation;

    private static final String NEW_INSTANCE_MEMBER_KEY = "member_key";
    private static final String DEFAULT_INDEX_KEY = "index_key";
    SharedPreferences preferences;
    Member memberToDisplay;
    Member profileMember;
    int defaultIndex = 0;

    public static OneProfileFragment_ newInstance(Member member, int defaultIndex) {
        OneProfileFragment_ fragment = new OneProfileFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBER_KEY, member);
        bundle.putSerializable(DEFAULT_INDEX_KEY, defaultIndex);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null) {
            profileMember = (Member) getArguments().getSerializable(NEW_INSTANCE_MEMBER_KEY);
            defaultIndex = (Integer) getArguments().getSerializable(DEFAULT_INDEX_KEY);
        }

        GlobalVariables.MENU_POSITION = 5;

        return null;
    }

    @AfterViews
    public void scrollFragmentToTop(){
        oneProfileScrollView.post(new Runnable() {
            @Override
            public void run() {
                oneProfileScrollView.scrollTo(0, personalInformation.getTop());
            }
        });
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
            editor.putString("profile_member", ParseHelpers.createJsonStringFromModel(profileMember));
            editor.commit();
        }


        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, memberToDisplay.image);

        fullName.setText(memberToDisplay.fullName);

        if(memberToDisplay.hub == null || memberToDisplay.hub.name == null)
            hubName.setText(getResources().getString(R.string.empty_hub_name));
        else
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
        }};

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

        tabhost.setCurrentTab(defaultIndex);
    }
}
