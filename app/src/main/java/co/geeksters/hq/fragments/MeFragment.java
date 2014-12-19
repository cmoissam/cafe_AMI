package co.geeksters.hq.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.LoginActivity_;
import co.geeksters.hq.activities.RegisterActivity_;
import co.geeksters.hq.global.helpers.ViewHelpers;

@EFragment(R.layout.fragment_me)
public class MeFragment extends Fragment {
    View interestContentAdded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        interestContentAdded = inflater.inflate(R.layout.interest_layout, container, false);
        return null;
    }

    @ViewById
    EditText bioContent;

    @ViewById
    ScrollView meScrollView;

    @ViewById
    Button logoutButton;

    @ViewById
    TextView contact;

    @ViewById
    EditText linkdin;

    @ViewById
    EditText twitter;

    @ViewById
    EditText facebook;

    @ViewById
    EditText skype;

    @ViewById
    EditText blog;

    @ViewById
    EditText website;

    @ViewById
    EditText interest;

    @ViewById
    ImageView addButtonInterest;

    @ViewById
    LinearLayout interestsContent;

    @ViewById
    LinearLayout interestContent;

    @Click(R.id.logoutButton)
    public void logout(){
        Intent intent = new Intent(getActivity(), LoginActivity_.class);
        getActivity().startActivityForResult(intent, 1);
        getActivity().finish();
    }

    @FocusChange(R.id.bioContent)
    public void scrollToTop(){
        meScrollView.post(new Runnable() {
            @Override
            public void run() {
                meScrollView.scrollTo(0, contact.getBottom());
            }
        });
    }

    @Click(R.id.deleteButtonLinkdin)
    public void deleteLinkdinLink(){
        ViewHelpers.deleteTextAndSetHint(linkdin, "linkdin");
    }

    @Click(R.id.deleteButtonTwitter)
    public void deleteTwitterLink(){
        ViewHelpers.deleteTextAndSetHint(twitter, "twitter");
    }

    @Click(R.id.deleteButtonFacebook)
    public void deleteFacebookLink(){
        ViewHelpers.deleteTextAndSetHint(facebook, "facebook");
    }

    @Click(R.id.deleteButtonSkype)
    public void deleteSkypeLink(){
        ViewHelpers.deleteTextAndSetHint(skype, "skype");
    }

    @Click(R.id.deleteButtonBlog)
    public void deleteBlogLink(){
        ViewHelpers.deleteTextAndSetHint(blog, "blog");
    }

    @Click(R.id.deleteButtonWebsite)
    public void deleteWebsiteLink(){
        ViewHelpers.deleteTextAndSetHint(website, "website");
    }

    @Click(R.id.deleteButtonInterest)
    public void deleteInterest(){
        if(interestsContent.getChildCount() == 1) {
            Log.d("Child Number","One Child");
            ViewHelpers.deleteTextAndSetHint(interest, getResources().getString(R.string.interest_name));
            addButtonInterest.setVisibility(View.GONE);
        } else {
            Log.d("Child Number",interestsContent.getChildCount() + " Childs");
            interestsContent.removeView(interestContent);
        }
    }

    @TextChange(R.id.interest)
    public void setVisibilityAddInterest(){
        if(interest.getText().length() > 0){
            addButtonInterest.setVisibility(View.VISIBLE);
        } else {
            addButtonInterest.setVisibility(View.GONE);
        }

    }

    @Click(R.id.addButtonInterest)
    public void addInterestField(){
        addButtonInterest.setVisibility(View.GONE);
        final EditText text = (EditText) interestContentAdded.findViewById(R.id.interestAdded);
        text.setText("");
        final ImageView add = (ImageView) interestContentAdded.findViewById(R.id.addButtonInterestAdded);
        ImageView delete = (ImageView) interestContentAdded.findViewById(R.id.deleteButtonInterestAdded);

        text.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(text.getText().length() > 0){
                    add.setVisibility(View.VISIBLE);
                } else {
                    add.setVisibility(View.GONE);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInterestField();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (interestsContent.getChildCount() == 1) {
                    Log.d("Child Number", "One Child");
                    ViewHelpers.deleteTextAndSetHint(text, getResources().getString(R.string.interest_name));
                    add.setVisibility(View.GONE);
                } else {*/
                    //Log.d("Child Number", interestsContent.getChildCount() + " Childs");
                    interestsContent.removeView(interestContentAdded);
                    addButtonInterest.setVisibility(View.VISIBLE);
                //}
            }
        });

        interestsContent.addView(interestContentAdded);
    }
}
