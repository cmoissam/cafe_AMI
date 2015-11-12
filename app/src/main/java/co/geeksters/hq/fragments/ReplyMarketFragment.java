package co.geeksters.hq.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.events.success.CommentsEventOnReplay;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.services.CommentService;

import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;

@EFragment(R.layout.fragment_reply)
public class ReplyMarketFragment extends Fragment {

    @ViewById(R.id.commentContent)
    EditText commentContent;

    @ViewById(R.id.replyScrollView)
    ScrollView replyScrollView;

    @ViewById(R.id.commentsLayout)
    LinearLayout commentsLayout;

    @ViewById(R.id.border)
    ImageView border;

    @ViewById(R.id.Genral_layout)
    LinearLayout generalLayout;

    @ViewById(R.id.commentNumber)
    TextView commentNumber;

    @ViewById(R.id.send)
    Button send;

    // Beans
    LayoutInflater layoutInflater;
    SharedPreferences preferences;
    String accessToken;
    List<Comment> commentList;
    Member currentMember;
    int postId;
    private static final String NEW_INSTANCE_COMMENTS_KEY = "comments_key";
    private static final String NEW_INSTANCE_POST_KEY = "post_key";

    public static ReplyMarketFragment_ newInstance(int postId, List<Comment> commentList) {
        ReplyMarketFragment_ fragment = new ReplyMarketFragment_();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NEW_INSTANCE_COMMENTS_KEY, (java.io.Serializable) commentList);
        bundle.putSerializable(NEW_INSTANCE_POST_KEY, postId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments() != null) {
            commentList = (List<Comment>) getArguments().getSerializable(NEW_INSTANCE_COMMENTS_KEY);
            postId = (Integer) getArguments().getSerializable(NEW_INSTANCE_POST_KEY);
        }

        getActivity().invalidateOptionsMenu();
        GlobalVariables.MENU_POSITION = 8;
        GlobalVariables.isMenuOnPosition = false;
        layoutInflater = inflater;
        BaseApplication.register(this);

        if(GlobalVariables.menuPart == 1)
            GlobalVariables.menuDeep = 2;
        if(GlobalVariables.menuPart == 2)
            GlobalVariables.menuDeep = 2;
        if(GlobalVariables.menuPart == 3)
            GlobalVariables.menuDeep = 3;
        if(GlobalVariables.menuPart == 5)
            GlobalVariables.menuDeep = 1;
        if(GlobalVariables.menuPart == 6)
            GlobalVariables.menuDeep = 1;
        if(GlobalVariables.menuPart == 4)
            GlobalVariables.menuDeep = 2;

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));
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
        ((GlobalMenuActivity) getActivity()).setActionBarTitle("OPPORTUNITY");
    }


    @AfterViews
    public void setCommentList() {
        //Collections.reverse(commentList);
        setCommentsList();
        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        commentContent.setTypeface(typeFace);
        commentNumber.setTypeface(typeFace);
        send.setTypeface(typeFace);
    }

    public void setCommentsList() {
        commentNumber.setText(commentList.size() + " comments");
        commentsLayout.removeAllViews();

        for(int i = 0; i < commentList.size(); i++) {
            View childViewComment = layoutInflater.inflate(R.layout.list_item_comment, null); //same layout you gave to the adapter

            TextView fullNameTextView = (TextView) childViewComment.findViewById(R.id.fullName);
            fullNameTextView.setText(commentList.get(i).member.fullName);

            TextView commentTextView = (TextView) childViewComment.findViewById(R.id.comment);
            commentTextView.setText(commentList.get(i).text);

            Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
            commentTextView.setTypeface(typeFace);
            fullNameTextView.setTypeface(typeFace);
            //TextView date = (TextView) childViewComment.findViewById(R.id.date);
            //date.setText(commentList.get(i).createdAt);

            final int index = i;

            ImageView picture = (ImageView) childViewComment.findViewById(R.id.picture);

            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GlobalVariables.isInMyProfileFragmentFromOpportunities = true;
                    FragmentTransaction fragmentTransaction = ((GlobalMenuActivity) GlobalVariables.activity).getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new OneProfileFragment_().newInstance(commentList.get(index).member, 0);
                    fragmentTransaction.setCustomAnimations(R.anim.anim_enter_right,R.anim.anim_exit_left);
                    fragmentTransaction.replace(R.id.contentFrame, fragment);
                    fragmentTransaction.commit();
                }
            });

            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, commentList.get(i).member.image);

            commentsLayout.addView(childViewComment, 0);
        }
    }

    @Click(R.id.send)
    public void sendComment() {
        commentContent.setError(null);
        if(String.valueOf(commentContent.getText()).length() < 1) {
            commentContent.setError(getString(R.string.error_field_required));
            commentContent.requestFocus();
        } else {
            Comment comment = new Comment();
            comment.text = String.valueOf(commentContent.getText());

            CommentService commentService = new CommentService(accessToken);
            commentService.commentPost(postId, comment, currentMember);
            commentContent.setText("");
        }
    }

    @Subscribe
    public void onGetCommentsEvent(CommentsEventOnReplay event) {
        commentList = new ArrayList<Comment>();
        commentList = event.comments;
        setCommentsList();
    }
}