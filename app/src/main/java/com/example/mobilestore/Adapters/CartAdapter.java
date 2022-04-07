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
import com.example.mobilestore.ProductInfoActivity;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CartAdapter extends FirestoreRecyclerAdapter<Cart, CartAdapter.CartHolder> {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private int productCount;

    public CartAdapter(@NonNull FirestoreRecyclerOptions<Cart> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartHolder holder, int position, @NonNull Cart model) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(model.getProductName());
        productReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                productCount = product.getProductCount();
                holder.txtPrice.setText(String.valueOf(product.getPrice()) + "₽");
                holder.txtProductCount.setText(String.valueOf(model.getProductCount()));
                holder.txtProductName.setText(product.getProductName());
                holder.txtExtraInfo.setText(product.getCategoryName() + " " + product.getManufacturerName());
                Picasso.get()
                        .load(product.getProductImage())
                        .into(holder.imgProductImage);
            }
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
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_cart,
                parent, false);
        return new CartHolder(view);
    }

    class CartHolder extends RecyclerView.ViewHolder {

        TextView txtProductCount, txtPrice, txtExtraInfo, txtProductName;
        Button btnMinus, btnPlus;
        ImageView imgProductImage;

        public CartHolder(View itemView) {
            super(itemView);
            txtProductCount = itemView.findViewById(R.id.txtProductCount);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtExtraInfo = itemView.findViewById(R.id.txtExtraInfo);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    DocumentReference cartReference = firebaseFirestore.collection("Carts").document(cartInformation(getAdapterPosition()));
                    cartReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent = new Intent(context, ProductInfoActivity.class);
                            Cart cart = documentSnapshot.toObject(Cart.class);
                            intent.putExtra("IdProduct", cart.getProductName());
                            context.startActivity(intent);
                        }
                    });
                }
            });
            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(txtProductCount.getText().toString()) == productCount) {
                        btnPlus.setEnabled(false);
                        return;
                    }
                    btnMinus.setEnabled(true);
                    updateItem(getAdapterPosition(), Integer.parseInt(txtProductCount.getText().toString()) + 1);
                }
            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(txtProductCount.getText().toString()) == 1) {
                        btnMinus.setEnabled(false);
                        return;
                    }
                    btnPlus.setEnabled(true);
                    updateItem(getAdapterPosition(), Integer.parseInt(txtProductCount.getText().toString()) - 1);
                }
            });
        }
    }
}