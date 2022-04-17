package com.example.mobilestore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CommentAdminAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdminAdapter.CommentAdminHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public CommentAdminAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentAdminHolder holder, int position, @NonNull Comment model) {
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
    public CommentAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_comment_user,
                parent, false);
        return new CommentAdminHolder(view);
    }

    public String commentInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CommentAdminHolder extends RecyclerView.ViewHolder {
        TextView txtText, txtLikeCount, txtLogin, txtTimeComment;
        ImageView imgAvatar;
        Button btnMore;
        RatingBar rtnRating;

        public CommentAdminHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtText = itemView.findViewById(R.id.txtText);
            txtTimeComment = itemView.findViewById(R.id.txtTimeComment);
            txtLogin = itemView.findViewById(R.id.txtLogin);
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            rtnRating = itemView.findViewById(R.id.rtnRating);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnMore.setOnClickListener(this::showPopupMenu);
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
            popupMenu.inflate(R.menu.popupmenu_full);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.mnUpdate:
                        Context context = itemView.getContext();

                        return true;
                    case R.id.mnDelete:
                        deleteCommentLikes(commentInformation(getAdapterPosition()));
                        deleteItem(getAdapterPosition());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }

        private void deleteCommentLikes(String id) {
            Query query = firebaseFirestore.collection("CommentLikes")
                    .whereEqualTo("commentName", id);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document(document.getId());
                        commentLikeReference.delete();
                    }
                }
            });
        }
    }
}