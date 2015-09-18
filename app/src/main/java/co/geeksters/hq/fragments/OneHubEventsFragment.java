package co.geeksters.hq.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EFragment;

import co.geeksters.hq.R;
import co.geeksters.hq.models.Member;

@EFragment(R.layout.fragment_coming_soon)
public class OneHubEventsFragment extends Fragment {

    private static final String NEW_INSTANCE_MEMBER_KEY = "member_key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return null;
    }
}
