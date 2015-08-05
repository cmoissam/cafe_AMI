package co.geeksters.hq.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.R;
import co.geeksters.hq.adapter.CommentsAdapter;
import co.geeksters.hq.adapter.ListViewHubAdapter;
import co.geeksters.hq.events.success.CommentEvent;
import co.geeksters.hq.events.success.CommentsEvent;
import co.geeksters.hq.events.success.CommentsEventOnReplay;
import co.geeksters.hq.events.success.HubsEvent;
import co.geeksters.hq.events.success.SaveMemberEvent;
import co.geeksters.hq.global.BaseApplication;
import co.geeksters.hq.global.Config;
import co.geeksters.hq.global.CustomOnItemSelectedListener;
import co.geeksters.hq.global.GlobalVariables;
import co.geeksters.hq.global.helpers.GeneralHelpers;
import co.geeksters.hq.global.helpers.ParseHelpers;
import co.geeksters.hq.global.helpers.ViewHelpers;
import co.geeksters.hq.models.Comment;
import co.geeksters.hq.models.Company;
import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Interest;
import co.geeksters.hq.models.Member;
import co.geeksters.hq.models.Social;
import co.geeksters.hq.services.CommentService;
import co.geeksters.hq.services.HubService;
import co.geeksters.hq.services.MemberService;

import static co.geeksters.hq.global.helpers.GeneralHelpers.formatActualDate;
import static co.geeksters.hq.global.helpers.GeneralHelpers.isInternetAvailable;
import static co.geeksters.hq.global.helpers.ParseHelpers.createJsonElementFromString;
import static co.geeksters.hq.global.helpers.ViewHelpers.createViewInterestToEdit;
import static co.geeksters.hq.global.helpers.ViewHelpers.deleteTextAndSetHint;
import static co.geeksters.hq.global.helpers.ViewHelpers.showProgress;

@EFragment(R.layout.fragment_reply)
public class ReplyMarketFragment extends Fragment {

    @ViewById(R.id.commentContent)
    EditText commentContent;

    @ViewById(R.id.replyScrollView)
    ScrollView replyScrollView;

    @ViewById(R.id.commentsLayout)
    LinearLayout commentsLayout;

    @ViewById(R.id.commentNumber)
    TextView commentNumber;

    @ViewById(R.id.send)
    ImageView send;

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

        GlobalVariables.MENU_POSITION = 8;
        GlobalVariables.isMenuOnPosition = false;
        layoutInflater = inflater;
        BaseApplication.register(this);

        SharedPreferences preferences = getActivity().getSharedPreferences("CurrentUser", getActivity().MODE_PRIVATE);
        accessToken = preferences.getString("access_token","").replace("\"","");
        currentMember = Member.createUserFromJson(createJsonElementFromString(preferences.getString("current_member", "")));

        return null;
    }

    @AfterViews
    public void setCommentList() {
        setCommentsList();
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

            TextView date = (TextView) childViewComment.findViewById(R.id.date);
            date.setText(commentList.get(i).createdAt);

            ImageView picture = (ImageView) childViewComment.findViewById(R.id.picture);

            ViewHelpers.setImageViewBackgroundFromURL(getActivity(), picture, commentList.get(i).member.image);

            commentsLayout.addView(childViewComment, 0);
        }
    }

    @Click(R.id.send)
    public void sendComment() {
        commentContent.setError(null);
        if(String.valueOf(commentContent.getText()).length() < 3) {
            commentContent.setError(getString(R.string.error_short_text));
            commentContent.requestFocus();
        } else {
            Comment comment = new Comment();
            comment.text = String.valueOf(commentContent.getText());

            CommentService commentService = new CommentService(accessToken);
            commentService.commentPost(postId, comment, currentMember);
        }
    }

    @Subscribe
    public void onGetCommentsEvent(CommentsEventOnReplay event) {
        commentList = new ArrayList<Comment>();
        commentList = event.comments;

        commentContent.setText("");
        setCommentsList();
    }
}