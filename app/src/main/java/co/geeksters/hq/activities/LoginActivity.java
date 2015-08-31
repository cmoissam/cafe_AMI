package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.LoginFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MembersEvent;
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
    public static GoogleCloudMessaging gcm;
    public static String regid;
    public static Context context;

    @AfterViews
    public void setPreferencesEditorAndVerifyLogin(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        String accessToken = preferences.getString("access_token","").replace("\"", "");
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
            email.setText("issam@geeksters.co");
        }
        password.setText("passfortest");
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

    /*  GCM */
   /* @Background
    public void registerDevice(){
        longRunningProcessStarted();
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(getAppContext());
            }
            regid = gcm.register("773290153741");
            msg = "Device registered, registration ID=" + regid;
            Log.i("1234 GCM",  msg);



        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            Log.i("Error regeister GCM", msg);

        }
        longRunningProcessEnded(regid);



    }

    @UiThread
    void longRunningProcessStarted() {

    }

    @UiThread
    void longRunningProcessEnded(String regid) {

        storeRegId(regid);
        //Toast.makeText(context,"Device DEJA registered, registration ID=" + regid,Toast.LENGTH_SHORT).show();
        Log.i("-------REG ID --------",regid);

    }

    public static void storeRegId(String regId){

        *//*editor.putString(REG_ID, regId);
        editor.commit();*//*
    }

    public static String retreiveRegIdPref(){
        *//*String regId = sharedPref.getString(REG_ID, "");
        return regId;*//*
        return "";
    }

    public void checkRegId(){
        regid = retreiveRegIdPref();
        if(regid== null ||regid.equals("")){
            //getRegId();
            //Toast.makeText(context,"Device non enregistré ---> Il va etre enregitres mnt",Toast.LENGTH_SHORT).show();
            registerDevice();
        }else {
            //Toast.makeText(context,"Device DEJA registered, registration ID=" + regid,Toast.LENGTH_SHORT).show();
            Log.i("-------REG ID --------",regid);
        }
    }

    public static String getDeviceId(){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    //	GCM END*/

    @Subscribe
    public void onLoginEvent(LoginEvent event) {
        // save the token

        context = getApplicationContext();
        editor.putString("access_token", event.accessToken.replace("\"", ""));
        editor.commit();



        registerInBackground(event.member,event.accessToken.replace("\"", ""));


        //        if(GeneralHelpers.isInternetAvailable(this)) {
//            MemberService memberService = new MemberService(event.accessToken.replace("\"",""));
//            memberService.getMemberInfo(780);
//        } else {
//            ViewHelpers.showProgress(false, this, loginForm, loginProgress);
//            ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
//        }
    }

    public void sendRegistrationIdToBackend(Member member, String accessToken){

        MemberService memberService = new MemberService(accessToken);
        memberService.updateMember(member.id,member);

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
    public void onUpdateEvent(SaveMemberEvent event){


        Intent intent = new Intent(this, GlobalMenuActivity_.class);
        finish();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        ViewHelpers.showProgress(false, this, loginForm, loginProgress);
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



