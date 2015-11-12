package co.geeksters.hq.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.adapter.TodoAdapter;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.MembersSearchEvent;
import co.geeksters.hq.events.success.UpdateTodoEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Todo;
import co.geeksters.hq.services.MemberService;
import co.geeksters.hq.services.TodoService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by geeksters on 11/08/15.
 */
@EFragment(R.layout.fragment_new_todo)
public class UpdateTodoFragment extends DialogFragment{

    @ViewById(R.id.interest)
    EditText interest;

    @ViewById(R.id.interestsContent)
    LinearLayout interestsContent;

    @ViewById(R.id.interestContent)
    LinearLayout interestContent;

    LayoutInflater layoutInflater;

    @ViewById(R.id.date_text)
    EditText dateText;
    @ViewById(R.id.time_text)
    EditText timeText;

    @ViewById(R.id.todo_input)
    EditText todoInput;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;

    @ViewById(R.id.loading)
    LinearLayout loading;

    @ViewById(R.id.find_by_city_or_name)
    TextView findByCityOrName;

    @ViewById(R.id.textView_no_result)
    TextView textViewNoResult;

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;

    public Calendar cal;
    public int day;
    public int month;
    public int year;

    public int lastPosition = 0;

    public int currentTodoId;
    public Calendar c;
    public int hour;
    public int minute;

    public boolean onRefresh = false;
    public boolean noMoreMembers = false;

    public View footer;

    public boolean waitForSearch = false;
    public boolean firstTime = true;
    public boolean firstTimeSearch = false;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    // Listview Adapter
    TodoAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    List<Member> concernedMembers = new ArrayList<Member>();

    String accessToken;
    List<Member> membersList = new ArrayList<Member>();
    int from = 0;
    boolean onSearch = false;

    SharedPreferences preferences;
    Member currentMember;
    private static final String NEW_INSTANCE_TODO_KEY = "todo_key";

    public static UpdateTodoFragment_ newInstance(Todo todo) {
        UpdateTodoFragment_ fragment= new UpdateTodoFragment_();

        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_TODO_KEY, todo);
        fragment.setArguments(bundle);

