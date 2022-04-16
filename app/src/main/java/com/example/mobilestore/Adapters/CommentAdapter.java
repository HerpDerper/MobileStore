package com.example.mobilestore.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.Models.CommentLikes;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder> {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private int documentCount;
    private String commentLikeId;
    private String commentId;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder holder, int position, @NonNull Comment model) {
        holder.txtText.setText(model.getText());
        holder.txtTimeComment.setText(model.getTimeComment());
        holder.txtLikeCount.setText(String.valueOf(model.getLikeCount()));
        DocumentReference userReference = firebaseFirestore.collection("Users").document(model.getUserName());
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            holder.txtLogin.setText(documentSnapshot.getString("login"));
            Picasso.get()
                    .load(documentSnapshot.getString("avatar"))
                    .into(holder.imgAvatar);
        });
        holder.rtnRating.setRating(model.getRating());
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_comment,
                parent, false);
        return new CommentHolder(view);
    }

    public String commentInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    class CommentHolder extends RecyclerView.ViewHolder {
        TextView txtText, txtLikeCount, txtLogin, txtTimeComment;
        ImageView imgAvatar;
        ImageButton btnLike;
        RatingBar rtnRating;

        public CommentHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtText = itemView.findViewById(R.id.txtText);
            txtTimeComment = itemView.findViewById(R.id.txtTimeComment);
            txtLogin = itemView.findViewById(R.id.txtLogin);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            rtnRating = itemView.findViewById(R.id.rtnRating);
            btnLike.setOnClickListener(view -> {
                commentId = commentInformation(getAdapterPosition());
                Query query = firebaseFirestore.collection("CommentLikes")
                        .whereEqualTo("commentName", commentId)
                        .whereEqualTo("userName", currentUser.getUid());
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            commentLikeId = document.getId();
                        }
                        documentCount = task.getResult().size();
                        if (documentCount == 0) addCommentLike(commentId);
                        else deleteCommentLike(commentId, commentLikeId);
                    }
                });
            });
        }

        private void addCommentLike(String commentId) {
            DocumentReference commentReference = firebaseFirestore.collection("Comments").document(commentId);
            commentReference.get().addOnSuccessListener(documentSnapshot -> {
                int likeCount = Integer.parseInt(documentSnapshot.get("likeCount").toString()) + 1;
                commentReference.update("likeCount", likeCount);
            });
            DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document();
            CommentLikes commentLikes = new CommentLikes(currentUser.getUid(), commentId);
            commentLikeReference.set(commentLikes);
        }

        private void deleteCommentLike(String commentId, String commentLikeId) {
            DocumentReference commentReference = firebaseFirestore.collection("Comments").document(commentId);
            commentReference.get().addOnSuccessListener(documentSnapshot -> {
                int likeCount = Integer.parseInt(documentSnapshot.get("likeCount").toString()) - 1;
                commentReference.update("likeCount", likeCount);
            });
            DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document(commentLikeId);
            commentLikeReference.delete();
        }
    }
}