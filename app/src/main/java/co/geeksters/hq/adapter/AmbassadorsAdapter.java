package co.geeksters.hq.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.OneProfileFragment_;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 04/02/15.
 */
public class AmbassadorsAdapter {

    Fragment context;
    ImageView picture1,picture2,picture3,picture4,picture5;
    Hub hub;
    List<Member> ambassadors;
    LayoutInflater inflaterFromFragment;

    public AmbassadorsAdapter(Fragment context, Hub hub, LayoutInflater inflater,ImageView picture1,ImageView picture2,ImageView picture3,ImageView picture4,ImageView picture5) {
        this.context = context;
        this.hub = hub;
        this.ambassadors=hub.ambassadors;
        this.inflaterFromFragment = inflater;
        this.picture1 = picture1;
        this.picture2 = picture2;
        this.picture3 = picture3;
        this.picture4 = picture4;
        this.picture5 = picture5;

    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(inflaterFromFragment.getContext());

        LinearLayout llList = (LinearLayout) ((Activity)inflaterFromFragment.getContext()).findViewById(R.id.ambassadors_layout_list);
        llList.removeAllViews();

        if(ambassadors.size() >= 1)
        {
            picture1.setVisibility(View.VISIBLE);
            if(ambassadors.get(0).image != null && ambassadors.get(0).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture1, ambassadors.get(0).image);
            picture1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(0).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(0), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 2)
        {
            picture2.setVisibility(View.VISIBLE);
            if(ambassadors.get(1).image != null && ambassadors.get(1).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture2, ambassadors.get(1).image);
            picture2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(1).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(1), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 3)
        {
            picture3.setVisibility(View.VISIBLE);
            if(ambassadors.get(2).image != null && ambassadors.get(2).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture3, ambassadors.get(2).image);
            picture3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(3).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(3), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 4)
        {
            picture4.setVisibility(View.VISIBLE);
            if(ambassadors.get(3).image != null && ambassadors.get(3).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture4, ambassadors.get(3).image);
            picture4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(4).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(4), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }
        if(ambassadors.size() >= 5)
        {
            picture5.setVisibility(View.VISIBLE);
            if(ambassadors.get(4).image != null && ambassadors.get(4).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), picture5, ambassadors.get(4).image);
            picture5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(5).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(5), 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });
        }


        }

}