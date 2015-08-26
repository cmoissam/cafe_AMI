package co.geeksters.hq.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.Click;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.fragments.MyToDosFragment_;
import co.geeksters.hq.fragments.ReplyMarketFragment;
import co.geeksters.hq.fragments.ReplyMarketFragment_;
import co.geeksters.hq.fragments.UpdateTodoFragment;
import co.geeksters.hq.fragments.UpdateTodoFragment_;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Post;
import co.geeksters.hq.models.Todo;
import co.geeksters.hq.services.PostService;
import co.geeksters.hq.services.TodoService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

/**
 * Created by soukaina on 04/02/15.
 */
public class TodosAdapter {


    private List<Todo> todoList;
    String accessToken;
    LinearLayout llList;
    LayoutInflater inflater;
    SharedPreferences preferences;
    Member currentUser;
    Fragment context;

    public TodosAdapter(LayoutInflater inflater, Member currentUser, LinearLayout llList, List<Todo> todoList, String accessToken,Fragment fragment) {

        this.context = fragment;
        this.currentUser = currentUser;
        this.todoList = todoList;
        this.accessToken = accessToken;
        this.llList = llList;
        this.inflater = inflater;

    }

    public void makeList() {
        llList.removeAllViews();

        for(int i = 0 ; i < todoList.size(); i++) {
            final int index = i;

            final LinearLayout childView = (LinearLayout)inflater.inflate(R.layout.list_item_todo, null, false);
            TextView todoTextView = (TextView)childView.findViewById(R.id.todo);
            todoTextView.setText(todoList.get(i).text);


            TextView memberCount = (TextView) childView.findViewById(R.id.member_text);
            memberCount.setText(todoList.get(i).members.size() + " members");
            TextView tododate = (TextView) childView.findViewById(R.id.date_text);
            tododate.setText(todoList.get(i).createdAt);


            ImageView deleteTodo = (ImageView)childView.findViewById(R.id.deleteTodo);
            ImageView editTodo = (ImageView)childView.findViewById(R.id.editTodo);


            if(todoList.get(i).memberId == currentUser.id) {
                deleteTodo.setVisibility(View.VISIBLE);
                editTodo.setVisibility(View.VISIBLE);
            }

            editTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Getting reference to the FragmentManager
                    FragmentManager fragmentManager = context.getActivity().getSupportFragmentManager();

                    // Creating a fragment transaction
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.contentFrame, new UpdateTodoFragment_().newInstance(todoList.get(index)));

                    // Committing the transaction
                    fragmentTransaction.commit();

                }
            });

            deleteTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TodoService todoService = new TodoService(accessToken);
                    todoService.deleteTodo(todoList.get(index).id);
                }
            });

            llList.addView(childView);
        }
    }


}