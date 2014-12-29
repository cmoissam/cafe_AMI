package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Social;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.formatActualDate;
import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelper.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterest;
import static co.geeksters.hq.global.helpers.ViewHelpers.deleteTextAndSetHint;
import static co.geeksters.hq.global.helpers.ViewHelpers.showPopupOnNoNetworkConnection;
import static co.geeksters.hq.global.helpers.ViewHelpers.showProgress;

@EFragment(R.layout.fragment_one_profile_info)
public class OneProfileInfoFragment extends Fragment {

    @ViewById(R.id.companyName)
    TextView companyName;

    @ViewById(R.id.goalContent)
    TextView goalContent;

    @ViewById(R.id.bioContent)
    TextView bioContent;

    @ViewById(R.id.contact)
    TextView contact;

    @ViewById(R.id.linkdin)
    TextView linkdin;

    @ViewById(R.id.twitter)
    TextView twitter;

    @ViewById(R.id.facebook)
    TextView facebook;

    @ViewById(R.id.skype)
    TextView skype;

    @ViewById(R.id.blog)
    TextView blog;

    @ViewById(R.id.website)
    TextView website;

    @ViewById(R.id.interest)
    TextView interest;

    @ViewById(R.id.interestsContent)
    LinearLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    Member currentMember;
    String accessToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        return null;
    }

    @AfterViews
    void initFieldsFromCurrentMemberInformation() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        companyName.setText(currentMember.returnNameForNullCompaniesValue());
        goalContent.setText(currentMember.goal);
        bioContent.setText(currentMember.blurp);

        linkdin.setText(currentMember.social.linkedin);
        twitter.setText(currentMember.social.twitter);
        facebook.setText(currentMember.social.facebook);
        skype.setText(currentMember.social.skype);
        blog.setText(currentMember.social.blog);
        website.setText(currentMember.social.website);

        interest.setText(currentMember.returnNameForNullInterestsValue(0));
        for(int i = 1; i < currentMember.interests.size(); i++)
            createViewInterest(getActivity(), layoutInflater, interestsContent, currentMember.interests.get(i).name);
    }
}
