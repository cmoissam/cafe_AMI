package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.geeksters.hq.R;

/**
 * Created by soukaina on 04/02/15.
 */
public class CommentsAdapter {

    Context context;
    private ArrayList<HashMap<String, String>> data;

    public CommentsAdapter(Context c, ArrayList<HashMap<String, String>> d) {
        context = c;
        data=d;
    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout llList = (LinearLayout) ((Activity)context).findViewById(R.id.commentsMarket);

        for(int i = 0 ; i < data.size() ; i++) {
            View childView = inflater.inflate(R.layout.list_item_comment, null); //same layout you gave to the adapter

            TextView fullNameTextView = (TextView)childView.findViewById(R.id.fullName);
            fullNameTextView.setText(data.get(i).get("full_name"));
            TextView commentTextView = (TextView)childView.findViewById(R.id.comment);
            commentTextView.setText(data.get(i).get("text"));
            TextView date = (TextView)childView.findViewById(R.id.date);
            date.setText(data.get(i).get("date"));

            llList.addView(childView);
        }
    }
}