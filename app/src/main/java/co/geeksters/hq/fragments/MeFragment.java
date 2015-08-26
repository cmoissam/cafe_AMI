package co.geeksters.hq.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.github.kevinsawicki.http.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.events.failure.InvalidFileFailureEvent;
import co.geeksters.hq.events.success.EmptyEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.Config;
import co.geeksters.hq.global.CustomOnItemSelectedListener;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Social;
import co.geeksters.hq.services.HubService;
import co.geeksters.hq.services.MemberService;
import retrofit.mime.TypedFile;

import static co.geeksters.hq.global.helpers.GeneralHelpers.formatActualDate;
import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterestToEdit;
import static co.geeksters.hq.global.helpers.ViewHelpers.deleteTextAndSetHint;
import static co.geeksters.hq.global.helpers.ViewHelpers.showProgress;
import static co.geeksters.hq.models.Hub.getHubsByAlphabeticalOrder;

@EFragment(R.layout.fragment_me)
public class MeFragment extends Fragment {

    Bitmap yourSelectedImage = null;

    @ViewById(R.id.fullName)
    EditText fullName;

    @ViewById(R.id.spinnerHubName)
    Spinner hubName;

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
    List<String> listItemHubSpinner = new ArrayList<String>();
    private static final int SELECT_PHOTO = 100;
    String urlPicture;
    Cloudinary cloudinary = new Cloudinary(Config.setCloudinaryConfiguration());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        GlobalVariables.MENU_POSITION = 6;
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

    public void listAllHubsService() {
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        accessToken = preferences.getString("access_token", "").toString().replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
            HubService hubService = new HubService(accessToken);
            hubService.listAllHubs();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
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

        if(currentMember.interests == null) {
            interest.setText("");
        }

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

        for(int i=0; i<event.hubs.size(); i++) {
            if(!event.hubs.get(i).name.equals(currentMember.hub.name))
                listItemHubSpinner.add(getHubsByAlphabeticalOrder(event.hubs).get(i).name);
        }

//        tv.setText("Uploading file path :- '/sdcard/android_1.png'");

        addItemsOnSpinner();
        //addListenerOnSpinnerItemSelection();
    }

    @Subscribe
    public void onValidFileUploadEvent(EmptyEvent event) {

        picture.setImageBitmap(yourSelectedImage);
    }

    @Subscribe
    public void onInvalidFileUploadEvent(InvalidFileFailureEvent event) {

        ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.alert_invalid_file));

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
        showProgress(true, getActivity(), meScrollView, logoutProgress);
        // Test internet availability
        if(isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            Member updatedMember = createMemberFromFields();
            memberService.updateMember(currentMember.id, updatedMember);

            //GlobalVariables.isMenuOnPosition = true;
            //GlobalVariables.MENU_POSITION = 5;
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }

        showProgress(false, getActivity(), meScrollView, logoutProgress);
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
        fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
        // Committing the transaction
        fragmentTransaction.commit();


    }

    @Click(R.id.deleteButton)
    public void deleteAccount() {
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

    public Member createMemberFromFields() {
        Member member = currentMember;
        member.updatedAt = formatActualDate();

        member.fullName = fullName.getText().toString();
        member.hub.name = String.valueOf(hubName.getSelectedItem());
        member.image = urlPicture;

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

        member.interests = new ArrayList<Interest>();

        if(!((EditText) (interestsContent.getChildAt(0)).findViewById(R.id.interest)).getText().toString().equals("")) {
            for (int i = 0; i < interestsContent.getChildCount(); i++) {
                Interest interest = new Interest();

                if(!((EditText) (interestsContent.getChildAt(i)).findViewById(R.id.interest)).getText().toString().equals("Interest"))
                    interest.name = ((EditText) (interestsContent.getChildAt(i)).findViewById(R.id.interest)).getText().toString();
                else
                    interest.name = "";

                member.interests.add(interest);
            }
        }

        member.notifyByEmailOnComment = checkBoxEmailComment.isChecked();
        member.notifyByEmailOnTodo = checkBoxEmailTodo.isChecked();
        member.notifyByPushOnComment = checkBoxPushComment.isChecked();
        member.notifyByPushOnTodo = checkBoxPushTodo.isChecked();

        return member;
    }
    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == getActivity().RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = GeneralHelpers.decodeUri(getActivity(), imageReturnedIntent.getData());

                        String selectedImagePath = null;

                        Cursor cursor = getActivity().getContentResolver().query(
                                selectedImage, null, null, null, null);
                        if (cursor == null) {
                            selectedImagePath = selectedImage.getPath();
                        } else {
                            cursor.moveToFirst();
                            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            selectedImagePath = cursor.getString(idx);
                        }
                        File photo = new File(selectedImagePath);
                        TypedFile typedImage = new TypedFile("application/octet-stream", photo);




                        MemberService memberService = new MemberService(accessToken);
                        memberService.updateImage(currentMember.id,typedImage);
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


}