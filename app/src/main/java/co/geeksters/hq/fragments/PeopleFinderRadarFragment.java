package co.geeksters.hq.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.internal.my;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GPSTrackerHelpers;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_people_finder_radar)
public class PeopleFinderRadarFragment extends Fragment {
    View view;
    static Member currentMember;
    List<Member> membersList = new ArrayList<Member>();
    Bitmap bitMap;
    String accessToken;
    static boolean finder = true;
    static boolean setBitmap = true;

    @ViewById(R.id.radarForm)
    LinearLayout radarForm;

    @ViewById(R.id.me)
    ImageView myPosition;

    @AfterViews
    public void listAllMembersAroundMeService() {
        setBitmap = true;

        if(GlobalVariables.afterViewsRadar)
            finder = true;

        if(finder) {
            if (GeneralHelpers.isInternetAvailable(getActivity())) {
                MemberService memberService = new MemberService(accessToken);
                memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }

            GlobalVariables.afterViewsRadar = false;
            finder = false;
        }
    }

    public void zoomOnRadar(List<Member> membersList) {
        if(membersList.size() != 0) {
            List<Integer> sliceIndexList = new ArrayList<Integer>();

            for (int i = 0; i < membersList.size(); i++) {
                sliceIndexList.add(getSliceIndex(membersList.get(i)));
            }

            int max = sliceIndexList.get(0);
            for (int i = 0; i < sliceIndexList.size(); i++) {
                if (sliceIndexList.get(i) > max) {
                    max = sliceIndexList.get(i);
                }
            }

            GlobalVariables.MAX_SLICE_NUMBER = max + 1;
        } else {
            GlobalVariables.MAX_SLICE_NUMBER = 1;
        }
    }

    @Subscribe
    public void onGetListMembersAroundMeEvent(MembersEvent event) {
        GeneralHelpers.setSliceNumber();

        zoomOnRadar(event.members);

        GlobalVariables.membersAroundMe = new ArrayList<Member>();
        GlobalVariables.membersAroundMe.addAll(event.members);
        membersList = event.members;

        membersList = Member.orderMembersByDescDistance(membersList);

        final float radius = (radarForm.getHeight()) / (GlobalVariables.MAX_SLICE_NUMBER + 1);

        ViewGroup.LayoutParams params = myPosition.getLayoutParams();
        params.width = (int) (2 * radius/3);
        params.height = (int) (2 * radius/3);
        myPosition.setLayoutParams(params);
        myPosition.requestLayout();

        // TODO : return to @AfterViews
        //if(setBitmap) {
        createBitMap(radius);

        for (int i = 0; i < membersList.size(); i++) {
            int sliceIndex = getSliceIndex(membersList.get(i));

            ImageView memberImage = new ImageView(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (radius / 3), (int) (radius / 3));
            memberImage.setLayoutParams(layoutParams);
            memberImage.setBackgroundDrawable(getResources().getDrawable(R.drawable.no_image_member));

            float angle = 0;
            float randomX = 0;
            float randomY = 0;
            boolean positionOk = false;

            while (true) {
                angle = (float) (Math.random() * Math.PI * 2);
                randomX = (float) (Math.cos(angle) * sliceIndex * radius);
                randomY = (float) (Math.sin(angle) * sliceIndex * radius);

                float minExeptLeft = -sliceIndex * radius;
                float maxExeptLeft = (1 - sliceIndex) * radius;

                float minExeptRight = (sliceIndex - 1) * radius;
                float maxExeptRight = sliceIndex * radius;

                float minExeptMyPositionX = -myPosition.getWidth() / 2;
                float minExeptMyPositionY = -myPosition.getHeight() / 2 + myPosition.getHeight() / 2;
                float maxExeptMyPositionX = myPosition.getWidth() / 2;
                float maxExeptMyPositionY = myPosition.getHeight() / 2 + myPosition.getHeight() / 2;

                //if(randomX >= - radarForm.getWidth()/2 && randomX <= radarForm.getWidth()/2
//                        && randomY + radius + myPosition.getWidth()/2 > 20 && randomY + radius + myPosition.getWidth()/2 < 20 + myPosition.getHeight() + 20 - radarForm.getHeight()
                //    ) {
                if (-radarForm.getWidth() / 2 < randomX && randomX < radarForm.getWidth() / 2 && 0 > randomY &&
                        randomY > myPosition.getHeight() - radarForm.getHeight()) {
                    if (sliceIndex == 1) {
                        if (!(randomX > minExeptMyPositionX && randomX < maxExeptMyPositionX && randomY > minExeptMyPositionY + radius + myPosition.getWidth() / 2
                                && randomY < maxExeptMyPositionY + radius + myPosition.getWidth() / 2))
                            break;
                    } else if ((randomX > minExeptLeft && randomX < maxExeptLeft && randomY > minExeptLeft && randomY < maxExeptLeft)
                            || (randomX > minExeptRight && randomX < maxExeptRight && randomY > minExeptRight && randomY < maxExeptRight)) {
                        break;
                    }
                }
            }

            memberImage.setX(randomX);
            memberImage.setY(randomY + radius);

            final int index = i;
            memberImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    GlobalVariables.finderRadar = false;
                    GlobalVariables.isMenuOnPosition = false;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(membersList.get(index));
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            radarForm.addView(memberImage, 0);

            if(i == membersList.size() - 1)
                setBitmap = false;
        }
    }

