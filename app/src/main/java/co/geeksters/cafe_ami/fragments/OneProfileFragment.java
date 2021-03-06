package co.geeksters.cafe_ami.fragments;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ParseHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_one_profile)
public class OneProfileFragment extends Fragment {


    @ViewById(R.id.fullName)
    TextView fullName;

    @ViewById(R.id.hubName)
    TextView hubName;

    @ViewById(R.id.picture)
    ImageView picture;

    @ViewById(R.id.facebook_Button)
    ImageView facebookButton;

    @ViewById(R.id.twitter_Button)
    ImageView twitterButton;

    @ViewById(R.id.blog_Button)
    ImageView blogButton;

    @ViewById(R.id.phone_Button)
    ImageView phoneButton;

    @ViewById(R.id.whatsapp_button)
    ImageView whatsappButton;

    @ViewById(R.id.web_Button)
    ImageView webButton;

    @ViewById(R.id.linkedin_Button)
    ImageView linkedButton;

    @ViewById(R.id.skype_Button)
    ImageView skypeButton;

    @ViewById(R.id.horiz_scroll_view)
    HorizontalScrollView horizScrollView;

    @ViewById(R.id.rows_button)
    ImageView rowsButton;

    @ViewById(R.id.innerLay)
    LinearLayout innerLay;

    @ViewById(R.id.list_Button)
    Button listButton;

    public boolean  listSelected = false;

    public String accessToken;

    @ViewById(R.id.marketplace_Button)
    Button marketplaceButton;

    public boolean  marketplaceSelected = false;

    @ViewById(R.id.marketplace_buttonlight)
    LinearLayout marketplaceButtonLight;

    @ViewById(R.id.list_buttonlight)
    LinearLayout listButtonLight;

