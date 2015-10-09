package co.geeksters.hq.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.Touch;

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
public class MenuAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private String [] titleList = new String[5];
    private ListView drawerList;
    private static LayoutInflater inflater = null;

    public MenuAdapter(FragmentActivity activity, String [] titles, ListView drawerList) {
        this.activity = activity;
        this.titleList = titles;
        this.drawerList = drawerList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return titleList.length;
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
            view = inflater.inflate(R.layout.drawer_list_item, null);


        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView titlePicture = (ImageView) view.findViewById(R.id.image_cell);

        title.setText(titleList[position]);
        Typeface typeFace=Typeface.createFromAsset(this.activity.getAssets(), "fonts/OpenSans-Regular.ttf");
        title.setTypeface(typeFace);

        if (titleList[position].equals("meet the family"))
        { titlePicture.setBackgroundResource(R.drawable.menu_meetthefamily_125x125);}
        else if(titleList[position].equals("search members")){titlePicture.setBackgroundResource(R.drawable.menu_search_125x125);}
        else if(titleList[position].equals("hubs")){titlePicture.setBackgroundResource(R.drawable.menu_hubs_125x125);}
        else if(titleList[position].equals("action items")){titlePicture.setBackgroundResource(R.drawable.menu_action_125x125);}
        else if(titleList[position].equals("opportunities")){titlePicture.setBackgroundResource(R.drawable.menu_opportunities_125x125);}
        else if(titleList[position].equals("my profile")){titlePicture.setBackgroundResource(R.drawable.menu_me_125x125);}

        /*view.setOnTouchListener(
        new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                int action = event.getAction();
                if (action==MotionEvent.ACTION_DOWN)
                {
                    myView.setBackgroundColor(Color.parseColor("#89c4c7"));
                }
                else
                    myView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                return false;
            }
        }
        );*/
    return view;
    }
}