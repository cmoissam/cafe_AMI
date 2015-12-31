package co.geeksters.cafe_ami.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.events.failure.ConnectionFailureEvent;
import co.geeksters.cafe_ami.events.failure.UnauthorizedFailureEvent;
import co.geeksters.cafe_ami.events.success.SaveMemberEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.services.MemberService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EActivity(R.layout.activity_start)
public class EmailLinkActivity extends Activity {
    private SharedPreferences.Editor editor;


    public String type = "";
    public int postId = - 1;


    @ViewById
    pl.droidsonroids.gif.GifImageView loadingGif;

    @ViewById
    TextView welcome;


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
        loadingGif.setVisibility(View.VISIBLE);


        if (!accessToken.equals("")) {
                Member currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
                MemberService memberService = new MemberService(accessToken);
                memberService.updateMember(currentMember.id, currentMember);
            }
        else{

            loadingGif.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, LoginActivity_.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();


        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();
        String scheme = data.getScheme(); // "http"
        String host = data.getHost(); // "thousand.network"
        List<String> params = data.getPathSegments();
        String first = params.get(0); // "comment"
        type = first;
        if(type.equals("comment"))
        {
        String second = params.get(1);//"post_id"
        postId = Integer.parseInt(second);
        }
    }

    @Subscribe
    public void onConnectionFailureEvent(ConnectionFailureEvent event)
    {

        loadingGif.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }

    @Subscribe
    public void onUpdatefailureEvent(UnauthorizedFailureEvent event)
    {
        loadingGif.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }


    @Subscribe
    public void onUpdateEvent(SaveMemberEvent event){

        if(type.equals("comment")) {
            GlobalVariables.notifiyedByPost = true;
            GlobalVariables.notificationPostId = postId;
        }
        if(type.equals("todo"))
            GlobalVariables.notifiyedByTodo = true;
        if(type.equals("interest_tag"))
            GlobalVariables.notifiyedByInterestsOnPost = true;

        loadingGif.setVisibility(View.INVISIBLE);
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



