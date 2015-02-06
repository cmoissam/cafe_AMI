package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.LoginFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.services.ConnectService;
import co.geeksters.hq.services.MemberService;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {
    private SharedPreferences.Editor editor;

    // UI references.
    @ViewById
    AutoCompleteTextView email;

    @ViewById
    EditText password;

    @ViewById
    View loginForm;

    @ViewById
    View loginProgress;

    @ViewById
    View emailLoginForm;

    @ViewById
    Button emailSignInButton;

    @ViewById
    Button registerButton;

    @ViewById
    Button forgotPasswordButton;

    @ViewById
    TextView noConnectionText;

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }

    @AfterViews
    public void setPreferencesEditorAndVerifyLogin(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        String accessToken = preferences.getString("access_token","").replace("\"","");
        //Member currentMember =  Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        if(!accessToken.equals("")) {
            Intent intent = new Intent(this, GlobalMenuActivity_.class);
            finish();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            ViewHelpers.showProgress(false, this, loginForm, loginProgress);
        }
    }

    @AfterViews
    public void initUsernameOnRegister(){
        Intent intent = getIntent();
        String emailOnRegister = intent.getStringExtra("username");
        if(emailOnRegister != null){
            email.setText(emailOnRegister);
        } else {
            email.setText("soukaina@geeksters.co");
        }
        password.setText("soukaina");
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

    @Click(R.id.emailSignInButton)
    public void attemptLogin() {
        // Reset errors.
        email.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String emailContent = email.getText().toString();
        String passwordContent = password.getText().toString();

        boolean login = false;
        View focusView = null;

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
        }

        if(GeneralHelpers.isEmailValid(emailContent) && GeneralHelpers.isPasswordValid(passwordContent))
            login = true;

        if (!login) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            ViewHelpers.showProgress(true, this, loginForm, loginProgress);
            // Test internet availability
            if(GeneralHelpers.isInternetAvailable(this)) {
                ConnectService connectService = new ConnectService();
                connectService.login("password", 1, "pioner911", emailContent, passwordContent, "basic");
            } else {
                ViewHelpers.showProgress(false, this, loginForm, loginProgress);
                ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }
        }
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        // save the token
        editor.putString("access_token", event.accessToken);
        editor.commit();

        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
        editor.commit();

        Intent intent = new Intent(this, GlobalMenuActivity_.class);
        finish();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        ViewHelpers.showProgress(false, this, loginForm, loginProgress);

//        if(GeneralHelpers.isInternetAvailable(this)) {
//            MemberService memberService = new MemberService(event.accessToken.replace("\"",""));
//            memberService.getMemberInfo(780);
//        } else {
//            ViewHelpers.showProgress(false, this, loginForm, loginProgress);
//            ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
//        }
    }

//    @Subscribe
//    public void onGetCurrentMemberEvent(SaveMemberEvent event) {
//        // save the current Member
//        editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
//        editor.commit();
//
//        Intent intent = new Intent(this, GlobalMenuActivity_.class);
//        finish();
//        overridePendingTransition(0, 0);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//
//        ViewHelpers.showProgress(false, this, loginForm, loginProgress);
//    }

    @Subscribe
    public void onLoginFailureEvent(LoginFailureEvent event) {
        ViewHelpers.showProgress(false, this, loginForm, loginProgress);
        email.setError(getString(R.string.error_field_incorrect_identifiers));
    }

    @Click(R.id.registerButton)
    public void registerRedirection() {
        Intent intent = new Intent(this, RegisterActivity_.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Click(R.id.forgotPasswordButton)
    public void forgotPasswordRedirection() {
        Intent intent = new Intent(this, ForgotPasswordActivity_.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }
}



