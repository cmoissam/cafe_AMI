package co.geeksters.hq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.events.success.DeleteMemberEvent;
import co.geeksters.hq.events.success.LogoutMemberEvent;
import co.geeksters.hq.events.success.MemberEvent;
import co.geeksters.hq.events.success.MembersByPaginationEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.fragments.HubsFragment;
import co.geeksters.hq.fragments.MeFragment_;
import co.geeksters.hq.fragments.OneProfileFragment_;
import co.geeksters.hq.fragments.OneProfileMarketPlaceFragment;
import co.geeksters.hq.fragments.MyToDosFragment;
import co.geeksters.hq.fragments.PeopleDirectoryFragment;
import co.geeksters.hq.fragments.PeopleDirectoryFragment_;
import co.geeksters.hq.fragments.PeopleFinderFragment;
import co.geeksters.hq.fragments.WebViewFragment;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelper;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.ParseHelper.createJsonElementFromString;

@EActivity(R.layout.global_menu)
public class GlobalMenuActivity extends FragmentActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Member currentMember;

    // ActionBarDrawerToggle indicates the presence of Navigation Drawer in the action bar
    private ActionBarDrawerToggle mDrawerToggle;

    // Title of the action bar
    private String mTitle = "HQ";

    String accessToken;

    @ViewById
    TextView noConnectionText;

    // Within which the entire activity is enclosed
    @ViewById
	DrawerLayout drawerLayout;

	// ListView represents Navigation Drawer
    @ViewById
	ListView drawerList;

    @ViewById(R.id.contentFrame)
    FrameLayout contentFrame;

    @AfterViews
    public void setPreferences(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");
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

    @AfterViews
    public void setActionBarColorAndTitle(){
        getActionBar().setTitle(mTitle);
        // Enabling Home button
        getActionBar().setHomeButtonEnabled(true);
        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @AfterViews
    public void setPreferencesAandDefaultFragment(){
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(!preferences.getString("current_member", "").equals("")) {
            currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
            // Adding a fragment to the fragment transaction
            if (currentMember.fullName.isEmpty() || currentMember.hub.name.isEmpty() || currentMember.companies == null || currentMember.goal.isEmpty() ||
                    currentMember.blurp.isEmpty() || currentMember.phone.isEmpty() || currentMember.interests == null || currentMember.social == null)
                fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());
            else fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());

        } else {
            fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
        }

        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @AfterViews
    public void busRegistration(){
        BaseApplication.register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }

    @Subscribe
    public void onLogoutEvent(LogoutMemberEvent event) {
        preferences.edit().clear().commit();

        Intent intent = new Intent(this, LoginActivity_.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Subscribe
    public void onDeleteEvent(DeleteMemberEvent event) {
        preferences.edit().clear().commit();

        Intent intent = new Intent(this, LoginActivity_.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Subscribe
    public void onSaveMemberEvent(MemberEvent event) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_save), Toast.LENGTH_LONG).show();

        // save the current Member
        editor.putString("current_member", ParseHelper.createJsonStringFromModel(event.member));
        editor.commit();

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Adding a fragment to the fragment transaction
        fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Adding a fragment to the fragment transaction
        if(position == 0) {
            //ViewHelpers.showProgress(true, this, contentFrame, membersSearchProgress);

            if(GeneralHelpers.isInternetAvailable(this)) {
                MemberService memberService = new MemberService(accessToken);
                memberService.listAllMembersByPaginationOrSearch(0, GlobalVariables.SEARCH_SIZE, "Asc", "full_name");
            } else {
                //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
                ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }
        } else if(position == 1) {
            fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment());
        } else if(position == 2){
            fragmentTransaction.replace(R.id.contentFrame, new HubsFragment());
        } else if(position == 3){
            fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment());
        } else if(position == 4){
            fragmentTransaction.replace(R.id.contentFrame, new OneProfileMarketPlaceFragment());
        } else if(position == 5){
            fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
        }

        // Committing the transaction
        fragmentTransaction.commit();

        // Closing the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Subscribe
    public void onGetListMembersByPaginationEvent(MembersEvent event) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = PeopleDirectoryFragment_.newInstance(event.members);
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.commit();
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
