package co.geeksters.hq.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.OneHubFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.models.Post;

/**
 * Created by soukaina on 12/01/15.
 */
public class ListViewMarketAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private List<Post> postsList = new ArrayList<Post>();
    private ListView listViewMarket;
    private static LayoutInflater inflater = null;

    public ListViewMarketAdapter(FragmentActivity activity, List<Post> postsList, ListView listViewMarket) {
        this.activity = activity;
        this.postsList = postsList;
        this.listViewMarket = listViewMarket;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return postsList.size();
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
            view = inflater.inflate(R.layout.list_item_post, null);

        Post post = postsList.get(position);

        TextView postContent = (TextView) view.findViewById(R.id.post);
        TextView commentsSize = (TextView) view.findViewById(R.id.commentsSize);

        postContent.setText(post.content);
        commentsSize.setText(post.comments.size() + " Members");

        GlobalVariables.listViewPostsHeight += view.getHeight();

        return view;
    }
}