package com.example.apnainsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apnainsta.daos.PostDao;
import com.example.apnainsta.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreatePostActivity extends AppCompatActivity {
    static final int PICK_IMAGE = 8989;
Button postButton;
EditText postInput;
ImageView postImagePicker;
    PostDao postDao;
    Bitmap bitmap;
    String imageURL;
    boolean hasVideo = true;
    private  String input;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
    postButton =findViewById(R.id.postButton);
    postInput = findViewById(R.id.postInput);
    postImagePicker = findViewById(R.id.postImagePicker);
    postDao = new PostDao();

    postImagePicker.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

        }

    });


    postButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(postInput.getText() != null)
                input = postInput.getText().toString().trim();

            if(input.isEmpty() && bitmap!=null  ) {
                Toast.makeText(getApplicationContext(),"Please add caption!",Toast.LENGTH_SHORT).show();
            }else if(!input.isEmpty() && bitmap==null ){
                Toast.makeText(getApplicationContext(),"Please add Image to post!",Toast.LENGTH_SHORT).show();
            }else if(input.isEmpty() && bitmap==null ) {
                Toast.makeText(getApplicationContext(),"Post cannot be empty!",Toast.LENGTH_SHORT).show();
            }


        if(!input.isEmpty() && bitmap!=null  ){
            // uploadImage();
            PostDao postDao = new PostDao();
            postDao.addPost(input,imageURL,hasVideo);
            Toast.makeText(getApplicationContext(),"Post Added",Toast.LENGTH_SHORT).show();
            finish();

        }
        }
    });
    }

//    private void uploadImage() {
//
//
//    }

    private String getImageExtention(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
      return   mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return; }

            imageUri =data.getData();


            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
              bitmap = BitmapFactory.decodeStream(inputStream);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(imageUri != null) {
          Glide.with(postImagePicker.getContext()).asBitmap().load(bitmap).into(postImagePicker);
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Uploading ....");
            pd.show();

            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("Post Images").child(System.currentTimeMillis() +"."+ getImageExtention(imageUri));
            imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL = uri.toString();
                            if(imageURL.contains(".jpg") || imageURL.contains(".png")){
                                hasVideo = false;
                            }
                            Log.d( "Apna", imageURL + hasVideo);
                            pd.dismiss();
                        }
                    });
                }

            });
     }

    }
}