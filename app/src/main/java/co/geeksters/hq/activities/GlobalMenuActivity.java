package co.geeksters.hq.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.MenuAdapter;
import co.geeksters.hq.events.failure.UnauthorizedFailureEvent;
import co.geeksters.hq.events.success.DeleteMemberEvent;
import co.geeksters.hq.events.success.EmptyEvent;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.RefreshRadarEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.fragments.HubsFragment;
import co.geeksters.hq.fragments.HubsFragment_;
import co.geeksters.hq.fragments.MarketPlaceFragment;
import co.geeksters.hq.fragments.MarketPlaceFragment_;
import co.geeksters.hq.fragments.MeFragment_;
import co.geeksters.hq.fragments.MyToDosFragment_;
import co.geeksters.hq.fragments.NewPostFragment;
import co.geeksters.hq.fragments.NewPostFragment_;
import co.geeksters.hq.fragments.NewTodoFragment;
import co.geeksters.hq.fragments.NewTodoFragment_;
import co.geeksters.hq.fragments.OneHubFragment_;
import co.geeksters.hq.fragments.OneHubMembersFragment_;
import co.geeksters.hq.fragments.OneProfileFragment;
import co.geeksters.hq.fragments.OneProfileFragment_;
import co.geeksters.hq.fragments.OneProfileMarketPlaceFragment;
import co.geeksters.hq.fragments.MyToDosFragment;
import co.geeksters.hq.fragments.PeopleDirectoryFragment_;
import co.geeksters.hq.fragments.PeopleFinderFragment_;
import co.geeksters.hq.fragments.WebViewFragment;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GPSTrackerHelpers;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.R.layout.menu_header;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EActivity(R.layout.global_menu)
public class GlobalMenuActivity extends FragmentActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Member currentMember;
    // ActionBarDrawerToggle indicates the presence of Navigation Drawer in the action bar
    private ActionBarDrawerToggle mDrawerToggle;
    // Title of the action bar
    private String mTitle = "Thousand Network";
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
    ImageButton menuList;

    @AfterViews
    public void setPreferences(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");
    }


    @AfterViews
    public void drawerLayoutSetting() {
        // Getting reference to the ActionBarDrawerToggle
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar);
        menuList = (ImageButton) getActionBar().getCustomView().findViewById(R.id.imageButton);
        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
                if(!drawerOpen){
                    drawerLayout.openDrawer(drawerList);
                }
                else{

                    drawerLayout.closeDrawer(drawerList);
                }
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.transparent, R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
                TextView titleView= (TextView) getActionBar().getCustomView().findViewById(R.id.mytitle);
                titleView.setText(mTitle);
                Typeface typeFace=Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
                titleView.setTypeface(typeFace);
            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {

                // getSupportActionBar().hide();
                invalidateOptionsMenu();
            }
        };

        // Setting DrawerToggle on DrawerLayout
        drawerLayout.setDrawerListener(mDrawerToggle);
    }


    @AfterViews
    public void drawerListSetting(){
//        Creating an ArrayAdapter to add items to the listview mDrawerList

        View header = (View)getLayoutInflater().inflate(R.layout.menu_header,null);
        View footer = (View)getLayoutInflater().inflate(R.layout.menu_footer, null);
        drawerList.addHeaderView(header,null,false);
        drawerList.addFooterView(footer,null,false);
        MenuAdapter menuAdapter = new MenuAdapter(this,getResources().getStringArray(R.array.menus), drawerList);
        // Setting the adapter on mDrawerList
        drawerList.setAdapter(menuAdapter);

    }

    @AfterViews
    public void setActionBarColorAndTitle() {

        getActionBar().setIcon(R.drawable.transparent);

        getActionBar().setTitle(mTitle);
        // Enabling Home button
        getActionBar().setHomeButtonEnabled(false);
        // Enabling Up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @AfterViews
    public void setPreferencesAndDefaultFragment() {
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        editor = preferences.edit();

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(GlobalVariables.notifiyedByPost) {
            fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());
        }
        else if(GlobalVariables.notifiyedByTodo){
            fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());
        }
        else {
            if (!GlobalVariables.isMenuOnPosition && !preferences.getString("current_member", "").equals("")) {
                currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
                // Adding a fragment to the fragment transaction
                if (currentMember.fullName.isEmpty() || currentMember.hub.name.isEmpty() || currentMember.companies == null || currentMember.goal.isEmpty() ||
                        currentMember.blurp.isEmpty() || currentMember.phone.isEmpty() || currentMember.interests == null || currentMember.social == null) {
//                GlobalVariables.isMenuOnPosition = true;
                    GlobalVariables.editMyInformation = false;
                    fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());
                } else {
                    GlobalVariables.fromPaginationDirectory = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
                }
            } else {
                if (!GlobalVariables.isMenuOnPosition) {
                    GlobalVariables.fromPaginationDirectory = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
                } else {
                    // Adding a fragment to the fragment transaction
                    if (GlobalVariables.MENU_POSITION == 0) {
                        mTitle = getResources().getString(R.string.title_directory_fragment);
                        GlobalVariables.fromPaginationDirectory = 0;

                        fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 1) {
                        mTitle = getResources().getString(R.string.title_find_fragment);
                        GlobalVariables.afterViewsRadar = true;

                        fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 2) {
                        fragmentTransaction.replace(R.id.contentFrame, new HubsFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 3) {
                        mTitle = getResources().getString(R.string.title_todos_fragment);

                        fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 4) {
                        MarketPlaceFragment_.defaultIndex = 0;
                        fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 5) {
                        mTitle = getResources().getString(R.string.title_me_fragment);

                        fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
                    }

                    getActionBar().setTitle(mTitle);
                }
            }
        }
        // Committing the transaction
        fragmentTransaction.commit();
    }

    public void updateLocation(){
            if (!GeneralHelpers.isGPSEnabled(this)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                verifyGpsActivation();
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                verifyGpsActivation();
            }
    }

    public void verifyGpsActivation() {
        GPSTrackerHelpers gps = new GPSTrackerHelpers(this);

        SharedPreferences preferences = this.getSharedPreferences("CurrentUser", this.MODE_PRIVATE);
        editor = preferences.edit();
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        Member updatedMember = currentMember;

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // update longitude latitude
            updatedMember.longitude = (float) longitude;
            updatedMember.latitude  = (float) latitude;
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            ViewHelpers.buildAlertMessageNoGps(this);
        }

        if (GeneralHelpers.isInternetAvailable(this)) {
            MemberService memberService = new MemberService(accessToken);
            memberService.updateMember(currentMember.id, updatedMember);
            GlobalVariables.updatePosition = true;
            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 1;
        } else {
            ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @Override
    public void onBackPressed() {
        if(GlobalVariables.isMenuOnPosition) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            // Creating a fragment transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);

            // MeFragment : GlobalVariables.MENU_POSITION == 6
            // OneHubFragment : GlobalVariables.MENU_POSITION == 7
            if(GlobalVariables.MENU_POSITION == 6 && !GlobalVariables.editMyInformation) {
                setResult(RESULT_CANCELED);
                finish();
            } else if(GlobalVariables.MENU_POSITION == 6 && GlobalVariables.editMyInformation) {
                GlobalVariables.MENU_POSITION = 5;
                GlobalVariables.isMenuOnPosition = true;
                fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
            } else if(GlobalVariables.MENU_POSITION == 5) {
                if(GlobalVariables.directory) {
                    GlobalVariables.directory = false;
                    GlobalVariables.MENU_POSITION = 0;
                    GlobalVariables.isMenuOnPosition = true;
                    GlobalVariables.fromPaginationDirectory = 0;
                    GlobalVariables.fromPaginationDirectory = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
                } else if(GlobalVariables.finderRadar) {
                    GlobalVariables.finderRadar = false;
                    GlobalVariables.MENU_POSITION = 1;
                    GlobalVariables.isMenuOnPosition = true;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                } else if(GlobalVariables.finderList) {
                    GlobalVariables.finderList = false;
                    GlobalVariables.MENU_POSITION = 1;
                    GlobalVariables.isMenuOnPosition = true;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                } else if(GlobalVariables.hubMember) {
                    GlobalVariables.hubMember = false;
                    Hub hub = Hub.createHubFromJson(createJsonElementFromString(preferences.getString("current_hub", "")));
                    GlobalVariables.MENU_POSITION = 7;
                    fragmentTransaction.replace(R.id.contentFrame, new OneHubFragment_().newInstance(hub));
                }
            } else if(GlobalVariables.MENU_POSITION == 7 && GlobalVariables.hubInformation) {
                GlobalVariables.hubInformation = false;
                GlobalVariables.MENU_POSITION = 2;
                GlobalVariables.isMenuOnPosition = true;
                fragmentTransaction.replace(R.id.contentFrame, new HubsFragment_());
            } else if(GlobalVariables.MENU_POSITION == 8) {
                if(GlobalVariables.replyFromMyMarket) {
                    GlobalVariables.MENU_POSITION = 5;
                    GlobalVariables.isMenuOnPosition = true;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(currentMember, 1));
                } else {
                    GlobalVariables.MENU_POSITION = 4;
                    GlobalVariables.isMenuOnPosition = true;
                    GlobalVariables.inMarketPlaceFragment = true;

                    if(GlobalVariables.replyToAll) {
                        fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_().newInstance(currentMember, 0));
                    }
                    else {
                        fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_().newInstance(currentMember, 1));
                    }

                    invalidateOptionsMenu();
                }
            } else if(GlobalVariables.MENU_POSITION == 9) {
                GlobalVariables.MENU_POSITION = 4;
                GlobalVariables.isMenuOnPosition = true;
                GlobalVariables.inMarketPlaceFragment = true;

                if(GlobalVariables.replyToAll)
                    fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_().newInstance(currentMember, 0));
                else
                    fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_().newInstance(currentMember, 1));

                invalidateOptionsMenu();
            }  else if(GlobalVariables.MENU_POSITION == 10) {
                GlobalVariables.MENU_POSITION = 3;
                GlobalVariables.isMenuOnPosition = true;
                GlobalVariables.inMyTodosFragment = true;

                fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());

                invalidateOptionsMenu();
            }

            fragmentTransaction.commit();
        }
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
     // TODO EMPTY EVENT IN GLOBAL MENU ACTIVTY
    @Subscribe
    public void onLogoutEvent(EmptyEvent event) {
        // preferences.edit().clear().commit();
        preferences.edit().remove("current_member").commit();
        preferences.edit().remove("access_token").commit();

        GlobalVariables.isMenuOnPosition = false;

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

    // Setting item click listener for the listview mDrawerList
    @ItemClick
    public void drawerListItemClicked(int position) {
        // Getting an array of rivers
        String[] menuItems = getResources().getStringArray(
                R.array.menus);

        // Currently selected river
        mTitle = menuItems[position-1];

        GlobalVariables.isMenuOnPosition = true;

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
        position = position - 1;

        // Adding a fragment to the fragment transaction
        if(position == 0) {
            mTitle = getResources().getString(R.string.title_directory_fragment);
            GlobalVariables.fromPaginationDirectory = 0;

            fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
        } else if(position == 1) {
            mTitle = getResources().getString(R.string.title_find_fragment);

            if(currentMember.radarVisibility) {
                updateLocation();
            }
            else fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
            GlobalVariables.inRadarFragement = true;

        } else if(position == 2) {
            fragmentTransaction.replace(R.id.contentFrame, new HubsFragment_());
        } else if(position == 3) {
            mTitle = getResources().getString(R.string.title_todos_fragment);
            fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());
        } else if(position == 4) {
            MarketPlaceFragment_.defaultIndex = 0;
            fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());
        } else if(position == 5) {
            mTitle = getResources().getString(R.string.title_me_fragment);

            fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
        }

        // Committing the transaction
        fragmentTransaction.commit();

        // Closing the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    @Subscribe
    public void onSaveLocationMemberEvent(SaveMemberEvent event) {
        if (!GlobalVariables.updatePositionFromRadar) {
            if (GlobalVariables.MENU_POSITION == 1) {
                // save the current Member
                editor.putString("current_member", ParseHelpers.createJsonStringFromModel(event.member));
                editor.commit();

                GlobalVariables.afterViewsRadar = true;

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                GlobalVariables.inRadarFragement = true;
                fragmentTransaction.commit();
            } else if (GlobalVariables.MENU_POSITION == 6) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_());
                fragmentTransaction.commit();
            }
        }
    }

    public void onAddPostPressed(){
        GlobalVariables.MENU_POSITION = 9;
        GlobalVariables.isMenuOnPosition = false;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.contentFrame, new NewPostFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    public void onAddTodoPressed(){
        GlobalVariables.MENU_POSITION = 10;
        GlobalVariables.isMenuOnPosition = false;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.contentFrame, new NewTodoFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    public void onRefreshRadarPressed(){

        BaseApplication.post(new RefreshRadarEvent());

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

        if (item.getItemId() == R.id.action_add)
        {
            if(GlobalVariables.inMarketPlaceFragment)
                onAddPostPressed();
            if(GlobalVariables.inMyTodosFragment)
                onAddTodoPressed();
            if(GlobalVariables.inRadarFragement)
                onRefreshRadarPressed();
        }

		return super.onOptionsItemSelected(item);
	}

	/** Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);

        if(GlobalVariables.inMarketPlaceFragment || GlobalVariables.inMyTodosFragment) {
            menu.findItem(R.id.action_add).setVisible(!drawerOpen);
        }
        else
            if(GlobalVariables.inRadarFragement)
            {
                menu.findItem(R.id.action_add).setIcon(R.drawable.refresh);
                menu.findItem(R.id.action_add).setVisible(!drawerOpen);
            }
            else menu.findItem(R.id.action_add).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)
        {
            case RESULT_CANCELED:
                setResult(RESULT_CANCELED);
                finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onUnauthorizedEvent(UnauthorizedFailureEvent event){

        GlobalVariables.sessionExpired = true;
        Intent intent = new Intent(this, LoginActivity_.class);
        finish();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
