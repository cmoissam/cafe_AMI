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
public class PostsAdapter {

    Context context;
    private ArrayList<HashMap<String, String>> data;

    public PostsAdapter(Context c, ArrayList<HashMap<String, String>> d) {
        context = c;
        data=d;
    }

    public void makeList() {
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout llList = (LinearLayout) ((Activity)context).findViewById(R.id.postsMarket);

        for(int i = 0 ; i < data.size() ; i++) {
            View childView = inflater.inflate(R.layout.list_item_post, null); //same layout you gave to the adapter

            LinearLayout commentDisplay = (LinearLayout) childView.findViewById(R.id.commentDisplay);
            commentDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            TextView postTextView = (TextView)childView.findViewById(R.id.post);
            postTextView.setText(data.get(i).get("text"));
            TextView commentSizeTextView = (TextView)childView.findViewById(R.id.commentsSize);
            commentSizeTextView.setText(data.get(i).get("comments_size") + " comments");

            llList.addView(childView);
        }
    }
}