package co.geeksters.cafe_ami.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.PredicateLayout;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.MemberService;

import static co.geeksters.cafe_ami.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.cafe_ami.global.helpers.ViewHelpers.createViewInterest;

@EFragment(R.layout.fragment_one_profile_info)
public class OneProfileInfoFragment extends Fragment {

    @ViewById(R.id.organisationContent)
    TextView organisationContent;

    @ViewById(R.id.organisationTitle)
    TextView organisationTitle;

    @ViewById(R.id.missionContent)
    TextView missionContent;

    @ViewById(R.id.missionTitle)
    TextView missionTitle;

    @ViewById(R.id.shortBioContent)
    TextView shortBioContent;

    @ViewById(R.id.shortBioTitle)
    TextView shortBioTitle;

    @ViewById(R.id.logoutButton)
    Button logoutButton;

    @ViewById(R.id.checkBoxRadarVisibility)
    CheckBox checkBoxRadarVisibility;

    @ViewById(R.id.checkBoxEmailComment)
    CheckBox checkBoxEmailComment;

    @ViewById(R.id.checkBoxPushComment)
    CheckBox checkBoxPushComment;

    @ViewById(R.id.checkBoxEmailTodo)
    CheckBox checkBoxEmailTodo;

    @ViewById(R.id.checkBoxPushTodo)
    CheckBox checkBoxPushTodo;

       @ViewById(R.id.interestsContent)
    PredicateLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    @ViewById(R.id.notification_block)
    LinearLayout notificationBLock;

    @ViewById(R.id.interestsTitle)
    TextView interestsTitle;
    @ViewById(R.id.logout_layout)
    RelativeLayout logoutLayout;

    @ViewById(R.id.edit_layout)
    RelativeLayout editLayout;

    @ViewById(R.id.edit_my_profile)
    Button editMyProfile;

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    Member memberToDisplay;
    String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;

        return null;
    }

    @AfterViews
    void initFieldsFromCurrentMemberInformation() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");

        if(GlobalVariables.isCurrentMember) {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

            if(memberToDisplay.radarVisibility)
                checkBoxRadarVisibility.setChecked(true);
            if(memberToDisplay.notifyByEmailOnComment)
                checkBoxEmailComment.setChecked(true);
            if(memberToDisplay.notifyByEmailOnTodo)
                checkBoxEmailTodo.setChecked(true);
            if(memberToDisplay.notifyByPushOnComment)
                checkBoxPushComment.setChecked(true);
            if(memberToDisplay.notifyByPushOnTodo)
                checkBoxPushTodo.setChecked(true);
        } else {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("profile_member", "")));
            logoutLayout.setVisibility(View.GONE);
            notificationBLock.setVisibility(View.GONE);
            editLayout.setVisibility(View.GONE);
        }

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        missionContent.setTypeface(typeFace);
        missionTitle.setTypeface(typeFace);
        organisationContent.setTypeface(typeFace);
        organisationTitle.setTypeface(typeFace);
        shortBioContent.setTypeface(typeFace);
        shortBioTitle.setTypeface(typeFace);
        interestsTitle.setTypeface(typeFace);
        logoutButton.setTypeface(null, typeFace.BOLD);

        organisationContent.setText(memberToDisplay.returnNameForNullCompaniesValue());
        missionContent.setText(memberToDisplay.goal);
        shortBioContent.setText(memberToDisplay.blurp);

        if(memberToDisplay.interests.size() != 0)
            interestsTitle.setVisibility(View.VISIBLE);

        for(int i = 0; i < memberToDisplay.interests.size(); i++)
            createViewInterest(getActivity(), layoutInflater, interestsContent, memberToDisplay.interests.get(i).name);
    }

    @Click(R.id.logoutButton)
    public void logout(){
        //showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            memberToDisplay.deviceToken = " ";
            memberToDisplay.deviceType = " ";
            MemberService memberService = new MemberService(accessToken);
            memberService.updateMember(memberToDisplay.id, memberToDisplay);
        } else {
            //showProgress(false, getActivity(), meScrollView, logoutProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.edit_my_profile)
    public void editMyProfile(){
        //showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        GlobalVariables.MENU_POSITION = 10;
        GlobalVariables.isMenuOnPosition = false;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
        fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }


}