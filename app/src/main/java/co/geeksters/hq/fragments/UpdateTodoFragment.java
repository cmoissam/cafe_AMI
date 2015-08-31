package co.geeksters.hq.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.TodoAdapter;
import co.geeksters.hq.events.failure.ConnectionFailureEvent;
import co.geeksters.hq.events.success.CreateTodoEvent;
import co.geeksters.hq.events.success.MembersEvent;
import co.geeksters.hq.events.success.MembersSearchEvent;
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


    public Calendar cal;
    public int day;
    public int month;
    public int year;


    public Calendar c;
    public int hour;
    public int minute;

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
            todoInput.setText(todo.text);
            String[] splitDate = todo.remindMeAt.split(" ");
            dateText.setText(splitDate[0]);
            timeText.setText(splitDate[1]);

        }
    }
    // Listview Adapter
    TodoAdapter adapter;
    // ArrayList for Listview
    ArrayList<HashMap<String, String>> members = new ArrayList<HashMap<String, String>>();
    List<Member> concernedMembers = new ArrayList<Member>();

    String accessToken;
    List<Member> membersList = new ArrayList<Member>();
    int from = 0;
    boolean onSearch = false;

    // List view
    @ViewById(R.id.list_view_members)
    ListView listViewMembers;

    // Search EditText
    @ViewById(R.id.inputSearch)
    EditText inputSearch;

    @ViewById(R.id.membersProgress)
    ProgressBar membersProgress;

    @ViewById(R.id.membersSearchForm)
    LinearLayout membersSearchForm;

    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    @ViewById(R.id.displayAll)
    TextView displayAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().invalidateOptionsMenu();
        BaseApplication.register(this);
        layoutInflater = inflater;
        preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
        return null;
    }

    @Click(R.id.save_button)
    public void createPost() {

        hide_keyboard(getActivity());

        if (todoInput.getText().toString().length() < 3) {
            ViewHelpers.showPopup(getActivity(), "Info", "The todo should contain more then 3 caracters");

        } else {

            Todo todoToSave = new Todo();

            todoToSave.memberId = currentMember.id;
            todoToSave.remindMeAt = dateText.getText().toString()+" "+timeText.getText().toString();
            todoToSave.members.addAll(adapter.concernedMembers);
            todoToSave.text = todoInput.getText().toString();

            TodoService todoService = new TodoService(accessToken);
            todoService.createTodo(todoToSave);
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

    @Click(R.id.date_button)
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
    @Click(R.id.time_button)
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
    public void onTodoCreate(CreateTodoEvent event) {


        // Getting reference to the FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Creating a fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.contentFrame, new MyToDosFragment_());

        // Committing the transaction
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onTodoNotCreate(ConnectionFailureEvent event) {

        ViewHelpers.showPopup(getActivity(), "Warning", "You cannot create your todo, try later please");
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
                memberService.listAllMembersByPaginationOrSearch(this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
            } else {
                //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }
        }

        public void searchForMembersByPaginationService(String search){
            if(GeneralHelpers.isInternetAvailable(getActivity())) {

                MemberService memberService = new MemberService(accessToken);
                memberService.searchForMembersFromKey(search,this.from, GlobalVariables.SEARCH_SIZE, GlobalVariables.ORDER_TYPE, GlobalVariables.ORDER_COLUMN);
            } else {
                //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
                ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
            }
        }

        @AfterViews
        public void listAllMembersByPagination(){
            listAllMembersByPaginationService();
        }

        @Subscribe
        public void onGetListMembersByPaginationEvent(MembersEvent event) {
            this.from += GlobalVariables.SEARCH_SIZE;


            membersList.addAll(event.members);

            members = Member.membersInfoForItem(getActivity(), members, membersList);

            GlobalVariables.finderList = false;
            adapter = new TodoAdapter(getActivity(), membersList, listViewMembers,concernedMembers);
            listViewMembers.setAdapter(adapter);

            ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

            if(members.size() < GlobalVariables.SEARCH_SIZE)
                displayAll.setVisibility(View.GONE);
            else
                displayAll.setVisibility(View.VISIBLE);

            if(adapter.isEmpty())
                emptySearch.setVisibility(View.VISIBLE);
            else
                emptySearch.setVisibility(View.GONE);
        }

        @AfterViews
        public void addFooterToListview() {
            listViewMembers.addFooterView(new View(getActivity()), null, true);
        }


        @TextChange(R.id.inputSearch)
        public void searchForMemberByPagination() {
            from = 0;
            membersList = new ArrayList<Member>();
            members = new ArrayList<HashMap<String, String>>();
            if(!inputSearch.getText().toString().isEmpty())
                searchForMembersByPaginationService(inputSearch.getText().toString());
            else {
                listAllMembersByPaginationService();
            }

        }

        @Subscribe
        public void onSearchForMemberByPaginationEvent(MembersSearchEvent event) {

            this.from += GlobalVariables.SEARCH_SIZE;

            membersList.addAll(event.members);

            adapter = new TodoAdapter(getActivity(), membersList, listViewMembers,concernedMembers);
            listViewMembers.setAdapter(adapter);
            ViewHelpers.setListViewHeightBasedOnChildren(listViewMembers);

            if(adapter.isEmpty()) {
                emptySearch.setVisibility(View.VISIBLE);
            }
            else
                emptySearch.setVisibility(View.GONE);

            if(event.members.size() < GlobalVariables.SEARCH_SIZE)
                displayAll.setVisibility(View.GONE);
            else
                displayAll.setVisibility(View.VISIBLE);

            if(event.members.size() == 0)
                displayAll.setVisibility(View.GONE);
        }

        @Click(R.id.clearContent)
        public void clearSearchInput() {
            inputSearch.setText("");
        }

        @Click(R.id.displayAll)
        public void displayAllMembers() {
            if(!inputSearch.getText().toString().isEmpty())
                searchForMembersByPaginationService(inputSearch.getText().toString());
            else {
                listAllMembersByPaginationService();
            }
        }

}
