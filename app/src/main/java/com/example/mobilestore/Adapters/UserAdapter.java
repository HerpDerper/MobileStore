package com.example.mobilestore.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.User;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {

    StringBuilder builder = new StringBuilder();;
    private AlertDialog.Builder dialog;
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.txtLogin.setText(model.getLogin());
        holder.txtRoleName.setText(model.getRoleName());
        holder.txtEmail.setText(model.getEmail());
        Picasso.get()
                .load(model.getAvatar())
                .into(holder.imgAvatar);
    }

    public String userInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_user,
                parent, false);
        return new UserHolder(view);
    }

    class UserHolder extends RecyclerView.ViewHolder {

        TextView txtLogin, txtEmail, txtRoleName;
        CircleImageView imgAvatar;

        public UserHolder(View itemView) {
            super(itemView);
            txtLogin = itemView.findViewById(R.id.txtLogin);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRoleName = itemView.findViewById(R.id.txtRoleName);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            itemView.setOnClickListener(view -> {

            });
        }
    }
}