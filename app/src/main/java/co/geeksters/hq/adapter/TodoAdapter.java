package co.geeksters.hq.adapter;

import android.content.Context;
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
public class TodoAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<Member> memberList = new ArrayList<Member>();
    private ListView listViewMembers;
    private static LayoutInflater inflater = null;
    public List<Member> concernedMembers = new ArrayList<Member>();

    public TodoAdapter(FragmentActivity activity, List<Member> memberList, ListView listViewMembers ,List<Member> concernedMembers) {
        this.activity = activity;
        this.memberList = memberList;
        this.listViewMembers = listViewMembers;
        this.concernedMembers = concernedMembers;

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

    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = inflater.inflate(R.layout.list_item_todo_members, null);

        TextView fullName = (TextView) view.findViewById(R.id.fullName);
        TextView hubName = (TextView) view.findViewById(R.id.hubName);
       // TextView distance = (TextView) view.findViewById(R.id.distance);
        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        final ImageView deleteOrAdd = (ImageView) view.findViewById(R.id.add_delete_image);
        boolean added = false;

        for(int i=0;i<concernedMembers.size();i++)
        {
            if (memberList.get(position).id == concernedMembers.get(i).id)
            {
                added = true;
                deleteOrAdd.setBackgroundResource(R.drawable.delete);
            }
        }

        fullName.setText(GeneralHelpers.firstToUpper(memberList.get(position).fullName));
        if(memberList.get(position).hub != null && !memberList.get(position).hub.name.equals(""))
            hubName.setText(GeneralHelpers.firstToUpper(memberList.get(position).hub.name));
        else
            hubName.setText(GeneralHelpers.firstToUpper(activity.getResources().getString(R.string.empty_hub_name)));

        ViewHelpers.setImageViewBackgroundFromURL(activity, picture, memberList.get(position).image);
        final boolean addedCopy = added;

        deleteOrAdd.setOnClickListener(new View.OnClickListener() {
            Boolean added = addedCopy;
            @Override
           public void onClick(View v) {
               if (added) {
                   deleteOrAdd.setBackgroundResource(R.drawable.add1);
                   concernedMembers.remove(memberList.get(position));
                   added = false;
               } else {
                   deleteOrAdd.setBackgroundResource(R.drawable.delete);
                   concernedMembers.add(memberList.get(position));
                   added = true;
               }
           }
        });

      /*  view.setOnClickListener(new View.OnClickListener() {
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
        });*/

        return view;
    }
}