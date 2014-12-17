package co.geeksters.hq.activities;

import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.WebViewFragment;

@EActivity(R.layout.global_menu)
public class GlobalMenuActivity extends FragmentActivity {

	// Within which the entire activity is enclosed
    @ViewById
	DrawerLayout drawerLayout;

	// ListView represents Navigation Drawer
    @ViewById
	ListView drawerList;

	// ActionBarDrawerToggle indicates the presence of Navigation Drawer in the action bar
	private ActionBarDrawerToggle mDrawerToggle;

	// Title of the action bar
	private String mTitle = "HQ";

    @AfterViews
    public void setActionBarColorAndTitle(){
        getActionBar().setTitle(mTitle);
        // Enabling Home button
        getActionBar().setHomeButtonEnabled(true);
        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @AfterViews
    public void drawerLayoutSetting() {
        // Getting reference to the ActionBarDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle);
                // getSupportActionBar().hide();
                invalidateOptionsMenu();
            }
        };

        // Setting DrawerToggle on DrawerLayout
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @AfterViews
    public void drawerListSetting(){
        // Creating an ArrayAdapter to add items to the listview mDrawerList
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(), R.layout.drawer_list_item, getResources()
                .getStringArray(R.array.menus));

        // Setting the adapter on mDrawerList
        drawerList.setAdapter(adapter);
    }

    // Setting item click listener for the listview mDrawerList
    @ItemClick
    public void drawerListItemClicked(int position){
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
        rFragment.setArguments(data);

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction ft = fragmentManager.beginTransaction();

        // Adding a fragment to the fragment transaction
        ft.replace(R.id.contentFrame, rFragment);

        // Committing the transaction
        ft.commit();

        // Closing the drawer
        drawerLayout.closeDrawer(drawerList);
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
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);

		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
}
