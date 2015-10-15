package co.geeksters.hq.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import co.geeksters.hq.fragments.PeopleFinderFragment_;;
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

    ImageButton addButton;

    @AfterViews
    public void setPreferences(){
        SharedPreferences preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        accessToken = preferences.getString("access_token","").replace("\"","");

        GlobalVariables.d = getResources().getDisplayMetrics().density;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        GlobalVariables.width = size.x;
        GlobalVariables.height = size.y;
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

        addButton = (ImageButton) getActionBar().getCustomView().findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalVariables.inMarketPlaceFragment)
                    onAddPostPressed();
                else if (GlobalVariables.inMyTodosFragment)
                    onAddTodoPressed();
                else if (GlobalVariables.inRadarFragement)
                    onRefreshRadarPressed();
                else if (GlobalVariables.inMyProfileFragment)
                    onEditProfilePressed();
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.transparent, R.string.drawer_open,
                R.string.drawer_close) {

            /** Called when drawer is closed */
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
               setActionBarTitle(mTitle);

            }

            /** Called when a drawer is opened */
            public void onDrawerOpened(View drawerView) {

                // getSupportActionBar().hide();
                addButton.setVisibility(View.INVISIBLE);
                invalidateOptionsMenu();
            }
        };

        // Setting DrawerToggle on DrawerLayout
        drawerLayout.setDrawerListener(mDrawerToggle);
    }
    public void setActionBarTitle(String title){

        TextView titleView = (TextView) getActionBar().getCustomView().findViewById(R.id.mytitle);
        titleView.setText(title);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        titleView.setTypeface(typeFace);

        if(GlobalVariables.needReturnButton)
        {
            menuList.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_arrow_left)));
            menuList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();

                }
            });


        }
        else {
            menuList.setBackgroundDrawable(getResources().getDrawable(R.drawable.topmenu_list));
            menuList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
                    if (!drawerOpen) {
                        menuList.setBackgroundDrawable(getResources().getDrawable(R.drawable.topmenu_list));
                        drawerLayout.openDrawer(drawerList);
                    } else {

                        drawerLayout.closeDrawer(drawerList);
                    }
                }
            });
        }

        if (GlobalVariables.inMarketPlaceFragment || GlobalVariables.inMyTodosFragment || GlobalVariables.inRadarFragement || GlobalVariables.inMyProfileFragment) {

            if (GlobalVariables.inMarketPlaceFragment || GlobalVariables.inMyTodosFragment) {
                addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_plus)));
                addButton.setVisibility(View.VISIBLE);
            }
            if (GlobalVariables.inRadarFragement) {
                addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_refresh)));
                addButton.setVisibility(View.VISIBLE);
            }
            if (GlobalVariables.inMyProfileFragment) {
                addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_pencil)));
                addButton.setVisibility(View.VISIBLE);
            }
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }
    public void setActionBarIconVisibility(Boolean visibility){

        if(visibility)
            addButton.setVisibility(View.VISIBLE);
        else
            addButton.setVisibility(View.INVISIBLE);

    }

    @AfterViews
    public void drawerListSetting(){
//        Creating an ArrayAdapter to add items to the listview mDrawerList

        View header = (View)getLayoutInflater().inflate(R.layout.menu_header,null);
        View footer = (View)getLayoutInflater().inflate(R.layout.menu_footer, null);
        drawerList.addHeaderView(header, null, false);
        drawerList.addFooterView(footer, null, false);
        MenuAdapter menuAdapter = new MenuAdapter(this,getResources().getStringArray(R.array.menus), drawerList);
        // Setting the adapter on mDrawerList
        drawerList.setAdapter(menuAdapter);

    }

    @AfterViews
    public void setActionBarColorAndTitle() {

        getActionBar().setIcon(R.drawable.topmenu_list);

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
            mTitle = getResources().getString(R.string.title_market_place);
            fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());
        }
        else if(GlobalVariables.notifiyedByTodo){
            mTitle = getResources().getString(R.string.title_todos_fragment);
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
                    mTitle = getResources().getString(R.string.title_me_fragment);
                    fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());
                } else {
                    GlobalVariables.fromPaginationDirectory = 0;
                    mTitle = getResources().getString(R.string.title_directory_fragment);
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());
                }
            } else {
                if (!GlobalVariables.isMenuOnPosition) {
                    mTitle = getResources().getString(R.string.title_directory_fragment);
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
                        GlobalVariables.inRadarFragement = true;

                        fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                    } else if (GlobalVariables.MENU_POSITION == 2) {
                        mTitle = getResources().getString(R.string.title_hubs);
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

                LayoutInflater inflater = this.getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.exit_pop_up, null);
                TextView infoTitle = (TextView) dialoglayout.findViewById(R.id.infoTitle);
                TextView infotext = (TextView) dialoglayout.findViewById(R.id.infoText);
                ImageView infoimage = (ImageView) dialoglayout.findViewById(R.id.infoImage);
                Button no = (Button)dialoglayout.findViewById(R.id.cancel_image);
                Button yes = (Button)dialoglayout.findViewById(R.id.quite_image);

                Typeface typeFace=Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Regular.ttf");
                infoTitle.setTypeface(null,typeFace.BOLD);
                infotext.setTypeface(null, typeFace.BOLD);
                no.setText("NO");
                yes.setText("Yes");
                infoTitle.setText("");
                infoTitle.setVisibility(View.GONE);
                infotext.setText("Your GPS seems to be disabled, do you want to enable it?");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(dialoglayout);
                builder.setCancelable(true);
                final AlertDialog ald =builder.show();
                ald.setCancelable(true);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyGpsActivation();
                        ald.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        ald.dismiss();
                    }
                });


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
        }

        if (GeneralHelpers.isInternetAvailable(this)) {
            MemberService memberService = new MemberService(accessToken);
            memberService.updateMember(currentMember.id, updatedMember);
            GlobalVariables.updatePosition = true;
            GlobalVariables.isMenuOnPosition = true;
            GlobalVariables.MENU_POSITION = 1;
        } else {
            ViewHelpers.showPopup(this, getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection), true);
        }
    }

    @Override
    public void onBackPressed(){

        //super.onBackPressed();

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_left, R.anim.anim_exit_right);
        if(GlobalVariables.menuDeep == 0){

            ViewHelpers.showExitPopup(this);

        }
        else{
            if(GlobalVariables.menuPart == 1){
                if(GlobalVariables.menuDeep == 1)
                {
                    // IN ONE PROFILE FROM PEOPLE DIRECTORY...
                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleDirectoryFragment_());


                }
                else if(GlobalVariables.menuDeep == 2){

                    // IN REPLY FROM PEOPLE DIRECTORY...

                    GlobalVariables.menuDeep = 1;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(GlobalVariables.actualMember,0));
                }


            }else
            if(GlobalVariables.menuPart == 2){
                if(GlobalVariables.menuDeep == 1)
                {
                    // IN ONE PROFILE FROM PEOPLE FINDER...
                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());


                }
                else if(GlobalVariables.menuDeep == 2){

                    // IN REPLY FROM PEOPLE FINDER...
                    GlobalVariables.menuDeep = 1;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(GlobalVariables.actualMember,0));


                }

            }else
            if(GlobalVariables.menuPart == 3){
                if(GlobalVariables.menuDeep == 1)
                {
                    // IN ONE HUB FROM HUBS...
                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new HubsFragment_());

                }
                else if(GlobalVariables.menuDeep == 2){

                    // IN ONE PROFILE FROM HUBS...
                    GlobalVariables.menuDeep = 1;
                    Hub hub = Hub.createHubFromJson(createJsonElementFromString(preferences.getString("current_hub", "")));
                    fragmentTransaction.replace(R.id.contentFrame, new OneHubFragment_().newInstance(hub));

                }
                else if(GlobalVariables.menuDeep == 3){

                    // IN REPLY FROM HUBS...
                    GlobalVariables.menuDeep = 2;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(GlobalVariables.actualMember,0));

                }


            }else
            if(GlobalVariables.menuPart == 4){

                if(GlobalVariables.menuDeep == 1)
                {
                    // IN ADD OR UPDATE T0D0 FROM T0D0S...

                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());

                }
                if (GlobalVariables.menuDeep == 2)
                {
                    GlobalVariables.menuDeep = 1;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(GlobalVariables.actualMember,0));

                }

            }else
            if(GlobalVariables.menuPart == 5){

                if(GlobalVariables.menuDeep == 1)
                {
                    // IN ADD OR REPLY POST FROM MARKETPLACE...

                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new MarketPlaceFragment_());

                }

            }else
            if(GlobalVariables.menuPart == 6){

                if(GlobalVariables.menuDeep == 1)
                {
                    // IN EDIT OR REPLY FROM MYPROFILE...

                    GlobalVariables.menuDeep = 0;
                    fragmentTransaction.replace(R.id.contentFrame, new OneProfileFragment_().newInstance(null, 1));

                }
            }
        }

        fragmentTransaction.commit();

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
            GlobalVariables.inRadarFragement = true;
            if(currentMember.radarVisibility) {
                updateLocation();
            }
            else { fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
            GlobalVariables.inRadarFragement = true;
                mTitle = getResources().getString(R.string.title_find_fragment);
            }

        } else if(position == 2) {
            mTitle = getResources().getString(R.string.title_hubs);
            fragmentTransaction.replace(R.id.contentFrame, new HubsFragment_());
        } else if(position == 3) {
            mTitle = getResources().getString(R.string.title_todos_fragment);
            fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());
        } else if(position == 4) {
            MarketPlaceFragment_.defaultIndex = 0;
            mTitle = getResources().getString(R.string.title_market_place);
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
                fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                fragmentTransaction.replace(R.id.contentFrame, new PeopleFinderFragment_());
                GlobalVariables.inRadarFragement = true;
                fragmentTransaction.commit();
            } else if (GlobalVariables.MENU_POSITION == 6) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
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

        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
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

        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
        fragmentTransaction.replace(R.id.contentFrame, new NewTodoFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    public void onEditProfilePressed(){

        GlobalVariables.MENU_POSITION = 10;
        GlobalVariables.isMenuOnPosition = false;

        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
        fragmentTransaction.replace(R.id.contentFrame, new MeFragment_());

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

	/*@Override
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
	}*/

	/** Called whenever we call invalidateOptionsMenu() */

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view

        GlobalVariables.menu = menu;
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        addButton = (ImageButton) getActionBar().getCustomView().findViewById(R.id.add_button);

        if(GlobalVariables.inMarketPlaceFragment || GlobalVariables.inMyTodosFragment) {
            addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_plus)));
            if(!drawerOpen) addButton.setVisibility(View.VISIBLE);
        }
        else
            if(GlobalVariables.inRadarFragement)
            {
                addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_refresh)));
               if(!drawerOpen) addButton.setVisibility(View.VISIBLE);
            }
            else
                if(GlobalVariables.inMyProfileFragment)
                {
                    addButton.setBackgroundDrawable(getResources().getDrawable((R.drawable.topmenu_pencil)));
                    if(!drawerOpen) addButton.setVisibility(View.VISIBLE);
                }
                else addButton.setVisibility(View.INVISIBLE);

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

    @Override
    public void onResume() {
        super.onResume();
        if(GlobalVariables.inRadarFragement) {
            updateLocation();
        }
        BaseApplication.register(this);
    }
}
