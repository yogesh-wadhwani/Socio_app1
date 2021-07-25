 package com.example.apnainsta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apnainsta.daos.PostDao;
import com.example.apnainsta.daos.UserDao;
import com.example.apnainsta.models.Comment;
import com.example.apnainsta.models.Post;
import com.example.apnainsta.models.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import static com.example.apnainsta.daos.PostDao.postCollections;

 public class CommentsActivity extends AppCompatActivity {
 RecyclerView commentRecyclerView;
 ImageView postComment;
     EditText commentInput;
     TextView postCaption;
     User currentUser = new User();
       Post currentPost = new Post();
       ProgressBar progressBar;
       Boolean foundUser = false;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        postComment = findViewById(R.id.postComment);
        commentInput = findViewById(R.id.commentInput);
        postCaption = findViewById(R.id.postCaption);
        progressBar = findViewById(R.id.commentsProgressBar);

        Intent intent = getIntent();
        String currentPostId = intent.getStringExtra("postRef");


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserDao userDao = new UserDao();


         postComment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if ( currentUser != null) {
                     String commentText = commentInput.getText().toString();
                     Long currentTime = System.currentTimeMillis();
                     Comment comment  = new Comment(commentText, currentUser.displayName, currentUser.imageURL,currentUser.uid ,currentTime);
                     (currentPost.comments).add(comment);

                     postCollections.document(currentPostId).set(currentPost);


                    commentInput.setText("");
                     Toast.makeText(CommentsActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();

                 }
             }
         });





           new Thread( new Runnable() { @Override public void run() {

             try{

                 currentPost = Tasks.await(PostDao.getPostById(currentPostId)).toObject(Post.class);
                 Log.d("ApnaInsta" , "Loading Post Successful");
                 currentUser = Tasks.await(userDao.getUserData(currentUserId)).toObject(User.class);
                 Log.d("ApnaInsta" , "Loading User Successful");
                 foundUser = true;

             }
             catch (Exception e){
                 Log.d("ApnaInsta" , e.getMessage());
             }
         } } ).start();




           for(int i =0;i<40;i++) {

               if (!foundUser) {
                   try {
                       Thread.sleep(50);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                 continue;
               } else if(foundUser){
                  //Toast.makeText(CommentsActivity.this, "found User", Toast.LENGTH_SHORT).show();

                   progressBar.setVisibility(View.INVISIBLE);
                   CommentsAdapter ad = new CommentsAdapter(currentPost.comments,currentPostId,currentPost);
                   commentRecyclerView.addItemDecoration(new DividerItemDecoration(CommentsActivity.this, DividerItemDecoration.VERTICAL));
                   commentRecyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
                   commentRecyclerView.setAdapter(ad);
                   commentRecyclerView.setVisibility(View.VISIBLE);
                   break;
               }
           if(i==39){
               Toast.makeText(CommentsActivity.this, "Loading Comments Failed", Toast.LENGTH_SHORT).show();
               finish();
           }

           }




 }

}