package co.geeksters.hq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.OneProfileFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 12/01/15.
 */
public class FinderListAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<Member> memberList = new ArrayList<Member>();
    private static LayoutInflater inflater = null;

    public FinderListAdapter(FragmentActivity activity, List<Member> memberList, ListView listViewMembers) {
        this.activity = activity;
        this.memberList = memberList;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return memberList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = inflater.inflate(R.layout.list_item_people_finder, null);

        TextView fullName = (TextView) view.findViewById(R.id.fullName);
        TextView hubName = (TextView) view.findViewById(R.id.hubName);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        ImageView picture = (ImageView) view.findViewById(R.id.picture);

        Typeface typeFace=Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        fullName.setTypeface(typeFace);
        hubName.setTypeface(typeFace);
        distance.setTypeface(typeFace);

        fullName.setText(GeneralHelpers.firstToUpper(memberList.get(position).fullName));
        if(memberList.get(position).hub != null && !memberList.get(position).hub.name.equals(""))
            hubName.setText(GeneralHelpers.firstToUpper(memberList.get(position).hub.name));
        else
            hubName.setText(GeneralHelpers.firstToUpper(activity.getResources().getString(R.string.empty_hub_name)));

        String distanceString = GeneralHelpers.distanceByInterval(memberList.get(position).distance);

        distance.setText(distanceString);


        if(distanceString.equalsIgnoreCase("0-2km"))
            distance.setTextColor(Color.parseColor("#FF8F3E42"));
        if(distanceString.equalsIgnoreCase("2-4km"))
            distance.setTextColor(Color.parseColor("#FF282947"));
        if(distanceString.equalsIgnoreCase("4-6km"))
            distance.setTextColor(Color.parseColor("#FF2F6B6C"));



                ViewHelpers.setImageViewBackgroundFromURL(activity, picture, memberList.get(position).image);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.directory = true;
                GlobalVariables.isMenuOnPosition = false;
                GlobalVariables.MENU_POSITION = 5;

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                Fragment fragment = new OneProfileFragment_().newInstance(memberList.get(position), 0);
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}