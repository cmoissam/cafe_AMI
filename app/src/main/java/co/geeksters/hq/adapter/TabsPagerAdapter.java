package co.geeksters.hq.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.geeksters.hq.fragments.OneProfileFragment;
import co.geeksters.hq.fragments.OneProfileInfoFragment;
import co.geeksters.hq.fragments.OneProfileMarketPlaceFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new OneProfileMarketPlaceFragment();
		case 1:
			// Games fragment activity
			return new OneProfileFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
