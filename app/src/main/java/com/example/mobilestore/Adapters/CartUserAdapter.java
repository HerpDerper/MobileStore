package com.example.mobilestore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Cart;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.Activities.ProductInfoActivity;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CartUserAdapter extends FirestoreRecyclerAdapter<Cart, CartUserAdapter.CartUserHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private int productCount;

    public CartUserAdapter(@NonNull FirestoreRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartUserHolder holder, int position, @NonNull Cart model) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(model.getProductName());
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            productCount = product.getProductCount();
            holder.txtPrice.setText(String.valueOf(product.getPrice()) + "₽");
            productCount = product.getProductCount();
            holder.txtPrice.setText(String.valueOf(product.getPrice()) + "₽");
            holder.txtProductCount.setText(String.valueOf(model.getProductCount()));
            holder.txtProductName.setText(product.getProductName());
            holder.txtExtraInfo.setText(product.getCategoryName() + " " + product.getManufacturerName());
            Picasso.get()
                    .load(product.getProductImage())
                    .into(holder.imgProductImage);
        });
    }

    public String cartInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void updateItem(int position, int count) {
        getSnapshots().getSnapshot(position).getReference().update("productCount", count);
    }

    @NonNull
    @Override
    public CartUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_cart_user,
                parent, false);
        return new CartUserHolder(view);
    }

    class CartUserHolder extends RecyclerView.ViewHolder {

        TextView txtProductCount, txtPrice, txtExtraInfo, txtProductName;
        Button btnMinus, btnPlus;
        ImageView imgProductImage;

        public CartUserHolder(View itemView) {
            super(itemView);
            txtProductCount = itemView.findViewById(R.id.txtProductCount);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtExtraInfo = itemView.findViewById(R.id.txtExtraInfo);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            itemView.setOnClickListener(view -> {
                Context context = itemView.getContext();
                DocumentReference cartReference = firebaseFirestore.collection("Carts").document(cartInformation(getAdapterPosition()));
                cartReference.get().addOnSuccessListener(documentSnapshot -> {
                    Intent intent = new Intent(context, ProductInfoActivity.class);
                    Cart cart = documentSnapshot.toObject(Cart.class);
                    intent.putExtra("IdProduct", cart.getProductName());
                    context.startActivity(intent);
                });
            });
            btnPlus.setOnClickListener(view -> {
                if (Integer.parseInt(txtProductCount.getText().toString()) == productCount) {
                    btnPlus.setEnabled(false);
                    return;
                }
                btnMinus.setEnabled(true);
                updateItem(getAdapterPosition(), Integer.parseInt(txtProductCount.getText().toString()) + 1);
            });
            btnMinus.setOnClickListener(view -> {
                if (Integer.parseInt(txtProductCount.getText().toString()) == 1) {
                    btnMinus.setEnabled(false);
                    return;
                }
                btnPlus.setEnabled(true);
                updateItem(getAdapterPosition(), Integer.parseInt(txtProductCount.getText().toString()) - 1);
            });
        }
    }
}