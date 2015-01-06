package co.geeksters.hq.global.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.PredicateLayout;
import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 26/11/14.
 */
public class ViewHelpers {

    /**
     * Shows the progress UI and hides the form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, Context context, final View loginForm, final View loginProgress) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            loginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void deleteTextAndSetHint(EditText fiels, String hint){
        fiels.setText("");
        fiels.setHint(hint);
    }

    public static void showPopup(Context context, String title, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);
        builder1.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void createViewInterest(final Context context, LayoutInflater layoutInflater, final PredicateLayout interestsContent, String lastValue){
        final View interestContent = layoutInflater.inflate(R.layout.interest_layout, null);
        final TextView text = (TextView) interestContent.findViewById(R.id.interest);
        text.setText(lastValue);

        interestsContent.addView(interestContent);
    }

    public static void createViewInterestToEdit(final Context context, LayoutInflater layoutInflater, final LinearLayout interestsContent, String lastValue){
        final View interestContent = layoutInflater.inflate(R.layout.interest_layout_edit, null);
        final EditText text = (EditText) interestContent.findViewById(R.id.interest);
        text.setText(lastValue);
        ImageView delete = (ImageView) interestContent.findViewById(R.id.deleteButtonInterest);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestsContent.getChildCount() == 1) {
                    ViewHelpers.deleteTextAndSetHint(text, context.getResources().getString(R.string.interest_name));
                } else {
                    interestsContent.removeView(interestContent);
                }
            }
        });

        interestsContent.addView(interestContent, 1);
    }

/*    public static ArrayList<HashMap<String, String>> setListMembersContent(List<Member> eventMembers){
        // ArrayList for Listview
        ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        for(int i = 0; i < eventMembers.size(); i++) {
            map = new HashMap<String, String>();

            if(!eventMembers.get(i).image.equals(""))
                map.put("picture", eventMembers.get(i).image);
            else
                map.put("picture", String.valueOf(R.drawable.no_image_member));
            map.put("fullName", eventMembers.get(i).fullName);
            map.put("hubName", eventMembers.get(i).hub.name);

            members.add(map);
        }

        return members;
    }

    public void setListMembersAdapter(Context context, SimpleAdapter adapter, ListView listViewMembers, ArrayList<HashMap<String, String>> members){
        // Adding items to listview
        adapter = new SimpleAdapter(context, members, R.layout.list_item_people_directory,
                new String[]{"picture", "fullName", "hubName"},
                new int[]{R.id.picture, R.id.fullName, R.id.hubName});
        listViewMembers.setAdapter(adapter);
    }*/
}
