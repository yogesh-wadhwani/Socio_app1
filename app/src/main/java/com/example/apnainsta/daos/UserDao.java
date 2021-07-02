package com.example.apnainsta.daos;

import com.example.apnainsta.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;


public class UserDao {

  private  FirebaseFirestore db = FirebaseFirestore.getInstance();


   private CollectionReference usersCollection =  db.collection("users");


   public void addUser(User user){
        if(user != null){
            new Thread( new Runnable() { @Override public void run() {

                usersCollection.document(user.uid).set(user);
            } } ).start();
        }

    }

public Task<DocumentSnapshot> getUserData(String uID){
return usersCollection.document(uID).get();
}




}