    float d;



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

        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").toString().replace("\"", "");

            Member member = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        if(profileMember == null || profileMember.id == member.id) {
            if(profileMember != null)
            {
                GlobalVariables.needReturnButton = true;
                if(GlobalVariables.menuPart == 1)
                    GlobalVariables.menuDeep = 1;
                if(GlobalVariables.menuPart == 2)
                    GlobalVariables.menuDeep = 1;
                if(GlobalVariables.menuPart == 3)
                    GlobalVariables.menuDeep = 2;
                if(GlobalVariables.menuPart == 4)
                    GlobalVariables.menuDeep = 1;

            }
            else {
                GlobalVariables.needReturnButton = false;
                GlobalVariables.menuPart = 6;
                GlobalVariables.menuDeep = 0;
            }
            GlobalVariables.inRadarFragement = false;
            GlobalVariables.inMyTodosFragment = false;
            GlobalVariables.inMarketPlaceFragment = false;
            GlobalVariables.inMyProfileFragment = true;
                    ((GlobalMenuActivity) getActivity()).setActionBarTitle("MY PROFILE");

        }
        else{
            GlobalVariables.inRadarFragement = false;
            GlobalVariables.inMyProfileFragment = false;
            GlobalVariables.inMyTodosFragment = false;
            GlobalVariables.inMarketPlaceFragment = false;
            GlobalVariables.needReturnButton = true;
            ((GlobalMenuActivity) getActivity()).setActionBarTitle("PROFILE");
            GlobalVariables.actualMember = profileMember;
            if(GlobalVariables.menuPart == 1)
                GlobalVariables.menuDeep = 1;
            if(GlobalVariables.menuPart == 2)
                GlobalVariables.menuDeep = 1;
            if(GlobalVariables.menuPart == 3)
                GlobalVariables.menuDeep = 2;
            if(GlobalVariables.menuPart == 4)
                GlobalVariables.menuDeep = 1;
        }
        GlobalVariables.MENU_POSITION = 5;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
        return null;
    }
    @Override
    public void onDestroy(){

        super.onDestroy();

        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
    }

    @AfterViews
    public void setNameAndHub(){
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        if(profileMember == null) {
            memberToDisplay = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
            GlobalVariables.isCurrentMember = true;

            getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
        } else {
            memberToDisplay = profileMember;
            GlobalVariables.isCurrentMember = false;

            getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profile_member", ParseHelpers.createJsonStringFromModel(profileMember));
            editor.commit();
        }
        if(memberToDisplay.social !=null) {

            if (memberToDisplay.social.linkedin == null || memberToDisplay.social.linkedin.equals(""))
                linkedButton.setVisibility(View.GONE);
            if (memberToDisplay.social.facebook == null || memberToDisplay.social.facebook.equals(""))
                facebookButton.setVisibility(View.GONE);
            if (memberToDisplay.social.twitter == null || memberToDisplay.social.twitter.equals(""))
                twitterButton.setVisibility(View.GONE);

            if (memberToDisplay.phone == null || memberToDisplay.phone.equals(""))
                phoneButton.setVisibility(View.GONE);

            if (memberToDisplay.social.blog == null || memberToDisplay.social.blog.equals(""))
                blogButton.setVisibility(View.GONE);

            if (memberToDisplay.social.skype == null || memberToDisplay.social.skype.equals(""))
                skypeButton.setVisibility(View.GONE);

            if (memberToDisplay.social.website == null || memberToDisplay.social.website.equals(""))
                webButton.setVisibility(View.GONE);
            if(memberToDisplay.whatsapp == null || memberToDisplay.whatsapp.equals(""))
                whatsappButton.setVisibility(View.GONE);

        }
        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        fullName.setTypeface(null, typeFace.BOLD);
        hubName.setTypeface(typeFace);
        marketplaceButton.setTypeface(typeFace);
        listButton.setTypeface(typeFace);
        d = getActivity().getResources().getDisplayMetrics().density;

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                boolean isScrollable = horizScrollView.getWidth() < horizScrollView.getChildAt(0).getWidth();

                if(!isScrollable)
                {
                    rowsButton.setVisibility(View.GONE);
                }
                else{
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(horizScrollView.getWidth(),horizScrollView.getHeight());
                    int margin = (int)(60 * d);
                    params.setMargins(0,0,margin, 0);
                    horizScrollView.setLayoutParams(params);
                }

                rowsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                horizScrollView.smoothScrollTo(
                                        (int) horizScrollView.getScrollX()
                                                + horizScrollView.getWidth() / 2,
                                        (int) horizScrollView.getScrollY());
                            }
                        }, 100L);
                    }
                });

            }
        });

        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, memberToDisplay.image);

        fullName.setText(memberToDisplay.fullName);

        if(memberToDisplay.hub == null || memberToDisplay.hub.name == null)
            hubName.setText(getResources().getString(R.string.empty_hub_name));
        else
            hubName.setText(memberToDisplay.hub.name);

        android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
        OneProfileInfoFragment_ newsFragment = (OneProfileInfoFragment_) fragmentManager.findFragmentByTag("list");
        OneProfileMarketPlaceFragment_ eventsFragment = (OneProfileMarketPlaceFragment_) fragmentManager.findFragmentByTag("marketplace");
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(newsFragment!=null) {
            fragmentTransaction.detach(newsFragment);
        }

        if(eventsFragment!=null) {
            fragmentTransaction.detach(eventsFragment);
        }

        fragmentTransaction.add(R.id.realtabcontent,new OneProfileInfoFragment_(), "list");
        marketplaceButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
        listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
        marketplaceSelected = false;
        listSelected = true;
        fragmentTransaction.commit();
    }


    @Override
    public void onStart(){
        super.onStart();
     }



    @Click(R.id.list_Button)
    public void onListSelect(){
        if(!listSelected)
            treatments("list");

    }

    @Click(R.id.marketplace_Button)
    public void onMarketPlaceSelect(){
        if (!marketplaceSelected)
            treatments("marketplace");

    }


    public void treatments(String tabId) {


            android.support.v4.app.FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
            OneProfileInfoFragment_ infoFragment = (OneProfileInfoFragment_) fragmentManager.findFragmentByTag("list");
            OneProfileMarketPlaceFragment_ marketFragment = (OneProfileMarketPlaceFragment_) fragmentManager.findFragmentByTag("marketplace");
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            /** Detaches the androidfragment if exists */
            if(infoFragment!=null) {
                fragmentTransaction.detach(infoFragment);
            }

            /** Detaches the applefragment if exists */
            if(marketFragment!=null) {
                fragmentTransaction.detach(marketFragment);
            }

            if(tabId.equalsIgnoreCase("list")){ /** If current tab is Info */
                /** Create AndroidFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent, new OneProfileInfoFragment_(), "list");
                marketplaceButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
                marketplaceSelected = false;
                listSelected = true;
                /** Bring to the front, if already exists in the fragmenttransaction */
            } else {	/** If current tab is Market */
                /** Create AppleFragment and adding to fragmenttransaction */
                fragmentTransaction.add(R.id.realtabcontent,new OneProfileMarketPlaceFragment_(), "marketplace");
                listButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_nonselected_407x9));
                marketplaceButtonLight.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonline_selected_407x9));
                marketplaceSelected = true;
                listSelected = false;
                /** Bring to the front, if already exists in the fragmenttransaction */
            }
            fragmentTransaction.commit();
    }

    @Click(R.id.linkedin_Button)
    public void openLinkdinLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {


            if (!memberToDisplay.social.linkedin.startsWith("https://") && !memberToDisplay.social.linkedin.startsWith("http://"))
                memberToDisplay.social.linkedin = "https://" + memberToDisplay.social.linkedin;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.linkedin));
            startActivity(browserIntent);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.facebook_Button)
    public void openFacebookLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

                if (!memberToDisplay.social.facebook.startsWith("https://") && !memberToDisplay.social.facebook.startsWith("http://")) {
                    if(memberToDisplay.social.facebook.contains("facebook"))
                    memberToDisplay.social.facebook = "https://" + memberToDisplay.social.facebook;
                    else
                        memberToDisplay.social.facebook = "https://www.facebook.com/" + memberToDisplay.social.facebook;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.facebook));
                startActivity(browserIntent);

        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.twitter_Button)
    public void openTwitterLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    if(memberToDisplay.social.twitter.contains("@"))
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+memberToDisplay.social.twitter.split("@")[1]));
                    else
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+memberToDisplay.social.twitter));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    if (!memberToDisplay.social.twitter.startsWith("https://") && !memberToDisplay.social.twitter.startsWith("http://"))
                        if(memberToDisplay.social.twitter.contains("twitter"))
                            memberToDisplay.social.twitter = "https://"+memberToDisplay.social.twitter;
                        else
                        memberToDisplay.social.twitter = "https://twitter.com/" + memberToDisplay.social.twitter;

                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.twitter));
                }
                startActivity(intent);

        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.web_Button)
    public void openWebsiteLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.website));
                startActivity(browserIntent);

        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.skype_Button)
    public void openSkypeLink() {
            if(GeneralHelpers.isInternetAvailable(getActivity())) {
                Intent skypeIntent = null;

                try {
                    Uri skypeUri = Uri.parse("skype:" + memberToDisplay.social.skype + "?chat");
                    skypeIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
                    skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
                    skypeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(skypeIntent);
                }catch (ActivityNotFoundException e){
                    ViewHelpers.showPopup(getActivity(), "oh! sorry","install skype app please",false);
                }
                catch (Exception e){
                    ViewHelpers.showPopup(getActivity(), "oh! sorry","install skype app please",false);
                }
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.blog_Button)
    public void openBlogLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

            if (!memberToDisplay.social.blog.startsWith("https://") && !memberToDisplay.social.blog.startsWith("http://"))
                    memberToDisplay.social.blog = "https://"+memberToDisplay.social.blog;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(memberToDisplay.social.blog));
                startActivity(browserIntent);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @Click(R.id.phone_Button)
    public void openPhoneLink() {
        if(GeneralHelpers.isInternetAvailable(getActivity())) {

                Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + memberToDisplay.phone));
                startActivity(Intent.createChooser(phoneIntent, getResources().getString(R.string.action_send_mail_title)));

        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }
    @Click(R.id.whatsapp_button)
    public void openWhatsappLink() {

       if(GeneralHelpers.isInternetAvailable(getActivity())) {

            try {
                Uri uri = Uri.parse("smsto:" + memberToDisplay.whatsapp);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if(isWhatsappInstalled)
                startActivity(Intent.createChooser(i, ""));
                else
                ViewHelpers.showPopup(getActivity(), "oh! sorry","install Whatsapp please",false);
            }catch (ActivityNotFoundException e){
                ViewHelpers.showPopup(getActivity(), "oh! sorry","install Whatsapp please",false);
            }
            catch (Exception e){
                ViewHelpers.showPopup(getActivity(), "oh! sorry","install Whatsapp please",false);
            }
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    public boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
