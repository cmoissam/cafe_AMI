package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 12/01/15.
 */
public class ListViewHubAdapter extends BaseAdapter {

    private Activity activity;
    private List<Hub> hubsList = new ArrayList<Hub>();
    private List<Hub> lastHubs = new ArrayList<Hub>();
    private static LayoutInflater inflater = null;

    public ListViewHubAdapter(Activity activity, List<Hub> hubsList, List<Hub> lastHubs) {
        this.activity = activity;
        this.hubsList = hubsList;
        this.lastHubs = lastHubs;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(R.layout.list_item_hub, null);

        Hub hub = hubsList.get(position);

        TextView hubName = (TextView) view.findViewById(R.id.hubName);
        TextView hubMembersNumber = (TextView) view.findViewById(R.id.membersNumber);
        LinearLayout removeItem = (LinearLayout) view.findViewById(R.id.removeItem);

        hubName.setText(GeneralHelpers.firstToUpper(hub.name));
        hubMembersNumber.setText(hub.members.size() + " Members");

        if (lastHubs.contains(hub) && (position >= 0 && position < lastHubs.size())) {
            // set removeItem button to visible
            removeItem.setVisibility(View.VISIBLE);
        }

        return view;
    }

}