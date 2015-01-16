package co.geeksters.hq.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.EFragment;

import co.geeksters.hq.R;
import co.geeksters.hq.models.Member;

@EFragment(R.layout.fragment_market_place)
public class OneHubNewsFragment extends Fragment {

    private static final String NEW_INSTANCE_MEMBER_KEY = "member_key";
    static Boolean seeProfile = false;

    public static OneProfileMarketPlaceFragment_ newInstance(Member member) {
        seeProfile = true;

        OneProfileMarketPlaceFragment_ fragment = new OneProfileMarketPlaceFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_MEMBER_KEY, member);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return null;
    }
}
