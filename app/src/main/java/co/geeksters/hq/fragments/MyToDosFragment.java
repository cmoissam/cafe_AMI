package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.adapter.ListViewMarketAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.adapter.TodosAdapter;
import co.geeksters.hq.events.failure.NoTodosFailureEvent;
import co.geeksters.hq.events.success.CommentEvent;
import co.geeksters.hq.events.success.CommentsEvent;
import co.geeksters.hq.events.success.DeleteTodosEvent;
import co.geeksters.hq.events.success.PostEvent;
import co.geeksters.hq.events.success.PostsEvent;
import co.geeksters.hq.events.success.TodoEvent;
import co.geeksters.hq.events.success.TodosEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.models.Todo;
import co.geeksters.hq.services.PostService;
import co.geeksters.hq.services.TodoService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_my_to_dos)
public class MyToDosFragment extends Fragment {
    // ArrayList for Listview
    String accessToken;
    List<Todo> todosList = new ArrayList<Todo>();
    TodosAdapter adapter;
    LayoutInflater inflater;
    Member currentMember;


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

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            //spinner.setVisibility(View.VISIBLE);
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