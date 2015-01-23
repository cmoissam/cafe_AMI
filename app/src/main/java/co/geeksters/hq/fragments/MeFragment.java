package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.CustomOnItemSelectedListener;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Social;
import co.geeksters.hq.services.MemberService;
import static co.geeksters.hq.global.helpers.GeneralHelpers.formatActualDate;
import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterestToEdit;
import static co.geeksters.hq.global.helpers.ViewHelpers.deleteTextAndSetHint;
import static co.geeksters.hq.global.helpers.ViewHelpers.showProgress;

@EFragment(R.layout.fragment_me)
public class MeFragment extends Fragment {

    @ViewById(R.id.fullName)
    EditText fullName;

    /*@ViewById(R.id.hubName)
    EditText hubName;*/

    @ViewById(R.id.spinner)
    Spinner spinner;

    @ViewById(R.id.companyName)
    EditText companyName;

    @ViewById(R.id.goalContent)
    EditText goalContent;

    @ViewById(R.id.bioContent)
    EditText bioContent;

    @ViewById(R.id.meScrollView)
    ScrollView meScrollView;

    @ViewById(R.id.logoutProgress)
    ProgressBar logoutProgress;

    @ViewById(R.id.contact)
    TextView contact;

    @ViewById(R.id.linkdin)
    EditText linkdin;

    @ViewById(R.id.twitter)
    EditText twitter;

    @ViewById(R.id.facebook)
    EditText facebook;

    @ViewById(R.id.skype)
    EditText skype;

    @ViewById(R.id.blog)
    EditText blog;

    @ViewById(R.id.website)
    EditText website;

    @ViewById(R.id.interest)
    EditText interest;

    @ViewById(R.id.addButtonInterest)
    ImageView addButtonInterest;

    @ViewById(R.id.interestsContent)
    LinearLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    @ViewById(R.id.checkBoxEmailComment)
    CheckBox checkBoxEmailComment;

    @ViewById(R.id.checkBoxPushComment)
    CheckBox checkBoxPushComment;

    @ViewById(R.id.checkBoxEmailTodo)
    CheckBox checkBoxEmailTodo;

    @ViewById(R.id.checkBoxPushTodo)
    CheckBox checkBoxPushTodo;

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Member currentMember;
    String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        layoutInflater = inflater;

        return null;
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
    }

    @AfterViews
    void initFieldsFromCurrentMemberInformation() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        fullName.setText(GeneralHelpers.firstToUpper(currentMember.fullName));
//        hubName.setText(GeneralHelpers.firstToUpper(currentMember.hub.name));
        companyName.setText(currentMember.returnNameForNullCompaniesValue());
        goalContent.setText(GeneralHelpers.firstToUpper(currentMember.goal));
        bioContent.setText(GeneralHelpers.firstToUpper(currentMember.blurp));

        linkdin.setText(currentMember.social.linkedin);
        twitter.setText(currentMember.social.twitter);
        facebook.setText(currentMember.social.facebook);
        skype.setText(currentMember.social.skype);
        blog.setText(currentMember.social.blog);
        website.setText(currentMember.social.website);

        currentMember.interests = new ArrayList<Interest>();
        Interest interest1 = new Interest();
        interest1.name = "Developement test";
        Interest interest2 = new Interest();
        interest2.name = "WEB";
        Interest interest3 = new Interest();
        interest3.name = "Finance";
        Interest interest4 = new Interest();
        interest4.name = "Law";
        currentMember.interests.add(interest1);
        currentMember.interests.add(interest2);
        currentMember.interests.add(interest3);
        currentMember.interests.add(interest4);

        interest.setText(currentMember.returnNameForNullInterestsValue(0));
        for(int i = 1; i < currentMember.interests.size(); i++)
            createViewInterestToEdit(getActivity(), layoutInflater, interestsContent, currentMember.interests.get(i).name);

        if(currentMember.notifyByEmailOnComment)
            checkBoxEmailComment.setChecked(true);
        if(currentMember.notifyByEmailOnTodo)
            checkBoxEmailTodo.setChecked(true);
        if(currentMember.notifyByPushOnComment)
            checkBoxPushComment.setChecked(true);
        if(currentMember.notifyByPushOnTodo)
            checkBoxPushTodo.setChecked(true);

