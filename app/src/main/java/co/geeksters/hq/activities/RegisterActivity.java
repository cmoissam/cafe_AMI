package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.ExistingAccountEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.ConnectService;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends Activity {

    // UI references.
    @ViewById
    AutoCompleteTextView fullName;

    @ViewById
    AutoCompleteTextView email;

    @ViewById
    pl.droidsonroids.gif.GifImageView loadingGif;

    @ViewById
    EditText password;

    @ViewById
    EditText passwordConfirmation;

    @ViewById
    View registerForm;

    @ViewById
    View memberRegisterForm;

    @ViewById
    Button emailSignInButton;

    @ViewById
    Button registerButton;

    @ViewById
    TextView noConnectionText;

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }

    @AfterViews
    public void initFiledsForTest(){
        // Random rand = new Random();
        // int randomNum = rand.nextInt((1000 - 0) + 1);
        fullName.setText("Soukaina");
        email.setText("soukaina@geeksters.co");
        password.setText("soukaina");
        passwordConfirmation.setText("soukaina");
        loadingGif.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActionBar().hide();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }

    @Touch(R.id.registerButton)
    public void attemptRegister(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(Color.parseColor("#89c4c7"));
        }else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));

            // Reset errors.
            fullName.setError(null);
            email.setError(null);
            password.setError(null);
            passwordConfirmation.setError(null);

            // Store values at the time of the login attempt.
            String fullNameContent = fullName.getText().toString();
            String emailContent = email.getText().toString();
            String passwordContent = password.getText().toString();
            String passwordConfirmationContent = passwordConfirmation.getText().toString();

            boolean register = false;
            View focusView = null;

            // Check for a valid email address.
            if (TextUtils.isEmpty(fullNameContent)) {
                fullName.setError(getString(R.string.error_field_required));
                focusView = fullName;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(emailContent)) {
                email.setError(getString(R.string.error_field_required));
                focusView = email;
            } else if (!GeneralHelpers.isEmailValid(emailContent)) {
                email.setError(getString(R.string.error_invalid_email));
                focusView = email;
            }

            // Check for a valid password, if the user entered one.
            if (TextUtils.isEmpty(passwordContent)) {
                password.setError(getString(R.string.error_field_required));
                focusView = password;
            } else if (!GeneralHelpers.isPasswordValid(passwordContent)) {
                password.setError(getString(R.string.error_invalid_password));
                focusView = password;
            } else if (!GeneralHelpers.isPasswordConfirmed(passwordContent, passwordConfirmationContent)) {
                password.setError(getString(R.string.error_invalid_password_confirmation));
                passwordConfirmation.setError(getString(R.string.error_invalid_password_confirmation));
                focusView = password;
            }

            if (GeneralHelpers.isEmailValid(emailContent) && GeneralHelpers.isPasswordValid(passwordContent) && !TextUtils.isEmpty(fullNameContent) && GeneralHelpers.isPasswordConfirmed(passwordContent, passwordConfirmationContent))
                register = true;

            if (!register) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                // Test internet availability
                if (GeneralHelpers.isInternetAvailable(this)) {
                    loadingGif.setVisibility(View.VISIBLE);
                    ConnectService connectService = new ConnectService();
                    Member member = new Member(fullNameContent, emailContent, passwordContent, passwordConfirmationContent);
                    connectService.register(member);
                } else {
                    ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
                }
            }
        }
    }

    @Subscribe
    public void onRegisterEvent(SaveMemberEvent event) {
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.putExtra("username", event.member.email);
        loadingGif.setVisibility(View.INVISIBLE);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Subscribe
    public void onRegisterFailureEvent(ExistingAccountEvent event) {
        loadingGif.setVisibility(View.INVISIBLE);
        email.setError(getString(R.string.error_field_exists));
        email.requestFocus();
    }

    @Touch(R.id.emailSignInButton)
    public void loginRedirection(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor("#89c4c7"));
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Intent intent = new Intent(this, LoginActivity_.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }
}



