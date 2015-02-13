package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.HashMap;
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
    Hub hub;
    List<Member> ambassadors;
    LayoutInflater inflaterFromFragment;

    public AmbassadorsAdapter(Fragment context, Hub hub, LayoutInflater inflater) {
        this.context = context;
        this.hub = hub;
        this.ambassadors=hub.ambassadors;
        this.inflaterFromFragment = inflater;
    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(inflaterFromFragment.getContext());

        LinearLayout llList = (LinearLayout) ((Activity)inflaterFromFragment.getContext()).findViewById(R.id.ambassadors);
        llList.removeAllViews();

        for(int i = 0 ; i < ambassadors.size() ; i++) {
            View childView = inflater.inflate(R.layout.list_item_ambassadors, null); //same layout you gave to the adapter

            ImageView pictureMember = (ImageView) childView.findViewById(R.id.picture);
            if(ambassadors.get(i).image != null && ambassadors.get(i).image.startsWith("http"))
                ViewHelpers.setImageViewBackgroundFromURL(context.getActivity(), pictureMember, ambassadors.get(i).image);

            final int index = i;
            pictureMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = context.getActivity().getSupportFragmentManager().beginTransaction();
                    ambassadors.get(index).hub.name = hub.name;
                    Fragment fragment = new OneProfileFragment_().newInstance(ambassadors.get(index));
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            llList.addView(childView);
        }
    }
}