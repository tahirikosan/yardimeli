package com.tahirabuzetoglu.yardimeli.ui.comment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Comment;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.OnItemClickListener  {

    public static final String COMMENT_POST = "COMMENT_POST";
    public static final String LAST_POSITION = "LAST_POSITION";

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    private ImageView ivOwnerImage;
    private TextView tvOwnerName;
    private TextView tvDescription;
    private ImageView ivPostImage;
    private EditText etComment;
    private Button btnInsertComment;
    private RecyclerView recyclerViewComments;

    private CommentAdapter commentAdapter;

    List<Comment> mCommentList = new ArrayList<>();
    private Post currentPost;
    private CommentViewModel commentViewModel;
    private SharedPrefData sharedPrefData;
    // load user
    User user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        // set shared prefs
        sharedPrefData = new SharedPrefData(this);
        user = sharedPrefData.loadUser();

        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        setUI();

        Intent intent = getIntent();
        currentPost = (Post) intent.getSerializableExtra(COMMENT_POST);
        Picasso.get().load(currentPost.getOwnerImageUrl()).into(ivOwnerImage);
        Picasso.get().load(currentPost.getImageUrl()).into(ivPostImage);
        tvOwnerName.setText(currentPost.getOwnerName());
        tvDescription.setText(currentPost.getDescription());


        //setCommentList();
        getComments(currentPost.getId());

        //setCommentAdapter();

        btnInsertComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertComment();
            }
        });

    }


    //get all comments from db
    private void getComments(String postId){
        commentViewModel.getComments(postId);
        commentViewModel.commenListLiveData.observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> commentList) {
                if(!commentList.isEmpty()){
                    mCommentList = commentList;
                    setCommentAdapter(commentList);
                }else{
                    Toast.makeText(CommentActivity.this, "Hiç yorum yok", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setUI() {
        ivOwnerImage = findViewById(R.id.iv_owner_image);
        ivPostImage = findViewById(R.id.iv_post_image);
        tvDescription = findViewById(R.id.tv_post_desc);
        tvOwnerName = findViewById(R.id.tv_post_owner);
        etComment = findViewById(R.id.et_comment);
        btnInsertComment = findViewById(R.id.btn_insert_comment);

        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        recyclerViewComments.setLayoutManager(layoutManager);
        recyclerViewComments.setHasFixedSize(true);

        progressDialog = new ProgressDialog(this);

    }

    private void setCommentAdapter(List<Comment> commentList) {
        //set comment adapter
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerViewComments.setAdapter(commentAdapter);
        commentAdapter.setOnItemClickListener(CommentActivity.this);
    }

    private void insertComment() {
        String ownerComment = etComment.getText().toString().trim();
        etComment.setText("");

        if (!ownerComment.isEmpty()) {
            //showProgressDialog();

            commentViewModel.insertComment(currentPost.getId(), ownerComment, user);
            commentViewModel.commentLiveData.observe(this, new Observer<Comment>() {
                @Override
                public void onChanged(Comment comment) {
                    if(comment.isSuccess()){
                        // add comment into recycler view
                        mCommentList.add(comment);
                        setCommentAdapter(mCommentList);

                        //progressDialog.dismiss();
                    }else{
                        Toast.makeText(CommentActivity.this, "Could not insert comment, please try again", Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteComment(String postId, Comment currentComment) {
        showProgressDialog();

        commentViewModel.deleteComment(postId, currentComment, user, mCommentList);
        commentViewModel.commenListLiveData.observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                // to avoid crashs
                if(comments.size() >= 1){
                    if(comments.get(0).isSuccess()){
                        mCommentList = comments;
                        setCommentAdapter(mCommentList);

                        Toast.makeText(CommentActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CommentActivity.this, "Comment could not deleted, please try again", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mCommentList = comments;
                    setCommentAdapter(mCommentList);

                    Toast.makeText(CommentActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }




    @Override
    public void onOptionsClicked(int position, View view) {

        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, view);
        setForceShowIcon(popupMenu);

        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.comment_options_menu, popupMenu.getMenu());
        popupMenu.show();


        // show delete_comment item only for owner of comment or owner of post
        if (!(user.getId().equals(mCommentList.get(position).getUserID()) || user.getId().equals(currentPost.getOwnerID()))) {
            popupMenu.getMenu().findItem(R.id.delete_comment).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.report_comment:
                        Toast.makeText(CommentActivity.this , "Comment Reported", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete_comment:
                        deleteComment(currentPost.getId(), mCommentList.get(position));
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }

    private void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

}