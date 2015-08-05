package co.geeksters.hq.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.adapter.PostsAdapter;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.TodosEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.models.Todo;
import co.geeksters.hq.services.HubService;
import co.geeksters.hq.services.TodoService;

@EFragment(R.layout.fragment_my_to_dos)
public class MyToDosFragment extends Fragment {
//    ListViewHubAdapter adapterForHubList;
    ArrayList<HashMap<String, String>> todos = new ArrayList<HashMap<String, String>>();
    String accessToken;
    List<Todo> todosList = new ArrayList<Todo>();
    SharedPreferences.Editor editor;

    // List view
    @ViewById(R.id.list_view_my_todos)
    ListView listViewMyTodos;


    @ViewById(R.id.search_no_element_found)
    TextView emptySearch;

    public void listMyTodosService() {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");

        if(GeneralHelpers.isInternetAvailable(getActivity())) {
            TodoService todoService = new TodoService(accessToken);
            todoService.listTodosForMember();
        } else {
            //ViewHelpers.showProgress(false, this, contentFrame, membersSearchProgress);
            ViewHelpers.showPopup(getActivity(), getResources().getString(R.string.alert_title), getResources().getString(R.string.no_connection));
        }
    }

    @AfterViews
    public void listMyTodos() {
        listMyTodosService();
    }

    @Subscribe
    public void onGetListTodosEvent(TodosEvent event) {
        todosList = event.todos;

        ArrayList<HashMap<String, String>> todos = Todo.todosInfoForItem(todosList);

//        PostsAdapter adapter = new PostsAdapter(getActivity(), posts);
//        adapter.makeList();
        //adapterForHubList = new ListViewHubAdapter(getActivity(), hubsList, lastHubs, listViewHubs);
        //listViewHubs.setAdapter(adapterForHubList);
        //iewHelpers.setListViewHeightBasedOnChildren(listViewHubs);
    }

    @AfterViews
    public void addFooterToListview() {
        listViewMyTodos.addFooterView(new View(getActivity()), null, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        editor = preferences.edit();

        BaseApplication.register(this);

        return null;
    }
}