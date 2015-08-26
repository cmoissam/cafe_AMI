package co.geeksters.hq.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.PredicateLayout;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterest;

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
    PredicateLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    @ViewById(R.id.interests)
    TextView interestsTitle;

    @ViewById(R.id.contactLinkdin)
    LinearLayout contactLinkdin;

    @ViewById(R.id.editImage)
    ImageView editInfo;

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
        } else {
            editInfo.setVisibility(View.GONE);
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("profile_member", "")));
        }

        companyName.setText(memberToDisplay.returnNameForNullCompaniesValue());
        goalContent.setText(memberToDisplay.goal);
        bioContent.setText(memberToDisplay.blurp);

        if(memberToDisplay.interests.size() != 0)
            interestsTitle.setVisibility(View.VISIBLE);

        for(int i = 0; i < memberToDisplay.interests.size(); i++)
            createViewInterest(getActivity(), layoutInflater, interestsContent, memberToDisplay.interests.get(i).name);
    }

    @Click(R.id.contactLinkdin)
    public void openLinkdinLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social == null || memberToDisplay.social.linkedin.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                // get the LinkedIn app if possible
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
                final PackageManager packageManager = getActivity().getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (list.isEmpty()) {
                    if (!memberToDisplay.social.linkedin.startsWith("https://") && !memberToDisplay.social.linkedin.startsWith("http://"))
                        memberToDisplay.social.linkedin = "https://" + memberToDisplay.social.linkedin;

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.linkedin));
                }

                startActivity(intent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactFacebook)
    public void openFacebookLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social == null || memberToDisplay.social.facebook.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                if (!memberToDisplay.social.facebook.startsWith("https://") && !memberToDisplay.social.facebook.startsWith("http://"))
                    memberToDisplay.social.facebook = "https://" + memberToDisplay.social.facebook;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.facebook));
                startActivity(browserIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactTwitter)
    public void openTwitterLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social == null || memberToDisplay.social.twitter.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+memberToDisplay.social.twitter.split("@")[0]));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    if (!memberToDisplay.social.twitter.startsWith("https://") && !memberToDisplay.social.twitter.startsWith("http://"))
                        memberToDisplay.social.twitter = "https://twitter.com/" + memberToDisplay.social.twitter.split("@")[0];

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.twitter));
                }
                startActivity(intent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactWebsite)
    public void openWebsiteLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social == null || memberToDisplay.social.website.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.website));
                startActivity(browserIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactSkype)
    public void openSkypeLink() {
        if(memberToDisplay.social == null || GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social.skype.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Uri skypeUri = Uri.parse("skype:" + memberToDisplay.social.skype + "?chat");
                Intent skypeIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
                skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
                skypeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(skypeIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactBlog)
    public void openBlogLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social == null || memberToDisplay.social.blog.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.blog));
                startActivity(browserIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactMail)
    public void openMailLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",memberToDisplay.email, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.action_send_mail_object));
            startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.action_send_mail_title)));
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactPhone)
    public void openPhoneLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.phone.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + memberToDisplay.phone));
                startActivity(Intent.createChooser(phoneIntent, getResources().getString(R.string.action_send_mail_title)));
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.editImage)
    public void editProfile(){
        //SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        GlobalVariables.editMyInformation = true;
        GlobalVariables.isMenuOnPosition = false;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());
        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Click(R.id.logoutButton)
    public void logout(){
        //showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.logout();
        } else {
            //showProgress(false, getActivity(), meScrollView, logoutProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }
}