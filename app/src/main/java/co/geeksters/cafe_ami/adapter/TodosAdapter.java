package co.geeksters.cafe_ami.adapter;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import co.geeksters.cafe_ami.R;
import co.geeksters.cafe_ami.activities.GlobalMenuActivity;
import co.geeksters.cafe_ami.fragments.UpdateTodoFragment_;
import co.geeksters.cafe_ami.global.GlobalVariables;
import co.geeksters.cafe_ami.models.Member;
import co.geeksters.cafe_ami.models.Todo;
import co.geeksters.cafe_ami.services.TodoService;

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
            TextView tododate = (TextView) childView.findViewById(R.id.date_text);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            try {
                date = format.parse(todoList.get(i).createdAt);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat formatToShow = new SimpleDateFormat("dd/MM/yyyy");
            formatToShow.setTimeZone(tz);
            tododate.setText(formatToShow.format(date));



            ImageView deleteTodo = (ImageView)childView.findViewById(R.id.deleteTodo);
            ImageView editTodo = (ImageView)childView.findViewById(R.id.editTodo);

            LinearLayout memberslist = (LinearLayout)childView.findViewById(R.id.memberslist);

            TodosMembersAdapter adapter = new TodosMembersAdapter(inflater,currentUser,memberslist, todoList.get(i).members , accessToken,this.context);
            adapter.makeList();

            LinearLayout deleteTodoLayout = (LinearLayout)childView.findViewById(R.id.deleteTodoLayout);
            LinearLayout editTodoLayout = (LinearLayout)childView.findViewById(R.id.editTodoLayout);




            editTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.MENU_POSITION = 10;
                    GlobalVariables.isMenuOnPosition = false;

                    // Getting reference to the FragmentManager
                    FragmentManager fragmentManager = ((GlobalMenuActivity) GlobalVariables.activity).getSupportFragmentManager();

                    // Creating a fragment transaction
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);

                    fragmentTransaction.replace(R.id.contentFrame, new UpdateTodoFragment_().newInstance(todoList.get(index)));

                    // Committing the transaction
                    fragmentTransaction.commit();

                }
            });

            deleteTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (todoList.get(index).memberId != currentUser.id) {

                        for(int j= 0 ;j<todoList.get(index).members.size();j++)
                        {
                         if(todoList.get(index).members.get(j).id == currentUser.id)
                         {
                             todoList.get(index).members.remove(j);
                             break;
                         }
                        }

                        TodoService todoService = new TodoService(accessToken);
                        todoService.updateTodoforDetach(todoList.get(index));

                    } else {
                        TodoService todoService = new TodoService(accessToken);
                        todoService.deleteTodo(todoList.get(index).id);
                    }
                }
            });




            final SwipeLayout swipeLayout =  (SwipeLayout)childView.findViewById(R.id.swipe_layout);
            //set show mode.
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);




            llList.addView(childView);

            if(todoList.get(i).memberId != currentUser.id) {

                editTodo.setVisibility(View.GONE);
                editTodoLayout.setVisibility(View.INVISIBLE);
            }
            else {

                editTodo.setVisibility(View.VISIBLE);
                editTodoLayout.setVisibility(View.VISIBLE);


            }
            swipeLayout.removeAllSwipeDeniers();

            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, childView.findViewById(R.id.bottom_wrapper));


            swipeLayout.open(true, SwipeLayout.DragEdge.Left);

            swipeLayout.close();

            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, childView.findViewById(R.id.bottom_wrapper));
            swipeLayout.setLeftSwipeEnabled(Boolean.FALSE);




            final HorizontalScrollView horizScrollView = (HorizontalScrollView)childView.findViewById(R.id.horiz_scroll_view);
            final ImageView rowsButton = (ImageView)childView.findViewById(R.id.rows_button);
            final RelativeLayout horizScrollViewParent = (RelativeLayout)childView.findViewById(R.id.horiz_scroll_view_parent);

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {

                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {


                }

                @Override
                public void onOpen(SwipeLayout layout) {


                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });



            View orizontalView = (View) horizScrollView;

            orizontalView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    boolean isScrollable = horizScrollView.getWidth() < horizScrollView.getChildAt(0).getWidth();
                    int horizWidth = horizScrollView.getWidth();
                    int childWidth = horizScrollView.getChildAt(0).getWidth();
                    if (!isScrollable) {
                        rowsButton.setVisibility(View.GONE);
                    } else {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(horizScrollView.getWidth(), horizScrollView.getHeight());
                        int margin = (int) (50 * GlobalVariables.d);
                        params.setMargins(0, 0, margin, 0);
                        horizScrollView.setLayoutParams(params);

                    }

                    rowsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    horizScrollView.smoothScrollTo(
                                            (int) horizScrollView.getScrollX()
                                                    + horizScrollView.getWidth() / 2,
                                            (int) horizScrollView.getScrollY());
                                }
                            }, 100L);
                        }
                    });
                }
            });

        }
    }


}