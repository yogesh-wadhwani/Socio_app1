package com.example.apnainsta;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.apnainsta.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.apnainsta.daos.PostDao.postCollections;

public class postAdapter extends FirestoreRecyclerAdapter<Post, postAdapter.postViewHolder>{

    final FirebaseAuth mAuth =FirebaseAuth.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private  postlike listner;
    public postAdapter(@NonNull FirestoreRecyclerOptions<Post> options,postlike listner) {
        super(options);
        this.listner = listner;
    }


    @Override
    protected void onBindViewHolder(@NonNull postViewHolder holder, int position, @NonNull Post model) {


    holder.postText.setText(model.text);
    holder.userName.setText(model.createdBy.displayName);

         if (mAuth.getCurrentUser().getUid().equals(model.createdBy.uid) ) {
             holder.deleteButton.setVisibility(View.VISIBLE);

             holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     DocumentSnapshot snapshot = getSnapshots()
                             .getSnapshot(holder.getAdapterPosition());

                     postCollections.document(snapshot.getId()).delete();
                     Toast.makeText( holder.deleteButton.getContext(),"Post Deleted" ,Toast.LENGTH_SHORT).show();
                 }
             });
         }else{
             holder.deleteButton.setVisibility(View.INVISIBLE);

         }

        Glide.with(holder.userImage.getContext()).load(model.createdBy.imageURL).circleCrop().into(holder.userImage);
    if(!model.hasVideo) {
            Glide.with(holder.postImage.getContext()).load(model.postImage).into(holder.postImage);
        }else {
        holder.playButton.setVisibility(View.VISIBLE);
       // RequestOptions requestOptions = new RequestOptions();
        Glide.with( holder.postImage.getContext() )
                .load( Uri.parse(model.postImage))
                .into( holder.postImage );

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.postImage.getContext(),VideoPlayActivity.class);
                intent.putExtra("videoURL", model.postImage);
             startActivity(holder.postImage.getContext(),intent,null);
            }
        });
}

     if(model.LikedBy.size() >0){ holder.likeCount.setText(String.valueOf(model.LikedBy.size()));}else{ holder.likeCount.setText("");}
        if(model.comments.size() > 0){ holder.commentCount.setText(String.valueOf(model.comments.size()));}else{ holder.commentCount.setText("");}
        holder.createdAt.setText(Utils.toDuration(System.currentTimeMillis() - model.createdAt));

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


       viewHolder.commmentButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(viewHolder.commmentButton.getContext(), CommentsActivity.class);
               DocumentSnapshot snapshot = getSnapshots()
                       .getSnapshot(viewHolder.getAdapterPosition());
               intent.putExtra("postRef", snapshot.getId() );
               startActivity(viewHolder.commmentButton.getContext(),intent,null);
           }
       });


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
TextView postText,userName,createdAt,likeCount,commentCount;
ImageView userImage,likeButton;
ImageView postImage,playButton,deleteButton,commmentButton;





    public postViewHolder(@NonNull View itemView) {
        super(itemView);
        postText = itemView.findViewById(R.id.postText);
       userName = itemView.findViewById(R.id.userName);
        createdAt = itemView.findViewById(R.id.createdAt);
        likeCount = itemView.findViewById(R.id.likeCount);
        likeButton = itemView.findViewById(R.id.likeButton);
        userImage = itemView.findViewById(R.id.userImage);
      postImage = itemView.findViewById(R.id.postImage);
     playButton = itemView.findViewById(R.id.playButton);
 deleteButton = itemView.findViewById(R.id.deleteButton);
   commmentButton = itemView.findViewById(R.id.commentButton);
   commentCount = itemView.findViewById(R.id.commentCount);
    }
}

interface postlike{
        void onLikeClicked(String posId);
}
}
