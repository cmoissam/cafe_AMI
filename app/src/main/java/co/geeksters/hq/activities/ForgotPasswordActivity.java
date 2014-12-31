package co.geeksters.hq.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.events.failure.LoginFailureEvent;
import co.geeksters.hq.events.success.LoginEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.PasswordResetEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.ConnectService;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.generateEmailsListFromString;

@EActivity(R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends Activity {
    private SharedPreferences.Editor editor;

    // UI references.
    @ViewById
    AutoCompleteTextView emails;

    @ViewById
    EditText password;

    @ViewById
    View forgotPasswordForm;

    @ViewById
    View forgotPasswordProgress;

    @ViewById
    Button resetPasswordButton;

    @ViewById
    Button loginButton;

    @ViewById
    TextView noConnectionText;

    @AfterViews
    public void initListEmails(){
        //emails.setText("soukaina@geeksters.co, abc@email.com");
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

    @Click(R.id.resetPasswordButton)
    public void resetPassword() {
        // Reset errors.
        emails.setError(null);

        // Store values at the time of the reset attempt.
        String emailsContent = emails.getText().toString();

        boolean forgotPassword = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailsContent)) {
            emails.setError(getString(R.string.error_field_required));
        } else {
            String[] emailsList = emailsContent.trim().split(",");

            for (int i = 0; i < emailsList.length; i++) {
                if (GeneralHelpers.isEmailValid(emailsList[i])) {
                    forgotPassword = true;
                }
                else {
                    emails.setError(getString(R.string.error_field_incorrect_emails));
                    forgotPassword = false;
                    break;
                }
            }
        }

        if(forgotPassword){
            emails.requestFocus();

            if(GeneralHelpers.isInternetAvailable(this)) {
                ViewHelpers.showProgress(true, this, forgotPasswordForm, forgotPasswordProgress);

                ConnectService service = new ConnectService();
                GlobalVariables.emails = new ArrayList<String>();
                GlobalVariables.emails = generateEmailsListFromString(emails.getText().toString());
                service.passwordReminder(GlobalVariables.emails);
            } else{
                ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }
        }
    }

    @Click(R.id.loginButton)
    public void loginRedirection() {
        Intent intent = new Intent(this, LoginActivity_.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Subscribe
    public void onPasswordResetEvent(PasswordResetEvent event) {
        ViewHelpers.showProgress(false, this, forgotPasswordForm, forgotPasswordProgress);

        Boolean reset = false;

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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_reset), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, LoginActivity_.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }
}



