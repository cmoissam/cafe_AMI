package co.geeksters.hq.global.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.global.CircleView;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.PredicateLayout;
import co.geeksters.hq.models.Hub;

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

    public static void deleteTextAndSetHint(EditText fiels, String hint) {
        fiels.setText("");
        fiels.setHint(hint);
    }


    public static void showPopup(Activity context, String title, String message,Boolean Error) {

        LayoutInflater inflater = context.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.pop_up, null);
        TextView infoTitle = (TextView) dialoglayout.findViewById(R.id.infoTitle);
        TextView infotext = (TextView) dialoglayout.findViewById(R.id.infoText);
        ImageView infoimage = (ImageView) dialoglayout.findViewById(R.id.infoImage);
        ImageView  cacelImage = (ImageView)dialoglayout.findViewById(R.id.cancelImage);

        Typeface typeFace=Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        infoTitle.setTypeface(null,typeFace.BOLD);
        infotext.setTypeface(typeFace);

        if(Error){
            infoimage.setBackgroundResource(R.drawable.popup_alert);
        }

        infoTitle.setText(title);
        infotext.setText(message);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialoglayout);
        builder.setCancelable(true);
        final AlertDialog ald =builder.show();
        ald.setCancelable(true);

        cacelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ald.dismiss();
            }
        });
    }

    public static void showExitPopup(final Activity context) {

        LayoutInflater inflater = context.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.exit_pop_up, null);
        TextView infoTitle = (TextView) dialoglayout.findViewById(R.id.infoTitle);
        TextView infotext = (TextView) dialoglayout.findViewById(R.id.infoText);
        ImageView infoimage = (ImageView) dialoglayout.findViewById(R.id.infoImage);
        Button cacelImage = (Button)dialoglayout.findViewById(R.id.cancel_image);
        Button quitImage = (Button)dialoglayout.findViewById(R.id.quite_image);

        Typeface typeFace=Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        infoTitle.setTypeface(null,typeFace.BOLD);
        infotext.setTypeface(null, typeFace.BOLD);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialoglayout);
        builder.setCancelable(true);
        final AlertDialog ald =builder.show();
        ald.setCancelable(true);

        cacelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ald.dismiss();
            }
        });
        quitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ald.dismiss();
                context.finish();
                System.exit(0);
            }
        });


    }



    public static void createViewInterest(final Context context, LayoutInflater layoutInflater, final PredicateLayout interestsContent, String lastValue) {
        final View interestContent = layoutInflater.inflate(R.layout.interest_layout, null);
        final TextView text = (TextView) interestContent.findViewById(R.id.interest);
        text.setText(lastValue);
        Typeface typeFace=Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        text.setTypeface(typeFace);
        interestsContent.addView(interestContent);
    }

    public static void createViewInterestToEdit(final Context context, LayoutInflater layoutInflater, final LinearLayout interestsContent, String lastValue) {
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

    public static void createCircleView(final Context context, final LinearLayout radarLayout, float circleRadius) {
        radarLayout.addView(new CircleView(context));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0){
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = desiredWidth;
                listView.setLayoutParams(params);
                listView.requestLayout();

                //view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void drawRadarSlice(ImageView myPosition, float radius, Canvas canvas, Paint paint) {
        int[] myPositionCoordinates = new int[2];
        myPosition.getLocationInWindow(myPositionCoordinates);

        for(int i=1; i<=GlobalVariables.MAX_SLICE_NUMBER; i++) {
            canvas.drawCircle(myPosition.getX() + myPosition.getWidth()/2, myPosition.getY() + myPosition.getHeight()/2, radius * i, paint);
        }
    }

    public static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void setImageViewBackgroundFromURL(Context context, ImageView picture, String image) {
//        if(ImageLoader.getInstance().isInited()) {
//            ImageLoader.getInstance().destroy();
//        }

//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
//        ImageLoader.getInstance().init(config);
//        ImageLoader.getInstance().displayImage(url, picture, options);


        String imageurl = GlobalVariables.UrlApiImage + image;
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageurl, picture);
    }
}