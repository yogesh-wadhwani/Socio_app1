package com.example.apnainsta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apnainsta.daos.PostDao;
import com.example.apnainsta.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements postAdapter.postlike {
FloatingActionButton fab;
private postAdapter ad;
RecyclerView recyclerView;
private PostDao postDao;
private TextView logout;
private ImageView moreButton;
final String CHANNEL_ID= "my";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
fab = findViewById(R.id.fab);
        Toolbar myToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);
moreButton = findViewById(R.id.more);
logout = findViewById(R.id.Logout);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);






moreButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(logout.getVisibility() == View.GONE){
        logout.setVisibility(View.VISIBLE);}else{
            logout.setVisibility(View.GONE);
        }

    }
});
logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
         FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }
});

fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
        startActivity(intent);

    }
});
  
setUpRecyclerView();
    }

    private void setUpRecyclerView() {
       recyclerView = findViewById(R.id.recyclerView);


       Query query =   PostDao.postCollections.orderBy("createdAt",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> recyclerViewOptions;
        recyclerViewOptions = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post.class).build();

        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));


        ad = new postAdapter(recyclerViewOptions,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ad);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ad.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ad.stopListening();
    }

    @Override
    public void onLikeClicked(String posId) {
        PostDao.updateLikes(posId);

    }
}