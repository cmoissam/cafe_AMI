package co.geeksters.hq.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.PasswordResetEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.services.ConnectService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.generateEmailsListFromString;

@EActivity(R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends Activity {
    private SharedPreferences.Editor editor;

    // UI references.
    @ViewById
    AutoCompleteTextView emails;

    @ViewById
    pl.droidsonroids.gif.GifImageView loadingGif;


    @ViewById
    View forgotPasswordForm;

    @ViewById
    Button resetPasswordButton;

    @ViewById
    Button loginButton;

    @ViewById
    TextView noConnectionText;

    @AfterViews
    public void initListEmails(){
        Typeface typeFace=Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        emails.setTypeface(typeFace);
        noConnectionText.setTypeface(typeFace);
        loginButton.setTypeface(typeFace);
        resetPasswordButton.setTypeface(typeFace);
        loadingGif.setVisibility(View.INVISIBLE);
    }

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
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

    @Touch(R.id.resetPasswordButton)
    public void resetPassword(View v, MotionEvent event) {
        // Reset errors.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.parseColor("#89c4c7"));
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {


            v.setBackgroundColor(Color.parseColor("#FFFFFF"));
            emails.setError(null);

            // Store values at the time of the reset attempt.
            String emailsContent = emails.getText().toString();

            emailsContent = GeneralHelpers.toLowerCase(emailsContent);

            boolean forgotPassword = false;

            // Check for a valid email address.
            if (TextUtils.isEmpty(emailsContent)) {
                emails.setError(getString(R.string.error_field_required));
            } else {

                    if (GeneralHelpers.isEmailValid(emailsContent)) {
                        forgotPassword = true;
                    } else {
                        emails.setError(getString(R.string.error_field_incorrect_emails));
                        forgotPassword = false;
                }
            }

            if (forgotPassword) {
                emails.requestFocus();

                if (GeneralHelpers.isInternetAvailable(this)) {
                    loadingGif.setVisibility(View.VISIBLE);
                    ConnectService service = new ConnectService();
                    GlobalVariables.emails = new ArrayList<String>();
                    GlobalVariables.emails = generateEmailsListFromString(emails.getText().toString());
                    service.passwordReminder(GlobalVariables.emails);
                } else {
                    ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
                }
            }
        }
    }
    @Touch(R.id.loginButton)
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

    @Subscribe
    public void onPasswordResetEvent(PasswordResetEvent event) {
        Boolean reset = false;
        loadingGif.setVisibility(View.INVISIBLE);
        for(int i = 0; i < event.emailsResponse.size(); i++) {
            if(event.emailsResponse.get(i).status.replace("\"","").equals("error")) {
                emails.setError(event.emailsResponse.get(i).email + " " + getString(R.string.error_field_incorrect_email));
                reset = false;
                break;
            } else {
                reset = true;
            }
        }

        if(reset) {


            LayoutInflater inflater = this.getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.pop_up, null);
            TextView infoTitle = (TextView) dialoglayout.findViewById(R.id.infoTitle);
            TextView infotext = (TextView) dialoglayout.findViewById(R.id.infoText);
            ImageView infoimage = (ImageView) dialoglayout.findViewById(R.id.infoImage);
            ImageView  cacelImage = (ImageView)dialoglayout.findViewById(R.id.cancelImage);

            Typeface typeFace=Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Regular.ttf");
            infoTitle.setTypeface(null, typeFace.BOLD);
            infotext.setTypeface(typeFace);

            infoTitle.setText("reset success");
            infotext.setText(event.emailsResponse.get(0).email);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialoglayout);
            builder.setCancelable(true);
            final AlertDialog ald =builder.show();
            ald.setCancelable(true);

            cacelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ald.dismiss();
                    passToLogin();

                }
            });


        }
    }

    public final void passToLogin()
    {

        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);

    }

    @Subscribe
    public void onConnectionFailureEvent(ConnectionFailureEvent event){

        loadingGif.setVisibility(View.INVISIBLE);
        emails.setEnabled(true);
        ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
    }
}



