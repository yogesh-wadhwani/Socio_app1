package com.example.apnainsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.apnainsta.daos.UserDao;
import com.example.apnainsta.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    com.google.android.gms.common.SignInButton SignInButton ;
    ProgressBar progressBar ;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        SignInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBar);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d("ApnaInsta", "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w("ApnaInsta", "Google sign in failed",e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
      progressBar.setVisibility(View.VISIBLE);
      SignInButton.setVisibility(View.GONE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ApnaInsta", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                          updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ApnaInsta", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser firebaseUser) {
   if(firebaseUser != null ){
       User user = new User(firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString());
       UserDao userDao = new UserDao();
       userDao.addUser(user);

       Intent intent = new Intent(this,MainActivity.class);
          startActivity(intent);
            finish();
   }else{
       progressBar.setVisibility(View.GONE);
       SignInButton.setVisibility(View.VISIBLE);
   }

    }

}