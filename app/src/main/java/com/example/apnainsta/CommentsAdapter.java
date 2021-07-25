package com.example.apnainsta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apnainsta.models.Comment;
import com.example.apnainsta.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static com.example.apnainsta.daos.PostDao.postCollections;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.commentsViewHolder> {

    private ArrayList<Comment> localDataSet;
    private String postRef;
    Post p;
    private static final FirebaseAuth mAuth =FirebaseAuth.getInstance();
    public CommentsAdapter(ArrayList<Comment> dataSet,String postRef,Post p) {
        localDataSet = dataSet;
        this.postRef = postRef;
        this.p = p;
    }

    @NonNull
    @Override
    public commentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comments_layout, viewGroup, false);


        return new commentsViewHolder(view);
    }

      @Override
      public void onBindViewHolder(@NonNull commentsViewHolder holder, int position) {

  holder.commentCreatedAt.setText(Utils.toDuration(System.currentTimeMillis() - localDataSet.get(position).createdAt));
  holder.commentText.setText(localDataSet.get(position).commentText);   //
  holder.commentUserName.setText(localDataSet.get(position).createdBy);
        Glide.with(holder.commentUserImage.getContext()).load(localDataSet.get(position).imageUrl).circleCrop().into(holder.commentUserImage);

        if (mAuth.getCurrentUser().getUid().equals(localDataSet.get(position).Uid) ) {
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.commentUserImage.setVisibility(View.INVISIBLE);
                    holder.commentUserName.setVisibility(View.INVISIBLE);
                    holder.commentText.setVisibility(View.INVISIBLE);
                    holder.commentCreatedAt.setVisibility(View.INVISIBLE);
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                    p.comments.remove(localDataSet.get(position));
                    postCollections.document(postRef).set(p);

                    Toast.makeText( holder.deleteButton.getContext(),"Comment Deleted" ,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            holder.deleteButton.setVisibility(View.INVISIBLE);

        }
    
    }

    @Override
    public int getItemCount() {
       return localDataSet.size();
    }

    public static class commentsViewHolder extends  RecyclerView.ViewHolder{
    TextView commentCreatedAt,commentUserName,commentText;
    ImageView commentUserImage,deleteButton;

        public commentsViewHolder(@NonNull View itemView) {
            super(itemView);
            commentCreatedAt = itemView.findViewById(R.id.commentCreatedAt);
            commentText = itemView.findViewById(R.id.commentText);
            commentUserImage= itemView.findViewById(R.id.commentUserImage);
            commentUserName = itemView.findViewById(R.id.commentUserName);
            deleteButton = itemView.findViewById(R.id.commentDeleteButton);

        }
    }



}