    private void createBitMap(final float radius) {
        if (GeneralHelpers.isInternetAvailable(getActivity())) {
            if (bitMap != null) {
                bitMap.recycle();
                bitMap = null;
            }

            //Create a new bitmap to load the bitmap again.
            bitMap = Bitmap.createBitmap(radarForm.getWidth(), radarForm.getHeight(), Bitmap.Config.ARGB_8888);
        }

        // bitMap = bitMap.copy(bitMap.getConfig(), true);
        // Construct a canvas with the specified bitmap to draw into
        final Canvas canvas = new Canvas(bitMap);
        // Create a new paint with default settings.
        final Paint paint = new Paint();

        // smooths out the edges of what is being drawn
        paint.setAntiAlias(true);
        // set color
        paint.setColor(Color.BLACK);
        // set style
        paint.setStyle(Paint.Style.STROKE);
        // set stroke
        paint.setStrokeWidth(1.0f);

        // prepare a paint
        /*mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);

        // draw a rectangle
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL); //fill the background with blue color
        canvas.drawRect(0+10, 0+10, width-10, height-10, mPaint);*/


        // modify Slice number depending on max index slice

        final ViewTreeObserver vto = myPosition.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewHelpers.drawRadarSlice(myPosition, radius, canvas, paint);
            }
        });

        // set on ImageView or any other view
        BitmapDrawable ob = new BitmapDrawable(getActivity().getResources(), bitMap);
        radarForm.setBackgroundDrawable(ob);
    }

    public int getSliceIndex(Member memberArroundMe) {
        int j = GeneralHelpers.setSliceNumber();
        int sliceIndex = 0;

        while(j > 0 && memberArroundMe.distance <= GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER * j) {
            if(memberArroundMe.distance >= (j - 1) * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER &&
                    memberArroundMe.distance <= j * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER) {
                sliceIndex = j;
                break;
            }
            j -= 1;
        }

        return sliceIndex;
    }

    @Click(R.id.me)
    public void seeMyProfile() {
        GlobalVariables.finderRadar = true;
        GlobalVariables.isMenuOnPosition = false;
        GlobalVariables.MENU_POSITION = 5;

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(currentMember);
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);
        GlobalVariables.finderRadar = true;

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        if(!isAdded()) {
            View view = inflater.inflate(R.layout.fragment_people_finder, container, false);
            return view;
        }
        else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(bitMap != null) {
            bitMap.recycle();
            bitMap = null;
        }
    }
}