//        addItemsOnSpinner();
//        addListenerOnButton();
//        addListenerOnSpinnerItemSelection();
    }

    public void addItemsOnSpinner() {
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    // add items into spinner dynamically
    /*public void addItemsOnSpinner() {
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }*/

    @Click(R.id.saveButton)
    public void save(){
        showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            Member updatedMember = createMemberFromFields();
            memberService.updateMember(currentMember.id, updatedMember);

            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 5;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }

        showProgress(false, getActivity(), meScrollView, logoutProgress);
    }

    @Subscribe
    public void onSaveMemberEvent(SaveMemberEvent event) {
        if(GlobalVariables.MENU_POSITION == 5)
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.alert_save), Toast.LENGTH_LONG).show();

        // save the current Member
        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
        editor.commit();
    }

    @Click(R.id.deleteButton)
    public void deleteAccount(){
        showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.deleteMember(currentMember.id);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }

        showProgress(false, getActivity(), meScrollView, logoutProgress);
    }

    @Click(R.id.logoutButton)
    public void logout(){
        showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.logout();
        } else {
            showProgress(false, getActivity(), meScrollView, logoutProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @FocusChange(R.id.bioContent)
    public void scrollToTop(){
        meScrollView.post(new Runnable() {
            @Override
            public void run() {
                meScrollView.scrollTo(0, bioContent.getTop());
            }
        });
    }

    @Click(R.id.deleteButtonLinkdin)
    public void deleteLinkdinLink(){
        deleteTextAndSetHint(linkdin, "linkdin");
    }

    @Click(R.id.deleteButtonTwitter)
    public void deleteTwitterLink(){
        deleteTextAndSetHint(twitter, "twitter");
    }

    @Click(R.id.deleteButtonFacebook)
    public void deleteFacebookLink(){
        deleteTextAndSetHint(facebook, "facebook");
    }

    @Click(R.id.deleteButtonSkype)
    public void deleteSkypeLink(){
        deleteTextAndSetHint(skype, "skype");
    }

    @Click(R.id.deleteButtonBlog)
    public void deleteBlogLink(){
        deleteTextAndSetHint(blog, "blog");
    }

    @Click(R.id.deleteButtonWebsite)
    public void deleteWebsiteLink(){
        deleteTextAndSetHint(website, "website");
    }

    @TextChange(R.id.interest)
    public void setVisibilityAddInterest(){
        if(interest.getText().length() > 0){
            addButtonInterest.setFocusable(true);
        }
    }

    @Click(R.id.addButtonInterest)
    public void addInterestField(){
        if(interest.getText().length() > 0) {
            createViewInterestToEdit(getActivity(), layoutInflater, interestsContent, interest.getText().toString());
            interest.setText("");
        }
    }

    public Member createMemberFromFields(){
        Member member = currentMember;
        //member.email = currentMember.email;

        member.updatedAt = formatActualDate();

        member.fullName = fullName.getText().toString();
        member.hub.name = String.valueOf(spinner.getSelectedItem());

        if(!companyName.getText().toString().equals("")) {
            member.companies = new ArrayList<Company>();
            Company company = new Company();
            company.name = companyName.getText().toString();
            member.companies.add(company);
        }

        member.goal = goalContent.getText().toString();
        member.blurp = bioContent.getText().toString();

        member.social = new Social(currentMember.social.id);
        member.social.linkedin = linkdin.getText().toString();
        member.social.facebook = facebook.getText().toString();
        member.social.twitter = twitter.getText().toString();
        member.social.blog = blog.getText().toString();
        member.social.skype = skype.getText().toString();
        member.social.website = website.getText().toString();

        if(!((EditText) (interestsContent.getChildAt(0)).findViewById(R.id.interest)).getText().toString().equals("")) {
            member.interests = new ArrayList<Interest>();
            for (int i = 0; i < interestsContent.getChildCount(); i++) {
                Interest interest = new Interest();
                interest.name = ((EditText) (interestsContent.getChildAt(i)).findViewById(R.id.interest)).getText().toString();
                member.interests.add(interest);
            }
        }

        member.notifyByEmailOnComment = checkBoxEmailComment.isChecked();
        member.notifyByEmailOnTodo = checkBoxEmailTodo.isChecked();
        member.notifyByPushOnComment = checkBoxPushComment.isChecked();
        member.notifyByPushOnTodo = checkBoxPushTodo.isChecked();

        return member;
    }
}
