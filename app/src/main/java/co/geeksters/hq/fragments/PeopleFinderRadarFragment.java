package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.ChangeToListEvent;
import co.geeksters.hq.events.success.MembersAroundMeEvent;
import co.geeksters.hq.events.success.RefreshRadarEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
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
    static int radarWidth = 0;
    static int radarHeight = 0;

    @ViewById(R.id.radarForm)
    RelativeLayout radarForm;

    @ViewById(R.id.radar_form_2km)
    RelativeLayout radarForm2km;

    @ViewById(R.id.radar_form_4km)
    RelativeLayout radarForm4km;

    @ViewById(R.id.radar_form_6km)
    RelativeLayout radarForm6km;

    @ViewById(R.id.loadingGif)
    LinearLayout loading;

    @ViewById(R.id.turn_location_layout)
    RelativeLayout turnLocationLayout;

    @ViewById(R.id.me)
    ImageView myPosition;
    @ViewById(R.id.me_turn_location)
    ImageView meTurnLocation;

    @ViewById(R.id.image_2_1)
    ImageView image21;
    @ViewById(R.id.image_2_2)
    ImageView image22;
    @ViewById(R.id.image_2_3)
    ImageView image23;
    @ViewById(R.id.image_2_4)
    ImageView image24;
    @ViewById(R.id.image_2_more)
    RelativeLayout image2More;
    @ViewById(R.id.text_2_more)
    TextView text2More;


    @ViewById(R.id.image_4_1)
    ImageView image41;
    @ViewById(R.id.image_4_2)
    ImageView image42;
    @ViewById(R.id.image_4_3)
    ImageView image43;
    @ViewById(R.id.image_4_4)
    ImageView image44;
    @ViewById(R.id.image_4_5)
    ImageView image45;
    @ViewById(R.id.image_4_more)
    RelativeLayout image4More;
    @ViewById(R.id.text_4_more)
    TextView text4More;



    @ViewById(R.id.image_6_1)
    ImageView image61;
    @ViewById(R.id.image_6_2)
    ImageView image62;
    @ViewById(R.id.image_6_3)
    ImageView image63;
    @ViewById(R.id.image_6_4)
    ImageView image64;
    @ViewById(R.id.image_6_5)
    ImageView image65;
    @ViewById(R.id.image_6_more)
    RelativeLayout image6More;
    @ViewById(R.id.text_6_more)
    TextView text6More;


    @Subscribe
    public void onRefreshRadarEvent(RefreshRadarEvent event){


            GlobalVariables.radarLock = false;
            if (GeneralHelpers.isInternetAvailable(getActivity())) {
                MemberService memberService = new MemberService(accessToken);
                //radarForm.setVisibility(View.INVISIBLE);
                //radarForm.setVisibility(View.INVISIBLE);
                turnLocationLayout.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            }


    }
    @AfterViews
    public void checkRadarActivation(){

        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), meTurnLocation, currentMember.image);
        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), myPosition, currentMember.image);

        if(!currentMember.radarVisibility) {
            radarForm.setVisibility(View.INVISIBLE);
            turnLocationLayout.setVisibility(View.VISIBLE);

        }
        else{
            radarForm.setVisibility(View.VISIBLE);
            turnLocationLayout.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
            listAllMembersAroundMeService();
        }


    }

    public void listAllMembersAroundMeService() {
        setBitmap = true;

        if(GlobalVariables.afterViewsRadar)
            finder = true;

        if(finder) {
            if (GeneralHelpers.isInternetAvailable(getActivity())) {
                //GlobalVariables.getPeopleAroundMe = true;
                BaseApplication.register(this);
                MemberService memberService = new MemberService(accessToken);
                memberService.getMembersArroundMe(currentMember.id, GlobalVariables.RADIUS);
            } else {
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
                loading.setVisibility(View.INVISIBLE);
            }

            GlobalVariables.afterViewsRadar = false;
            finder = false;
        }
    }



    @Subscribe
    public void onGetListMembersAroundMeEvent(MembersAroundMeEvent event) {


        if (BaseApplication.isRegistered(this))
            BaseApplication.unregister(this);

            radarForm.setVisibility(View.VISIBLE);
        turnLocationLayout.setVisibility(View.INVISIBLE);

        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), meTurnLocation, currentMember.image);
        ViewHelpers.setImageViewBackgroundFromURL(getActivity(), myPosition, currentMember.image);

        GlobalVariables.membersAroundMe = new ArrayList<Member>();
        GlobalVariables.membersAroundMe.addAll(Member.addMemberAroundMe(event.members));
        membersList.clear();
        membersList = Member.addMemberAroundMe(event.members);
        membersList = Member.orderMembersByDescDistance(membersList);

        final ArrayList<Member> list0to2 = new ArrayList<Member>();
        final ArrayList<Member> list2to4 = new ArrayList<Member>();
        final ArrayList<Member> list4to6 = new ArrayList<Member>();

        for(int i=0;i<membersList.size();i++) {

            if (membersList.get(i).distance >= 0 && membersList.get(i).distance <= 2) {
                list0to2.add(membersList.get(i));
            } else if (membersList.get(i).distance > 2 && membersList.get(i).distance <= 4) {
                list2to4.add(membersList.get(i));
            } else if (membersList.get(i).distance > 4 && membersList.get(i).distance <= 6) {

                list4to6.add(membersList.get(i));
            }
        }

        //.............................LIST 0 TO 2km.......................................................

           if(list0to2.size()>0)
           {
               ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image21, list0to2.get(0).image);
               image21.setVisibility(View.VISIBLE);
               image21.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       GlobalVariables.directory = true;
                       GlobalVariables.isMenuOnPosition = false;
                       GlobalVariables.MENU_POSITION = 5;

                       FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                       Fragment fragment = new OneProfileFragment_().newInstance(list0to2.get(0), 0);
                       fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                       fragmentTransaction.replace(R.id.contentFrame, fragment);
                       fragmentTransaction.commit();
                   }
               });

           }
            if(list0to2.size()>1)
            {
                ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image22, list0to2.get(1).image);
                image22.setVisibility(View.VISIBLE);
                image22.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalVariables.directory = true;
                        GlobalVariables.isMenuOnPosition = false;
                        GlobalVariables.MENU_POSITION = 5;

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new OneProfileFragment_().newInstance(list0to2.get(1), 0);
                        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                        fragmentTransaction.replace(R.id.contentFrame, fragment);
                        fragmentTransaction.commit();
                    }
                });

            }
            if(list0to2.size()>2)
            {
                ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image23, list0to2.get(2).image);
                image23.setVisibility(View.VISIBLE);
                image23.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalVariables.directory = true;
                        GlobalVariables.isMenuOnPosition = false;
                        GlobalVariables.MENU_POSITION = 5;

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new OneProfileFragment_().newInstance(list0to2.get(2), 0);
                        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                        fragmentTransaction.replace(R.id.contentFrame, fragment);
                        fragmentTransaction.commit();
                    }
                });

            }
            if(list0to2.size()>3)
            {
                ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image24, list0to2.get(3).image);
                image24.setVisibility(View.VISIBLE);
                image24.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalVariables.directory = true;
                        GlobalVariables.isMenuOnPosition = false;
                        GlobalVariables.MENU_POSITION = 5;

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new OneProfileFragment_().newInstance(list0to2.get(3), 0);
                        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                        fragmentTransaction.replace(R.id.contentFrame, fragment);
                        fragmentTransaction.commit();
                    }
                });

            }
            if(list0to2.size()>4)
            {
                text2More.setText(""+(list0to2.size()-4));
                image2More.setVisibility(View.VISIBLE);
                image2More.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseApplication.post(new ChangeToListEvent());
                    }
                });

            }


        //.............................LIST 2 TO 4km.......................................................

        if(list2to4.size()>0)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image41, list2to4.get(0).image);
            image41.setVisibility(View.VISIBLE);
            image41.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list2to4.get(0), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list2to4.size()>1)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image42, list2to4.get(1).image);
            image42.setVisibility(View.VISIBLE);
            image42.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list2to4.get(1), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list2to4.size()>2)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image43, list2to4.get(2).image);
            image43.setVisibility(View.VISIBLE);
            image43.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list2to4.get(2), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list2to4.size()>3)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image44, list2to4.get(3).image);
            image44.setVisibility(View.VISIBLE);
            image44.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list2to4.get(3), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }

        if(list2to4.size()>4)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image45, list2to4.get(4).image);
            image45.setVisibility(View.VISIBLE);
            image45.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list2to4.get(4), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list2to4.size()>5)
        {
            text4More.setText(""+(list2to4.size()-5));
            image4More.setVisibility(View.VISIBLE);
            image4More.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseApplication.post(new ChangeToListEvent());
                }
            });

        }



        //.............................LIST 4 TO 6km.......................................................

        if(list4to6.size()>0)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image61, list4to6.get(0).image);
            image61.setVisibility(View.VISIBLE);
            image61.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list4to6.get(0), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list4to6.size()>1)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image62, list4to6.get(1).image);
            image62.setVisibility(View.VISIBLE);
            image62.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list4to6.get(1), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list4to6.size()>2)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image63, list4to6.get(2).image);
            image63.setVisibility(View.VISIBLE);
            image63.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list4to6.get(2), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list4to6.size()>3)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image64, list4to6.get(3).image);
            image64.setVisibility(View.VISIBLE);
            image64.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list4to6.get(3), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }

        if(list4to6.size()>4)
        {
            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), image65, list4to6.get(4).image);
            image65.setVisibility(View.VISIBLE);
            image65.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.directory = true;
                    GlobalVariables.isMenuOnPosition = false;
                    GlobalVariables.MENU_POSITION = 5;

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(list4to6.get(4), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

        }
        if(list4to6.size()>5)
        {
            text6More.setText(""+(list2to4.size()-5));
            image6More.setVisibility(View.VISIBLE);
            image6More.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseApplication.post(new ChangeToListEvent());
                }
            });

        }

        GlobalVariables.radarLock = true;

        loading.setVisibility(View.INVISIBLE);

        BaseApplication.register(this);
    }




    @Click(R.id.me)
    public void seeMyProfile() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OneProfileFragment_().newInstance(currentMember, 0);
        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);
        View subview = null;

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"", "");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));


        float heightDp = GlobalVariables.height/GlobalVariables.d;

        if(heightDp < 580) {
            container.removeAllViewsInLayout();
             subview = inflater.inflate(R.layout.fragment_people_finder_radar_small_screen, container,false);
        }
        else if(heightDp < 800) {
            container.removeAllViewsInLayout();
            subview = inflater.inflate(R.layout.fragment_people_finder_radar, container, false);
        }
        else if(heightDp < 1000){

            container.removeAllViewsInLayout();
            subview = inflater.inflate(R.layout.fragment_people_finder_radar_tablet_7, container, false);

        }
        else {
            container.removeAllViewsInLayout();
            subview = inflater.inflate(R.layout.fragment_people_finder_radar_tablet_10, container, false);
        }
        return subview;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (BaseApplication.isRegistered(this))
            BaseApplication.unregister(this);

        if(bitMap != null) {
            bitMap.recycle();
            bitMap = null;
        }
    }
}