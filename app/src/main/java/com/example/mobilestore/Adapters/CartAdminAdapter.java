package com.example.mobilestore.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Cart;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.Models.User;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdminAdapter extends FirestoreRecyclerAdapter<Cart, CartAdminAdapter.CartAdminHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public CartAdminAdapter(@NonNull FirestoreRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdminHolder holder, int position, @NonNull Cart model) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(model.getProductName());
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            holder.txtPrice.setText(String.valueOf(product.getPrice()) + "₽");
            holder.txtProductCount.setText(String.valueOf(model.getProductCount()));
            holder.txtProductName.setText(product.getProductName());
            holder.txtExtraInfo.setText(product.getCategoryName() + " " + product.getManufacturerName());
            Picasso.get()
                    .load(product.getProductImage())
                    .into(holder.imgProductImage);
        });
        DocumentReference userReference = firebaseFirestore.collection("Users").document(model.getUserName());
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            holder.txtEmail.setText(user.getEmail());
            holder.txtLogin.setText(user.getLogin());
            Picasso.get()
                    .load(user.getAvatar())
                    .into(holder.imgAvatar);
        });
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public CartAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_cart_admin,
                parent, false);
        return new CartAdminHolder(view);
    }

    class CartAdminHolder extends RecyclerView.ViewHolder {

        TextView txtProductCount, txtPrice, txtExtraInfo, txtProductName, txtLogin, txtEmail;
        Button btnMore;
        ImageView imgProductImage;
        CircleImageView imgAvatar;

        public CartAdminHolder(View itemView) {
            super(itemView);
            txtProductCount = itemView.findViewById(R.id.txtProductCount);
            txtLogin = itemView.findViewById(R.id.txtLogin);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtExtraInfo = itemView.findViewById(R.id.txtExtraInfo);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            btnMore = itemView.findViewById(R.id.btnMore);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            btnMore.setOnClickListener(this::showPopupMenu);
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
            popupMenu.inflate(R.menu.popupmenu_delete);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.mnDelete:
                        deleteItem(getAdapterPosition());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }
}