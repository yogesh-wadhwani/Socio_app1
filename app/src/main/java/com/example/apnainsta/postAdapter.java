package com.example.apnainsta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.example.apnainsta.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;
import com.squareup.okhttp.internal.DiskLruCache;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class postAdapter extends FirestoreRecyclerAdapter<Post, postAdapter.postViewHolder>{


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private  postlike listner;
    private  MediaPlayer player;
    public postAdapter(@NonNull FirestoreRecyclerOptions<Post> options,postlike listner) {
        super(options);
        this.listner = listner;
    }

    @Override
    protected void onBindViewHolder(@NonNull postViewHolder holder, int position, @NonNull Post model) {


    holder.postText.setText(model.text);
    holder.userName.setText(model.createdBy.displayName);

        Glide.with(holder.userImage.getContext()).load(model.createdBy.imageURL).circleCrop().into(holder.userImage);

    Glide.with(holder.postImage.getContext()).load(model.postImage).into( holder.postImage);
//   String uri = "https://firebasestorage.googleapis.com/v0/b/apna-insta-aebad.appspot.com/o/Post%20Images%2Fvideoplayback%20(1).mp4?alt=media&token=3b464d50-7705-4030-bf95-750d41a9a827";
//        player=new MediaPlayer();
//        try {
//            player.setDataSource(holder.surfaceView.getContext(), Uri.parse(uri));
//           SurfaceHolder sh=holder.surfaceView.getHolder();
//            sh.addCallback(new SurfaceHolder.Callback() {
//                @Override
//                public void surfaceCreated(@NonNull SurfaceHolder sh) {
//                    player.setDisplay(sh);
//                }
//
//                @Override
//                public void surfaceChanged(@NonNull SurfaceHolder sh, int format, int width, int height) {
//
//                }
//
//                @Override
//                public void surfaceDestroyed(@NonNull SurfaceHolder sh) {
//                }
//            });
//            holder.surfaceView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(player.isPlaying()){
//                    player.pause();}else{player.start();}
//                }
//            });
//            player.prepare();
//            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    holder.progressBar.setVisibility(View.INVISIBLE);
//                    player.start();
//                    player.setLooping(true);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//       //



      holder.likeCount.setText(String.valueOf(model.LikedBy.size()));
        holder.createdAt.setText(Utils.toDuration(System.currentTimeMillis()  - model.createdAt));
        final FirebaseAuth mAuth =FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Boolean isLikedBy = model.LikedBy.contains(currentUserId);
        if(isLikedBy){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_baseline_favorite_24_like));
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.getContext(),R.drawable.ic_baseline_favorite_unlike));
        }

    }

    @NonNull
    @Override
    public postViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       postViewHolder viewHolder = new postViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_layout, parent, false));

       viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DocumentSnapshot snapshot = getSnapshots()
                       .getSnapshot(viewHolder.getAdapterPosition());


              listner.onLikeClicked(snapshot.getId());
           }
       });
        return  viewHolder;
    }

    static class postViewHolder extends RecyclerView.ViewHolder {
TextView postText,userName,createdAt,likeCount;
ImageView userImage,likeButton;
ImageView postImage;
SurfaceView surfaceView;
ProgressBar progressBar;



    public postViewHolder(@NonNull View itemView) {
        super(itemView);
        postText = itemView.findViewById(R.id.postText);
       userName = itemView.findViewById(R.id.userName);
        createdAt = itemView.findViewById(R.id.createdAt);
        likeCount = itemView.findViewById(R.id.likeCount);
        likeButton = itemView.findViewById(R.id.likeButton);
        userImage = itemView.findViewById(R.id.userImage);
        postImage = itemView.findViewById(R.id.postImage);
    surfaceView = itemView.findViewById(R.id.surfaceView);
    progressBar = itemView.findViewById(R.id.progressBar1);
    }
}

interface postlike{
        void onLikeClicked(String posId);
}
}
