package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.ExistingAccountEvent;
import co.geeksters.hq.events.success.MemberEvent;
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
    EditText password;

    @ViewById
    EditText passwordConfirmation;

    @ViewById
    View registerForm;

    @ViewById
    View registerProgress;

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

    @Click(R.id.registerButton)
    public void attemptRegister() {
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
        } else if(!GeneralHelpers.isPasswordConfirmed(passwordContent, passwordConfirmationContent)){
            password.setError(getString(R.string.error_invalid_password_confirmation));
            passwordConfirmation.setError(getString(R.string.error_invalid_password_confirmation));
            focusView = password;
        }

        if(GeneralHelpers.isEmailValid(emailContent) && GeneralHelpers.isPasswordValid(passwordContent) && !TextUtils.isEmpty(fullNameContent) && GeneralHelpers.isPasswordConfirmed(passwordContent, passwordConfirmationContent))
            register = true;

        if (!register) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            ViewHelpers.showProgress(true, this, registerForm, registerProgress);
            // Test internet availability
            if(GeneralHelpers.isInternetAvailable(this)) {
                ConnectService connectService = new ConnectService();
                Member member = new Member(fullNameContent, emailContent, passwordContent, passwordConfirmationContent);
                connectService.register(member);
            } else{
                ViewHelpers.showProgress(false, this, registerForm, registerProgress);
                ViewHelpers.showPopupOnNoNetworkConnection(this);
            }
        }
    }

    @Subscribe
    public void onRegisterEvent(MemberEvent event) {
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.putExtra("username", event.member.email);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);

        ViewHelpers.showProgress(false, this, registerForm, registerProgress);
    }

    @Subscribe
    public void onRegisterFailureEvent(ExistingAccountEvent event) {
        ViewHelpers.showProgress(false, this, registerForm, registerProgress);

        email.setError(getString(R.string.error_field_exists));
    }

    @Click(R.id.emailSignInButton)
    public void loginRedirection() {
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}



