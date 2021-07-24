package com.example.apnainsta.daos;





import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.apnainsta.CommentsActivity;
import com.example.apnainsta.models.Comment;
import com.example.apnainsta.models.Post;
import com.example.apnainsta.models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class PostDao {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
public static CollectionReference postCollections = db.collection("posts");

    private static final FirebaseAuth mAuth =FirebaseAuth.getInstance();


public  void addPost(String text , String imageURL ,boolean hasVideo){

   String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
UserDao userDao = new UserDao();
    new Thread( new Runnable() { @Override public void run() {
        try {
            User user = Tasks.await(userDao.getUserData(currentUserId)).toObject(User.class);
                        Long currentTime = System.currentTimeMillis();
            ArrayList<String> likes = new ArrayList<>();
            ArrayList<Comment> comments = new ArrayList<>();

            Post post = new Post(text,user,currentTime,likes,imageURL,hasVideo,comments);


            postCollections.document().set(post);


        }catch (Exception e){
            Log.d("ApnaInsta" , e.getMessage());
        }
    } } ).start();


}

public static Task<DocumentSnapshot> getPostById( String posId){
return  postCollections.document(posId).get();

}


 public static void updateLikes(String posId){
      String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
     new Thread( new Runnable() { @Override public void run() {

         try{Post p = Tasks.await(getPostById(posId)).toObject(Post.class);
if(p.LikedBy != null) {
    Boolean isLiked = p.LikedBy.contains(currentUserId);
    if (isLiked) {
        p.unlike(currentUserId);
    } else {
        p.like(currentUserId);
    }
}else{
    p.like(currentUserId.toString());
}


    postCollections.document(posId).set(p);
   }
         catch (Exception e){Log.d("ApnaInsta" , e.getMessage());}
     } } ).start();

}











}