        return fragment;
    }

    @AfterViews
    public void setPreferences() {

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);

        accessToken = preferences.getString("access_token", "").replace("\"", "");
        if(getArguments() != null) {
            Todo todo = (Todo) getArguments().getSerializable(NEW_INSTANCE_TODO_KEY);
            concernedMembers.addAll(todo.members);
            currentTodoId = todo.id;
            todoInput.setText(todo.text);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            try {
                date = format.parse(todo.remindMeAt);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat formatToShow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatToShow.setTimeZone(tz);

            String[] splitDate = formatToShow.format(date).split(" ");
            dateText.setText(splitDate[0]);
            timeText.setText(splitDate[1]);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().invalidateOptionsMenu();
        getActivity().getActionBar().setTitle("To do");
        BaseApplication.register(this);
        layoutInflater = inflater;
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        GlobalVariables.menuDeep = 1;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
        return null;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.needReturnButton = true;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle("TO DO");
    }



    @Click(R.id.save_button)
    public void createPost() {

        hide_keyboard(getActivity());

        if (todoInput.getText().toString().length() < 3) {
            ViewHelpers.showPopup(getActivity(), "info", getResources().getString(R.string.todo_error), false);

        } else {

            Todo todoToSave = new Todo();

            todoToSave.memberId = currentMember.id;
            String dateWithTimeZone = dateText.getText().toString()+" "+timeText.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = format.parse(dateWithTimeZone);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Date actualDate = new Date();
            if (date.before(actualDate))
            {
                ViewHelpers.showPopup(getActivity(),"Date error","Reminder date can't be before actual date",true);
            }
            else{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            todoToSave.remindMeAt = sdf.format(date);
            todoToSave.members.addAll(adapter.concernedMembers);
            todoToSave.text = todoInput.getText().toString();
            todoToSave.id = currentTodoId;

            TodoService todoService = new TodoService(accessToken);
            todoService.updateTodo(todoToSave);
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!BaseApplication.isRegistered(this))
            BaseApplication.register(this);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

    }

    @Click(R.id.date_text)
    public void showDatePicker() {

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox

                        String day = "";
                        String month = "";
                        if (dayOfMonth<10) day = "0"+dayOfMonth;
                        else day = ""+dayOfMonth;
                        if ((monthOfYear + 1)<10) month = "0"+(monthOfYear + 1);
                        else month = ""+(monthOfYear + 1);
                        dateText.setText(year + "-"
                                + month + "-" + day);
                    }
                }, year, month, day);
        dpd.show();

    }
    @Click(R.id.time_text)
    public void showTimePicker() {


        TimePickerDialog timepicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {


            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = "";
                String minuteString = "";

                if (hourOfDay<10) hour = "0"+hourOfDay;
                else hour = ""+hourOfDay;
                if ((minute + 1)<10) minuteString = "0"+(minute + 1);
                else minuteString = ""+(minute + 1);

                timeText.setText(hour + ":"
                        +minuteString+":00");
            }
        }, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        timepicker.show();
    }

    @Subscribe
    public void onTodoUpdate(UpdateTodoEvent event) {


        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.anim_enter_left,R.anim.anim_exit_right);

        fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onTodoNotUpdate(ConnectionFailureEvent event) {

        ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
    }



    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseApplication.unregister(this);
    }


        public void listAllMembersByPaginationService(){
            SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);

            accessToken = preferences.getString("access_token","").replace("\"","");

            if(GeneralHelpers.isInternetAvailable(getActivity())) {
                MemberService memberService = new MemberService(accessToken);
                memberService.listAllMembersByPaginationForTodo(this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
            } else {
                //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            }
        }

        public void searchForMembersByPaginationService(String search){
            if(GeneralHelpers.isInternetAvailable(getActivity())) {

                MemberService memberService = new MemberService(accessToken);
                memberService.searchForMembersForTodo(search, this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
            } else {
                //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
            }
        }

        @AfterViews
        public void listAllMembersByPagination(){
            listAllMembersByPaginationService();
            listViewMembers.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
            emptySearch.setVisibility(View.INVISIBLE);
        }

        @Subscribe
        public void onGetListMembersByPaginationEvent(MembersEvent event) {

            if(firstTime && firstTimeSearch) {

            }
            else {
                this.from += GlobalVariables.SEARCH_SIZE;
                waitForSearch = false;

                loading.setVisibility(View.INVISIBLE);
                listViewMembers.setVisibility(View.VISIBLE);
                emptySearch.setVisibility(View.INVISIBLE);

                membersList.addAll(event.members);

                members = Member.membersInfoForItem(getActivity(), members, membersList);

                GlobalVariables.finderList = false;
                adapter = new TodoAdapter(getActivity(), membersList, listViewMembers, concernedMembers);
                listViewMembers.setAdapter(adapter);

                ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

                listViewMembers.removeFooterView(footer);

                if (adapter.isEmpty()) {
                    emptySearch.setVisibility(View.VISIBLE);
                } else
                    emptySearch.setVisibility(View.INVISIBLE);


                if (onRefresh) {
                    //TODO scroll to end of list
                    listViewMembers.setSelection(lastPosition);
                }
                onRefresh = false;

                if (members.size() < GlobalVariables.SEARCH_SIZE) {
                    noMoreMembers = true;
                }
                if (event.members.size() == 0)
                    noMoreMembers = true;

            }
            firstTime = false;
        }

        @TextChange(R.id.inputSearch)
        public void searchForMemberByPagination() {
            emptySearch.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
            listViewMembers.setVisibility(View.INVISIBLE);
            firstTimeSearch = true;

            Runnable task = new Runnable() {
                public void run() {
                    if (!waitForSearch) {
                        waitForSearch = true;
                        from = 0;
                        membersList = new ArrayList<Member>();
                        members = new ArrayList<HashMap<String, String>>();

                        noMoreMembers = false;
                        if (!inputSearch.getText().toString().isEmpty()) {
                            searchForMembersByPaginationService(inputSearch.getText().toString());
                            listViewMembers.setVisibility(View.INVISIBLE);
                            loading.setVisibility(View.VISIBLE);
                            emptySearch.setVisibility(View.INVISIBLE);
                        }
                        else {
                            listAllMembersByPaginationService();
                            loading.setVisibility(View.VISIBLE);
                            listViewMembers.setVisibility(View.INVISIBLE);
                            emptySearch.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            };

            worker.schedule(task, 2, TimeUnit.SECONDS);

        }

        @Subscribe
        public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {

            this.from += GlobalVariables.SEARCH_SIZE;

            loading.setVisibility(View.INVISIBLE);
            listViewMembers.setVisibility(View.VISIBLE);
            emptySearch.setVisibility(View.INVISIBLE);
            membersList.addAll(event.members);

            adapter = new TodoAdapter(getActivity(), membersList, listViewMembers,concernedMembers);
            listViewMembers.setAdapter(adapter);
            ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

            listViewMembers.removeFooterView(footer);


            if(adapter.isEmpty()) {
                emptySearch.setVisibility(View.VISIBLE);
            }
            else
                emptySearch.setVisibility(View.INVISIBLE);

            if(onRefresh)
            {
                //TODO scroll to end of list
                listViewMembers.setSelection(lastPosition);
            }
            onRefresh = false;

            if(members.size() < GlobalVariables.SEARCH_SIZE){
                noMoreMembers = true;
            }
            if(event.members.size() == 0)
                noMoreMembers = true;

            waitForSearch = false;

        }
    @AfterViews
    public void listViewSetting(){

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        findByCityOrName.setTypeface(typeFace);
        inputSearch.setTypeface(typeFace);
        textViewNoResult.setTypeface(typeFace);

        footer = getActivity().getLayoutInflater().inflate(R.layout.refresh_list_view, null);

        listViewMembers.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

                    if (!onRefresh) {
                        if (!noMoreMembers) {
                            onRefresh = true;
                            if (!inputSearch.getText().toString().isEmpty())
                                searchForMembersByPaginationService(inputSearch.getText().toString());

                            else {
                                listAllMembersByPaginationService();
                            }
                            lastPosition = listViewMembers.getLastVisiblePosition();
                            listViewMembers.addFooterView(footer);
                        }
                    }
                }
            }
        });


    }

}
