package co.geeksters.hq.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.models.Member;
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
        GlobalVariables.menuPart = 5;
        GlobalVariables.menuDeep = 0;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

        return null;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
             GlobalVariables.inMarketPlaceFragment = true;
        GlobalVariables.needReturnButton = false;
            ((GlobalMenuActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_market_place));
        }

    @AfterViews
    public void tabSetting(){

        if(PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).getBoolean("visit_info_market_place",true)) {


        PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).edit().putBoolean("visit_info_market_place", false).commit();
        LayoutInflater inflater = GlobalVariables.activity.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.pop_up_info_opportunity, null);

        ImageView cancelImage = (ImageView) dialoglayout.findViewById(R.id.cancel_popup);
        TextView infoText = (TextView) dialoglayout.findViewById(R.id.popup_info_text);

        infoText.setText("Hey! This is the Opportunity space in the 1000N App. This is where you will post your requests to the community. This is where you can ask for help and offer help.");

        Typeface typeFace = Typeface.createFromAsset(GlobalVariables.activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        infoText.setTypeface(typeFace);

        AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVariables.activity);
        builder.setView(dialoglayout);
        builder.setCancelable(true);
        final AlertDialog ald = builder.show();
        ald.setCancelable(true);

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ald.dismiss();
            }
        });

        }


        GlobalVariables.notifiyedByInterestsOnPost = false;
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        GlobalVariables.inMarketPlaceFragment = false;

    }
}