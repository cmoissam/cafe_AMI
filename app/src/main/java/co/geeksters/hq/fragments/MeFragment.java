package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.events.failure.InvalidFileFailureEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.events.success.UploadImageEvent;
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
import co.geeksters.hq.services.HubService;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.formatActualDate;
import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterestToEdit;
import static co.geeksters.hq.models.Hub.getHubsByAlphabeticalOrder;

@EFragment(R.layout.fragment_me)
public class MeFragment extends Fragment {

    Bitmap yourSelectedImage = null;

    @ViewById(R.id.fullName)
    EditText fullName;

    @ViewById(R.id.spinnerHubName)
    Spinner hubName;

    @ViewById(R.id.loadingGif)
    pl.droidsonroids.gif.GifImageView loading;

    @ViewById(R.id.companyName)
    EditText companyName;

    @ViewById(R.id.picture)
    ImageView picture;

    @ViewById(R.id.goalContent)
    EditText goalContent;

    @ViewById(R.id.bioContent)
    EditText bioContent;

    @ViewById(R.id.meScrollView)
    ScrollView meScrollView;

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

    @ViewById(R.id.phone)
    EditText phone;

    @ViewById(R.id.website)
    EditText website;

    @ViewById(R.id.interest)
    EditText interest;

    @ViewById(R.id.saveButton)
    Button saveButton;

    @ViewById(R.id.addButtonInterest)
    ImageView addButtonInterest;

    @ViewById(R.id.interestsContent)
    LinearLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

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

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Member currentMember;
    String accessToken;
    List<String> listItemHubSpinner = new ArrayList<String>();
    private static final int SELECT_PHOTO = 100;
    String urlPicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        GlobalVariables.MENU_POSITION = 6;
        layoutInflater = inflater;
        GlobalVariables.menuPart = 6;
        GlobalVariables.menuDeep = 1;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

        return null;
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.needReturnButton = true;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_me_fragment));
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

    public void listAllHubsService() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
            HubService hubService = new HubService(accessToken);
            hubService.listAllHubs();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    @AfterViews
    public void listAllHubs(){
        listAllHubsService();
    }

    @Subscribe
    public void onGetListHubsEvent(HubsEvent event) {
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        if(currentMember.hub.name == null || !currentMember.hub.name.equals(""))
            listItemHubSpinner.add(GeneralHelpers.firstToUpper(currentMember.hub.name));

        fullName.setText(GeneralHelpers.firstToUpper(currentMember.fullName));
        companyName.setText(currentMember.returnNameForNullCompaniesValue());
        goalContent.setText(GeneralHelpers.firstToUpper(currentMember.goal));
        bioContent.setText(GeneralHelpers.firstToUpper(currentMember.blurp));


        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, currentMember.image);


        linkdin.setText(currentMember.social.linkedin);
        twitter.setText(currentMember.social.twitter);
        facebook.setText(currentMember.social.facebook);
        skype.setText(currentMember.social.skype);
        blog.setText(currentMember.social.blog);
        website.setText(currentMember.social.website);
        phone.setText(currentMember.phone);

        if(currentMember.interests == null) {
            interest.setText("");
        }

        //interest.setText(currentMember.returnNameForNullInterestsValue(0));

        for(int i = 0; i < currentMember.interests.size(); i++)
            createViewInterestToEdit(getActivity(), layoutInflater, interestsContent, currentMember.interests.get(i).name);

        if(currentMember.radarVisibility)
            checkBoxRadarVisibility.setChecked(true);
        if(currentMember.notifyByEmailOnComment)
            checkBoxEmailComment.setChecked(true);
        if(currentMember.notifyByEmailOnTodo)
            checkBoxEmailTodo.setChecked(true);
        if(currentMember.notifyByPushOnComment)
            checkBoxPushComment.setChecked(true);
        if(currentMember.notifyByPushOnTodo)
            checkBoxPushTodo.setChecked(true);

        for(int i=0; i<event.hubs.size(); i++) {
            if(!event.hubs.get(i).name.equals(currentMember.hub.name))
                listItemHubSpinner.add(getHubsByAlphabeticalOrder(event.hubs).get(i).name);
        }

