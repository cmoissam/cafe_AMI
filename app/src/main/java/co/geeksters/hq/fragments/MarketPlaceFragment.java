package co.geeksters.hq.fragments;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import co.geeksters.hq.R;
import co.geeksters.hq.activities.DummyTabContent;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
@EFragment(R.layout.fragment_market_place)
public class MarketPlaceFragment extends Fragment {
    private static final String NEW_INSTANCE_MEMBER_KEY = "post_key";
    private static final String DEFAULT_INDEX_KEY = "index_key";
    public static int defaultIndex = 0;
    @ViewById(R.id.me_Button)
    Button meButton;
    @ViewById(R.id.all_Button)
    Button allButton;
    @ViewById(R.id.me_buttonlight)
    LinearLayout meButtonLight;
    @ViewById(R.id.all_buttonlight)
    LinearLayout allButtonLight;
    public Boolean allSelected = false;
    public  Boolean meSelected = false;
    public static MarketPlaceFragment_ newInstance(Member member, int index) {
        MarketPlaceFragment_ fragment = new MarketPlaceFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBER_KEY, member);
        bundle.putSerializable(DEFAULT_INDEX_KEY, index);
        defaultIndex = index;
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalVariables.MENU_POSITION = 4;
        GlobalVariables.inMarketPlaceFragment = true;

        return null;
    }

    @AfterViews
    public void tabSetting(){

        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        AllMarketPlaceFragment_ allFragment = (AllMarketPlaceFragment_) fragmentManager.findFragmentByTag("all");
        MeMarketPlaceFragment_ meFragment = (MeMarketPlaceFragment_) fragmentManager.findFragmentByTag("me");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /** Detaches the androidfragment if exists */
        if(allFragment!=null) {
            fragmentTransaction.detach(allFragment);
        }
        /** Detaches the applefragment if exists */
        if(meFragment!=null) {
            fragmentTransaction.detach(meFragment);
        }
        fragmentTransaction.add(R.id.realtabcontent,new AllMarketPlaceFragment_(), "all");
        allSelected = true;
        meSelected = false;
        meButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
        allButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
        fragmentTransaction.commit();
    }

    @Click(R.id.me_Button)
    public void onMeSelected(){
        if(!meSelected)
            treatments("me");
    }
    @Click(R.id.all_Button)
    public void onAllSelected(){
        if (!allSelected)
            treatments("all");
    }


    public void treatments(String tabId) {
        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        AllMarketPlaceFragment_ allFragment = (AllMarketPlaceFragment_) fragmentManager.findFragmentByTag("all");
        MeMarketPlaceFragment_ meFragment = (MeMarketPlaceFragment_) fragmentManager.findFragmentByTag("me");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        /** Detaches the androidfragment if exists */
        if(allFragment!=null) {
            fragmentTransaction.detach(allFragment);
        }
        /** Detaches the applefragment if exists */
        if(meFragment!=null) {
            fragmentTransaction.detach(meFragment);
        }
        if(tabId.equalsIgnoreCase("all")){ /** If current tab is Info */
            /** Create AndroidFragment and adding to fragmenttransaction */
            fragmentTransaction.add(R.id.realtabcontent,new AllMarketPlaceFragment_(), "all");
            allSelected = true;
            meSelected = false;
            meButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
            allButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
            /** Bring to the front, if already exists in the fragmenttransaction */
        } else {   /** If current tab is Market */
            /** Create AppleFragment and adding to fragmenttransaction */
            fragmentTransaction.add(R.id.realtabcontent,new MeMarketPlaceFragment_(), "me");
            meSelected = true;
            allSelected = false;
            allButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
            meButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
            /** Bring to the front, if already exists in the fragmenttransaction */
        }
        fragmentTransaction.commit();
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        GlobalVariables.inMarketPlaceFragment = false;
    }
}