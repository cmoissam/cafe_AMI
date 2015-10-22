package co.geeksters.hq.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.OneHubFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;

import static co.geeksters.hq.models.Hub.getHubsByAlphabeticalOrder;

/**
 * Created by soukaina on 12/01/15.
 */
public class ListViewHubAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<Hub> hubsList = new ArrayList<Hub>();
    private List<Hub> lastHubs = new ArrayList<Hub>();
    private ListView listViewHubs;
    private static LayoutInflater inflater = null;

    public ListViewHubAdapter(FragmentActivity activity, List<Hub> hubsList, List<Hub> lastHubs, ListView listViewHubs) {
        this.activity = activity;
        this.hubsList = hubsList;
        this.lastHubs = lastHubs;
        this.listViewHubs = listViewHubs;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return hubsList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(R.layout.list_item_hub, null);

        Hub hub = hubsList.get(position);

        TextView hubName = (TextView) view.findViewById(R.id.hubName);
        TextView hubMembersNumber = (TextView) view.findViewById(R.id.membersNumber);
        LinearLayout removeItem = (LinearLayout) view.findViewById(R.id.removeItem);
        RelativeLayout hubInformation = (RelativeLayout) view.findViewById(R.id.hubInformation);

        Typeface typeFace=Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        hubName.setTypeface(typeFace);
        hubMembersNumber.setTypeface(typeFace);

        hubName.setText(GeneralHelpers.firstToUpper(hub.name));
        hubMembersNumber.setText(hub.members.size() + " Members");

        for(int i=0;i<lastHubs.size();i++)
        {
            if (lastHubs.get(i).id == hub.id){
                removeItem.setVisibility(View.VISIBLE);
                break;
            }
        }

/*        if (lastHubs.contains(hub) && (position >= 0 && position < lastHubs.size())) {
            // set removeItem button to visible
            removeItem.setVisibility(View.VISIBLE);
        }*/

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = activity.getSharedPreferences("CurrentUser", activity.MODE_PRIVATE);
                // GeneralHelpers.getPreferencesPositionFromItemPosition(position)
                int id = hubsList.get(position).id;
                preferences.edit().remove("last_hub" + hubsList.get(position).id).commit();

                lastHubs.remove(position);
                //hubsList.add(lastHubs.get(position));
                List<Hub> orderedHubList = new ArrayList<Hub>();
                orderedHubList.addAll(getHubsByAlphabeticalOrder(hubsList));
                for (int i = 0; i < lastHubs.size(); i++) {
                    for(int j =0;j<orderedHubList.size();j++)
                    {
                        if(orderedHubList.get(j).id == lastHubs.get(i).id)
                        {
                            orderedHubList.remove(j);
                            break;
                        }
                    }
                }
                hubsList.clear();
                hubsList = Hub.concatenateTwoListsOfHubs(lastHubs, orderedHubList);

                ListViewHubAdapter adapterForHubList = new ListViewHubAdapter(activity, hubsList, lastHubs, listViewHubs);
                listViewHubs.setAdapter(adapterForHubList);
                ViewHelpers.setListViewHeightBasedOnChildren(listViewHubs);
                Toast.makeText(activity, "Remove Item", Toast.LENGTH_LONG).show();
            }
        });

        hubInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save this hub as a last search
                hubsList.get(position).saveLastHub(activity);
                GlobalVariables.hubInformation = true;
                GlobalVariables.isMenuOnPosition = false;
                GlobalVariables.MENU_POSITION = 7;

                SharedPreferences preferences = activity.getSharedPreferences("CurrentUser", activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current_hub", ParseHelpers.createJsonStringFromModel(hubsList.get(position)));
                editor.commit();

                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                Fragment fragment = new OneHubFragment_().newInstance(hubsList.get(position));
                fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}