//        tv.setText("Uploading file path :- '/sdcard/android_1.png'");

        addItemsOnSpinner();
        //addListenerOnSpinnerItemSelection();
    }

    @Subscribe
    public void onValidFileUploadEvent(UploadImageEvent event) {
        loading.setVisibility(View.INVISIBLE);
        currentMember.image = event.image;
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(currentMember));
        editor.commit();
        ViewHelpers.setImageViewBackgroundFromURLWhenUpdated(getActivity(), picture, event.image);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "profile_picture_temp.jpg");
        boolean deleted = file.delete();
    }

    @Subscribe
    public void onInvalidFileUploadEvent(InvalidFileFailureEvent event) {

        ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.alert_invalid_file), true);

    }

    @Click(R.id.picture)
    public void uploadPicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //add items into spinner dynamically
    public void addItemsOnSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,listItemHubSpinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hubName.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerItemSelection(){
        hubName.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    @Click(R.id.saveButton)
    public void save() {
        //showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability

        saveButton.setEnabled(false);


        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            Member updatedMember = createMemberFromFields();
            memberService.updateMember(currentMember.id, updatedMember);

            //GlobalVariables.isMenuOnPosition = true;
            //GlobalVariables.MENU_POSITION = 5;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            saveButton.setEnabled(true);
        }

        //showProgress(false, getActivity(), meScrollView, logoutProgress);
    }

    @Subscribe
    public void onSaveMemberEvent(SaveMemberEvent event) {
        // save the current Member
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
        editor.commit();

        //if(GlobalVariables.MENU_POSITION == 5)
        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.alert_save), Toast.LENGTH_LONG).show();
        GlobalVariables.isMenuOnPosition = true;
        GlobalVariables.MENU_POSITION = 5;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_left, R.anim.anim_exit_right);
        fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
        // Committing the transaction
        fragmentTransaction.commit();


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

    public Member createMemberFromFields() {
        Member member = currentMember;
        member.updatedAt = formatActualDate();

        member.fullName = fullName.getText().toString();
        member.hub.name = String.valueOf(hubName.getSelectedItem());
        member.image = urlPicture;
        member.goal = goalContent.getText().toString();

        member.companies = new ArrayList<Company>();

        if(!companyName.getText().toString().equals("")) {
            String[] companies = companyName.getText().toString().trim().split(",");

            for(int i=0; i<companies.length; i++) {
                Company company = new Company();

                if (!companies[i].equals("Company"))
                    company.name = companyName.getText().toString();
                else
                    company.name = "";

                member.companies.add(company);
            }
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
        member.phone = phone.getText().toString();

        member.interests = new ArrayList<Interest>();

            for (int i = 0; i < interestsContent.getChildCount(); i++) {
                Interest interest = new Interest();

                if(!((EditText) (interestsContent.getChildAt(i)).findViewById(R.id.interest)).getText().toString().equals("Interest"))
                    interest.name = ((EditText) (interestsContent.getChildAt(i)).findViewById(R.id.interest)).getText().toString();
                else
                    interest.name = "";

                member.interests.add(interest);
            }
        member.radarVisibility = checkBoxRadarVisibility.isChecked();
        member.notifyByEmailOnComment = checkBoxEmailComment.isChecked();
        member.notifyByEmailOnTodo = checkBoxEmailTodo.isChecked();
        member.notifyByPushOnComment = checkBoxPushComment.isChecked();
        member.notifyByPushOnTodo = checkBoxPushTodo.isChecked();

        return member;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {


                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {

                        //imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = GeneralHelpers.decodeUri(getActivity(), imageReturnedIntent.getData());

                        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Thousand-network.jpg");
                        if (!photo.canRead())
                        photo.createNewFile();

                        Crop.of(imageReturnedIntent.getData(), Uri.fromFile(photo)).asSquare().start(getActivity());

                        //ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        loading.setVisibility(View.VISIBLE);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (resultCode == getActivity().RESULT_CANCELED) {

                }


    }




}