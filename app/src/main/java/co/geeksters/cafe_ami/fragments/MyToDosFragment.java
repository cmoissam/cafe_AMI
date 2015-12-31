package co.geeksters.cafe_ami.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.adapter.TodosAdapter;
import co.geeksters.cafe_ami.events.failure.NoTodosFailureEvent;
import co.geeksters.cafe_ami.events.success.DeleteTodosEvent;
import co.geeksters.cafe_ami.events.success.TodosEvent;
import co.geeksters.cafe_ami.global.BaseApplication;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.global.helpers.GeneralHelpers;
import co.geeksters.cafe_ami.global.helpers.ViewHelpers;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.models.Todo;
import co.geeksters.cafe_ami.services.TodoService;

import static co.geeksters.cafe_ami.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_my_to_dos)
public class MyToDosFragment extends Fragment {
    // ArrayList for Listview
    String accessToken;
    List<Todo> todosList = new ArrayList<Todo>();
    TodosAdapter adapter;
    LayoutInflater inflater;
    Member currentMember;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();


    @ViewById(R.id.todoList)
    LinearLayout todoList;

    @ViewById(R.id.loading)
    LinearLayout loading;

    @ViewById(R.id.empty_search)
    LinearLayout emptySearch;


    @ViewById(R.id.myTodoForm)
    LinearLayout myTodoForm;


    public void listTodosForCurrentMemberService() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token", "").replace("\"", "");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));


        if(PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).getBoolean("visit_info_todo",true)) {


            PreferenceManager.getDefaultSharedPreferences(GlobalVariables.activity).edit().putBoolean("visit_info_todo",false).commit();

            LayoutInflater inflater = GlobalVariables.activity.getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.pop_up_info_todo, null);

            ImageView cancelImage = (ImageView) dialoglayout.findViewById(R.id.cancel_popup);

            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVariables.activity);
            builder.setView(dialoglayout);
            builder.setCancelable(true);
            final AlertDialog ald =builder.show();
            ald.setCancelable(true);

            cancelImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ald.dismiss();
                }
            });

        }

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            TodoService todoService = new TodoService(accessToken);
            loading.setVisibility(View.VISIBLE);
            todoService.listTodosForMember();
        } else {
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title_network), getResources().getString(R.string.no_connection),true);
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyProfileFragment = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.inMyTodosFragment = true;
        GlobalVariables.needReturnButton = false;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_todos_fragment));
    }


    @AfterViews
    public void listTodoForCurrentMember() {

        myTodoForm.setBackgroundColor(Color.parseColor("#eeeeee"));

        listTodosForCurrentMemberService();
    }

    @Subscribe
    public void onGetListTodosEvent(TodosEvent event) {

        emptySearch.setVisibility(View.INVISIBLE);
        GlobalVariables.notifiyedByTodo = false;
        loading.setVisibility(View.INVISIBLE);
        //spinner.setVisibility(View.GONE);
        todosList = event.todos;
        TodosAdapter adapter = new TodosAdapter(inflater,currentMember, todoList, todosList, accessToken,this);
        adapter.makeList();
        if (event.todos.isEmpty())
        {
            emptySearch.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onGetDeletedTodoEvent(DeleteTodosEvent event) {


            loading.setVisibility(View.INVISIBLE);

            TodoService todoService = new TodoService(accessToken);
            todoService.listTodosForMember();

    }
    @Subscribe
    public void onGetDeletedTodoEvent(NoTodosFailureEvent event) {

        loading.setVisibility(View.INVISIBLE);
        emptySearch.setVisibility(View.VISIBLE);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseApplication.register(this);

        this.inflater = inflater;
        GlobalVariables.inMyTodosFragment = true;
        GlobalVariables.menuPart = 4;
        GlobalVariables.menuDeep = 0;
        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);

        return null;
    }

    @Override
    public void onDestroyView(){

        super.onDestroyView();
        GlobalVariables.inMyTodosFragment = false;
    }

    @Override
    public void onDestroy(){
        super.onDestroyView();
        GlobalVariables.inMyTodosFragment = false;

    }
}