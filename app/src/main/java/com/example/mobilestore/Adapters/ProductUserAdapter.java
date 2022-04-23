package com.example.mobilestore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Product;
import com.example.mobilestore.Activities.ProductInfoActivity;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ProductUserAdapter extends FirestoreRecyclerAdapter<Product, ProductUserAdapter.ProductUserHolder> {

    public ProductUserAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductUserHolder holder, int position, @NonNull Product model) {
        holder.txtProductName.setText(model.getProductName());
        Picasso.get()
                .load(model.getProductImage())
                .into(holder.imgProductImage);
    }

    @NonNull
    @Override
    public ProductUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_product_user,
                parent, false);
        return new ProductUserHolder(v);
    }

    public String productInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    class ProductUserHolder extends RecyclerView.ViewHolder {
        ImageView imgProductImage;
        TextView txtProductName;

        public ProductUserHolder(View itemView) {
            super(itemView);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            itemView.setOnClickListener(view -> {
                Context context = itemView.getContext();
                context.startActivity(new Intent(context, ProductInfoActivity.class).putExtra("IdProduct", productInformation(getAdapterPosition())));
            });
        }
    }
}