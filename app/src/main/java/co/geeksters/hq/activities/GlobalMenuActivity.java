package co.geeksters.hq.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.WebViewFragment;

public class GlobalMenuActivity extends ActionBarActivity {

	// Within which the entire activity is enclosed
	private DrawerLayout mDrawerLayout;

	// ListView represents Navigation Drawer
	private ListView mDrawerList;

	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the
	// action bar
	private ActionBarDrawerToggle mDrawerToggle;

	// Title of the action bar
	private String mTitle = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.global_menu);
        getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#308BD1")));
		mTitle = "Sandbox";
        getSupportActionBar().setTitle(mTitle);

		// ImageButton drawer_button = (ImageButton) findViewById(
		// R.id.action_settings );
		// setWhiteIcon(drawer_button);

		// ColorFilter filter = new LightingColorFilter(Color.RED, 1);
		// .setColorFilter(filter);

		// Getting reference to the DrawerLayout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.drawer_list);

		// Getting reference to the ActionBarDrawerToggle
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			// @Override
			// public boolean onOptionsItemSelected(MenuItem item) {
			// if (item != null && item.getItemId() == android.R.id.home) {
			// if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
			// mDrawerLayout.closeDrawer(Gravity.RIGHT);
			// } else {
			// mDrawerLayout.openDrawer(Gravity.RIGHT);
			// }
			// }
			// return false;
			// }

			/** Called when drawer is closed */
			public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();

			}

			/** Called when a drawer is opened */
			public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Sandbox");
                // getSupportActionBar().hide();
				invalidateOptionsMenu();
			}

		};

		// Setting DrawerToggle on DrawerLayout
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// mDrawerLayout.setAlpha(128);

		// Creating an ArrayAdapter to add items to the listview mDrawerList
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getBaseContext(), R.layout.drawer_list_item, getResources()
						.getStringArray(R.array.menus));

		// Setting the adapter on mDrawerList
		mDrawerList.setAdapter(adapter);

		// Enabling Home button
        getSupportActionBar().setHomeButtonEnabled(true);

		// Enabling Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Setting item click listener for the listview mDrawerList
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Getting an array of rivers
				String[] menuItems = getResources().getStringArray(
						R.array.menus);

				// Currently selected river
				mTitle = menuItems[position];

				// Creating a fragment object
				WebViewFragment rFragment = new WebViewFragment();

				// Passing selected item information to fragment
				Bundle data = new Bundle();
				data.putInt("position", position);
				data.putString("url", getUrl(position));
				rFragment.setArguments(data);

				// Getting reference to the FragmentManager
				FragmentManager fragmentManager = getSupportFragmentManager();

				// Creating a fragment transaction
				FragmentTransaction ft = fragmentManager.beginTransaction();

				// Adding a fragment to the fragment transaction
				ft.replace(R.id.content_frame, rFragment);

				// Committing the transaction
				ft.commit();

				// Closing the drawer
				mDrawerLayout.closeDrawer(mDrawerList);

			}
		});
	}

	protected String getUrl(int position) {
		switch (position) {
		case 0:
			return "http://javatechig.com";
		case 1:
			return "http://javatechig.com/category/android/";
		case 2:
			return "http://javatechig.com/category/blackberry/";
		case 3:
			return "http://javatechig.com/category/j2me/";
		case 4:
			return "http://javatechig.com/category/sencha-touch/";
		case 5:
			return "http://javatechig.com/category/phonegap/";
		case 6:
			return "http://javatechig.com/category/java/";
		default:
			return "http://javatechig.com";
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public void setWhiteIcon(ImageButton imageButton) {
		ColorFilter filter = new LightingColorFilter(Color.RED, 1);
		imageButton.setColorFilter(filter);
	}
}
