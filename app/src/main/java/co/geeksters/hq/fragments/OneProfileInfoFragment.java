package co.geeksters.hq.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.PredicateLayout;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelper.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterest;
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
    //Member currentMember;
    Member memberToDisplay;
    String accessToken;

    //private static final String NEW_INSTANCE_MEMBER_SEE_PROFILE_KEY = "see_profile_member_key";
    //static Boolean seeProfile = false;

    /*public static OneProfileInfoFragment_ newInstance(Member member) {
        seeProfile = true;

        OneProfileInfoFragment_ fragment = new OneProfileInfoFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBER_SEE_PROFILE_KEY, member);
        fragment.setArguments(bundle);

        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*Serializable var = getArguments().getSerializable(NEW_INSTANCE_MEMBER_SEE_PROFILE_KEY);

        if(var != null)
            profileMember = (Member) getArguments().getSerializable(NEW_INSTANCE_MEMBER_SEE_PROFILE_KEY);*/

        layoutInflater = inflater;

        return null;
    }

    @AfterViews
    void initFieldsFromCurrentMemberInformation() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");

        if(GlobalVariables.isCurrentMember) {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        } else{
            editInfo.setVisibility(View.GONE);
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("profile_member", "")));
        }



        companyName.setText(memberToDisplay.returnNameForNullCompaniesValue());
        goalContent.setText(memberToDisplay.goal);
        bioContent.setText(memberToDisplay.blurp);

        if(memberToDisplay.interests == null) {
            memberToDisplay.interests = new ArrayList<Interest>();
            Interest interest1 = new Interest();
            interest1.name = "Developement test";
            Interest interest2 = new Interest();
            interest2.name = "WEB";
            Interest interest3 = new Interest();
            interest3.name = "Finance";
            Interest interest4 = new Interest();
            interest4.name = "Law";
            memberToDisplay.interests.add(interest1);
            memberToDisplay.interests.add(interest2);
            memberToDisplay.interests.add(interest3);
            memberToDisplay.interests.add(interest4);
        }

        if(memberToDisplay.interests.size() != 0)
            interestsTitle.setVisibility(View.VISIBLE);

        for(int i = 0; i < memberToDisplay.interests.size(); i++)
            createViewInterest(getActivity(), layoutInflater, interestsContent, memberToDisplay.interests.get(i).name);
    }

    @Click(R.id.contactLinkdin)
    public void openLinkdinLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social.linkedin.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                if (!memberToDisplay.social.linkedin.startsWith("https://"))
                    memberToDisplay.social.linkedin = "https://" + memberToDisplay.social.linkedin;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.linkedin));
                startActivity(browserIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactFacebook)
    public void openFacebookLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social.facebook.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                if (!memberToDisplay.social.facebook.startsWith("https://"))
                    memberToDisplay.social.facebook = "https://" + memberToDisplay.social.facebook;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.facebook));
                startActivity(browserIntent);

                //"http://facebook.com/darienjay100"
                //"https://www.facebook.com/alexmsimon"
                // https://www.facebook.com/soukaina.mjahed

                /*final Session session = Session.getActiveSession();
                makeMeRequest(session);*/

                /*if (session != null && session.isOpened()) {
                    // If the session is open, make an API call to get user data
                    // and define a new callback to handle the response
                    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            // If the response is successful
                            if (session == Session.getActiveSession()) {
                                if (user != null) {
                                    String user_ID = user.getId();//user id
                                    String profileName = user.getName();//user's profile name
                                }
                            }
                        }
                    });
                    Request.executeBatchAsync(request);
                }*/


                /*if (GlobalVariables.facebook.getAccessToken() != null) {
                    JSONObject userInfo = null;
                    try {
                        userInfo = new JSONObject(GlobalVariables.facebook.request("https://graph.facebook.com/soukaina.mjahed"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        String id = userInfo.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String url = "fb://page/" + "20531316728";
                    Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    startActivity(facebookAppIntent);
                } else {

                    if (!GlobalVariables.facebook.isSessionValid()) {
                        GlobalVariables.facebook.authorize(getActivity(), new String[]{},
                                new Facebook.DialogListener() {

                                    @Override
                                    public void onCancel() {
                                        // Function to handle cancel event
                                        JSONObject userInfo = null;
                                        try {
                                            userInfo = new JSONObject(GlobalVariables.facebook.request("https://graph.facebook.com/soukaina.mjahed"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            String id = userInfo.getString("id");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        final String url = "fb://page/" + "20531316728";
                                        Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                        startActivity(facebookAppIntent);
                                    }

                                    @Override
                                    public void onComplete(Bundle values) {
                                        JSONObject userInfo = null;
                                        try {
                                            userInfo = new JSONObject(GlobalVariables.facebook.request("https://graph.facebook.com/soukaina.mjahed"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            String id = userInfo.getString("id");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        final String url = "fb://page/" + "20531316728";
                                        Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                        startActivity(facebookAppIntent);
                                    }

                                    @Override
                                    public void onError(DialogError error) {
                                        // Function to handle error
                                    }

                                    @Override
                                    public void onFacebookError(FacebookError fberror) {
                                        // Function to handle Facebook errors
                                        //pwindo.dismiss();
                                        //Toast.makeText(c, "Facebook Error", Toast.LENGTH_LONG).show();
                                    }

                                });
                    }
                }*/
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.

        Log.i("++++++++AuthenticatedFragment", "++++++MakeRequest");

        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    public void onCompleted(GraphUser user,com.facebook.Response response) {
                        Log.i("*****************AuthenticatedFragment:","onCompleted**********");
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {

                                String token = session.getAccessToken();
                                // fbUser = user;
                                Log.i("HOME XXXXXXXXXXXXXXXXXXX", "" + user.getName());

                                Object[] media = {"facebook",token};
                                //params_facebook =  media;
                                //editor.putString("provider", "facebook");
                                //editor.commit();
                                //connect_facebook();
                            }
                        }
                        if (response.getError() != null) {
                            FacebookRequestError error1 = response.getError();
                            FacebookRequestError error2 = response.getError();
                        }
                    }
                });
        request.executeAsync();
    }

    @Click(R.id.contactTwitter)
    public void openTwitterLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social.twitter.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                if (!memberToDisplay.social.twitter.startsWith("https://"))
                    memberToDisplay.social.twitter = "https://twitter.com/" + memberToDisplay.social.twitter.split("@")[1];

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.twitter));
                startActivity(browserIntent);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Click(R.id.contactWebsite)
    public void openWebsiteLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            if(memberToDisplay.social.website.equals(""))
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
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
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
            if(memberToDisplay.social.blog.equals(""))
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.empty_field));
            else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.website));
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
