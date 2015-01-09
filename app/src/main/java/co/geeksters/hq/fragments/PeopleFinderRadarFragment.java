package co.geeksters.hq.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.MyMapFragment;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_people_finder_radar)
public class PeopleFinderRadarFragment extends Fragment {

    private static GoogleMap map;
    private static View view;
    MapFragment mMapFragment;
    ViewGroup.LayoutParams params;
    String accessToken;
    static Member currentMember;

    public void listAllMembersAroundMeService(){
        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            MemberService memberService = new MemberService(accessToken);
            memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listAllMembersAroundMe(){
        listAllMembersAroundMeService();
    }

    @Subscribe
    public void onGetListMembersAroundMeEvent(MembersEvent event) {
        BitmapDrawable icon= (BitmapDrawable) getResources().getDrawable(R.drawable.no_image_member);
        Bitmap bitmap = icon.getBitmap();
        Bitmap bitmapIcon=Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/3,bitmap.getHeight()/3, false);

        for (int i = 0; i < event.members.size(); i++) {
            /*map.addMarker(new MarkerOptions().position(new LatLng(event.members.get(i).latitude, event.members.get(i).longitude)).title(event.members.get(i).fullName))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));*/

            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(event.members.get(i).latitude, event.members.get(i).longitude))
                    .title(event.members.get(i).fullName)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapIcon));

            map.addMarker(markerOptions);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.fragment_people_finder_radar, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        map = ((MyMapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(currentMember.latitude, currentMember.longitude);
        CameraUpdate center= CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(13);

        map.addMarker(new MarkerOptions().position(latLng).title(getResources().getString(R.string.my_position)));
        map.moveCamera(center);
        map.animateCamera(zoom);

        mMapFragment = (MyMapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        params = mMapFragment.getView().getLayoutParams();

        //Display display = getActivity().getWindowManager().getDefaultDisplay();

        /*Point size = new Point();
        try {
            display.getRealSize(size);
            params.height = size.y;
        } catch (NoSuchMethodError e) {*/

        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int rotation = display.getRotation();
        int rotation = getResources().getConfiguration().orientation;

        if(rotation == Configuration.ORIENTATION_PORTRAIT)
            params.height = display.getHeight()-182;
        else if(rotation == Configuration.ORIENTATION_LANDSCAPE)
            params.height = display.getHeight()-158;

        /*}*/

        mMapFragment.getView().setLayoutParams(params);
    }
}
