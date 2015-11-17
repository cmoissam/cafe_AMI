package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.failure.LoginFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
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
    pl.droidsonroids.gif.GifImageView loadingGif;

    @ViewById
    View loginForm;

    @ViewById
    View emailLoginForm;

    @ViewById
    Button emailSignInButton;

    @ViewById
    Button registerButton;

    @ViewById
    TextView welcome;

    @ViewById
    Button forgotPasswordButton;

    @ViewById
    TextView noConnectionText;

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }
    public static GoogleCloudMessaging gcm;
    public static String regid;
    public static Context context;

    @AfterViews
    public void setPreferencesEditorAndVerifyLogin(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        String accessToken = preferences.getString("access_token","").replace("\"", "");
        Typeface typeFace=Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        welcome.setTypeface(typeFace);
        email.setTypeface(typeFace);
        password.setTypeface(typeFace);
        noConnectionText.setTypeface(typeFace);
        registerButton.setTypeface(typeFace);
        emailSignInButton.setTypeface(typeFace);
        forgotPasswordButton.setTypeface(typeFace);
        loadingGif.setVisibility(View.INVISIBLE);

        if(GlobalVariables.sessionExpired)
        {
            ViewHelpers.showPopup(this, "oh! sorry", "session expired",true);
            GlobalVariables.sessionExpired = false;

        }

    }

    @AfterViews
    public void initUsernameOnRegister(){
        Intent intent = getIntent();
        String emailOnRegister = intent.getStringExtra("username");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email.setText(preferences.getString("last_email",""));
        if(emailOnRegister != null){
            email.setText(emailOnRegister);
        }
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

    @Touch(R.id.emailSignInButton)
    public void attemptLogin(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor("#89c4c7"));
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));


        email.setEnabled(false);
        password.setEnabled(false);
        // Reset errors.
        email.setError(null);
        password.setError(null);

        // Store values at the time of the login attempt.
        String emailContent = email.getText().toString();
        String passwordContent = password.getText().toString();

            emailContent = GeneralHelpers.toLowerCase(emailContent);

        boolean login = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailContent)) {
            email.setError(getString(R.string.error_field_required));
            email.setEnabled(true);
            password.setEnabled(true);
            focusView = email;
        } else if (!GeneralHelpers.isEmailValid(emailContent)) {
            email.setError(getString(R.string.error_invalid_email));
            email.setEnabled(true);
            password.setEnabled(true);
            focusView = email;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordContent)) {
            password.setError(getString(R.string.error_field_required));
            email.setEnabled(true);
            password.setEnabled(true);
            focusView = password;
        } else if (!GeneralHelpers.isPasswordValid(passwordContent)) {
            password.setError(getString(R.string.error_invalid_password));
            email.setEnabled(true);
            password.setEnabled(true);
            focusView = password;
        }

        if (GeneralHelpers.isEmailValid(emailContent) && GeneralHelpers.isPasswordValid(passwordContent))
            login = true;

        if (!login) {
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
                connectService.login("password", 1, "pioner911", emailContent, passwordContent, "basic");
            } else {
                email.setEnabled(true);
                password.setEnabled(true);
                ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            }
        }
    }
    }




    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        // save the token
        context = getApplicationContext();
        editor.putString("access_token", event.accessToken.replace("\"", ""));
        editor.commit();

        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("last_email",email.getText().toString()).commit();
        registerInBackground(event.member, event.accessToken.replace("\"", ""));

    }

    public void sendRegistrationIdToBackend(Member member, String accessToken){

        MemberService memberService = new MemberService(accessToken);
        memberService.updateMember(member.id, member);

    }

    private void registerInBackground(final Member member, final String accessToken) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(GlobalVariables.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.

                    member.deviceToken = regid;
                    member.deviceType = "android";
                    editor.putString("current_member", ParseHelpers.createJsonStringFromModel(member));
                    editor.commit();
                    sendRegistrationIdToBackend(member,accessToken);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                  //  storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }



    @Subscribe
    public void onLoginFailureEvent(LoginFailureEvent event) {

        loadingGif.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        if(event.errorMessage.equals("need email confirmation"))
            email.setError("need email confirmation");
        else if(event.errorMessage.equals("wrong password"))
            password.setError("wrong password");
            else if(event.errorMessage.equals("not existant email"))
            ViewHelpers.showPopup(this,"oh! sorry", "nonexistent email, please register or contact your hub administrator.", true);
    }

    @Subscribe
    public void onConnectionFailureEvent(ConnectionFailureEvent event){

        loadingGif.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
    }

    @Touch(R.id.registerButton)
    public void registerRedirection(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor("#89c4c7"));
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Intent intent = new Intent(this, RegisterActivity_.class);
            startActivity(intent);
            finish();
        }
    }

    @Touch(R.id.forgotPasswordButton)
    public void forgotPasswordRedirection(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor("#89c4c7"));
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Intent intent = new Intent(this, ForgotPasswordActivity_.class);
            startActivity(intent);
            finish();
        }
    }

    @Subscribe
    public void onUpdateEvent(SaveMemberEvent event){
        loadingGif.setVisibility(View.INVISIBLE);
        GlobalVariables.notifiyedByPost = false;
        GlobalVariables.notifiyedByTodo = false;
        Intent intent = new Intent(this, GlobalMenuActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if(BaseApplication.isRegistered(this))
        BaseApplication.unregister(this);
    }
}



