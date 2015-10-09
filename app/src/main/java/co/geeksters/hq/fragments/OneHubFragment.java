package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.activities.GlobalMenuActivity_;
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



    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.hubName)
    TextView hubName;

    @ViewById(R.id.headerOneHub)
    LinearLayout headerHub;

    @ViewById(R.id.ambassadors_layout_list)
    LinearLayout ambassadorsLayout;

    @ViewById(R.id.thousand)
    TextView thousand;

    @ViewById(R.id.picture1)
    ImageView picture1;
    @ViewById(R.id.picture2)
    ImageView picture2;
    @ViewById(R.id.picture3)
    ImageView picture3;
    @ViewById(R.id.picture4)
    ImageView picture4;
    @ViewById(R.id.picture5)
    ImageView picture5;

    @ViewById(R.id.ambassadorsTitle)
    TextView ambassadorsTitle;

    @ViewById(R.id.news_Button)
    Button newsButton;
    public boolean  newsSelected = true;

    @ViewById(R.id.members_Button)
    Button membersButton;

    public boolean  membersSelected = false;

    @ViewById(R.id.Events_Button)
    Button eventsButton;

    public boolean  eventsSelected = false;

    @ViewById(R.id.news_buttonlight)
    LinearLayout newsButtonLight;
    @ViewById(R.id.members_buttonlight)
    LinearLayout membersButtonLight;
    @ViewById(R.id.events_buttonlight)
    LinearLayout eventsButtonLight;

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
        accessToken = preferences.getString("access_token","").replace("\"", "");
        GlobalVariables.menuDeep = 1;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
        BaseApplication.register(this);
        return null;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        ((GlobalMenuActivity)getActivity()).setActionBarTitle("HUB");
    }

    public void listAllAmbassadorsOfHubService() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            HubService hubService = new HubService(accessToken);
            hubService.getHubAmbassadors(hubToDisplay.id);
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listAllAmbassadorsByPagination() {

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        thousand.setTypeface(null, typeFace.BOLD);
        hubName.setTypeface(typeFace);
        ambassadorsTitle.setTypeface(typeFace);
        eventsButton.setTypeface(typeFace);
        membersButton.setTypeface(typeFace);
        newsButton.setTypeface(typeFace);


        headerHub.getBackground().setAlpha(100);
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

        fragmentTransaction.add(R.id.realtabcontent,new OneHubNewsFragment_(), "news");
        eventsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
        membersButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
        newsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
        membersSelected = false;
        eventsSelected = false;
        newsSelected = true;
        fragmentTransaction.commit();

        listAllAmbassadorsOfHubService();
    }

    @Subscribe
    public void onGetListAmbassadorsOfHubEvent(AmbassadorsEvent event) {

        hubName.setText(hubToDisplay.name);

        hubToDisplay.ambassadors = new ArrayList<Member>();
        hubToDisplay.ambassadors = event.members;

        if(hubToDisplay.ambassadors.size() != 0) {
            ambassadorsTitle.setVisibility(View.VISIBLE);
            makeList(hubToDisplay.ambassadors,hubToDisplay);
        }

        //TODO : set background hub image layout with the correspondant image


    }

    public void makeList(final List<Member> ambassadors , final Hub hub) {

        if(ambassadors.size() >= 1)
        {
            picture1.setVisibility(View.VISIBLE);
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture1, ambassadors.get(0).image);
            picture1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(0).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(0), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 2)
        {
            picture2.setVisibility(View.VISIBLE);
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture2, ambassadors.get(1).image);
            picture2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(1).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(1), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 3)
        {
            picture3.setVisibility(View.VISIBLE);
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture3, ambassadors.get(2).image);
            picture3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(3).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(3), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 4)
        {
            picture4.setVisibility(View.VISIBLE);
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture4, ambassadors.get(3).image);
            picture4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(4).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(4), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 5)
        {
            picture5.setVisibility(View.VISIBLE);
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture5, ambassadors.get(4).image);
            picture5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(5).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(5), 0);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }


    }

    @Click(R.id.news_Button)
    public void onNewsSelect(){
        if(!newsSelected)
        treatments("news");

    }

    @Click(R.id.members_Button)
    public void onMembersSelect(){
        if(!membersSelected)
        treatments("members");

    }

    @Click(R.id.Events_Button)
    public void onEventsSelect(){
        if (!eventsSelected)
        treatments("events");

    }
    public void treatments(String tabId) {

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
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
                eventsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                membersButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                newsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
                membersSelected = false;
                eventsSelected = false;
                newsSelected = true;
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else if(tabId.equalsIgnoreCase("events")){ /** If current tab is Info */
                /** Create AndroidFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneHubEventsFragment_(), "events");
                eventsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
                membersButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                newsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                membersSelected = false;
                eventsSelected = true;
                newsSelected = false;
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else {	/** If current tab is Market */
                /** Create AppleFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneHubMembersFragment_().newInstance(hubToDisplay), "members");
                eventsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                membersButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
                newsButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_2_no_selected_562x188));
                membersSelected = true;
                eventsSelected = false;
                newsSelected = false;
                /** Bring to the front, if already exists in the fragmenttransaction */
            }
            fragmentTransaction.commit();
    }
}
