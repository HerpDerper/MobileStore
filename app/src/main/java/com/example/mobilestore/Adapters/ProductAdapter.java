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
import com.example.mobilestore.ProductInfoActivity;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        holder.txtProductName.setText(model.getProductName());
        Picasso.get()
                .load(model.getProductImage())
                .resize(90, 90)
                .into(holder.imgProductImage);
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_product,
                parent, false);
        return new ProductHolder(v);
    }

    public String productInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        ImageView imgProductImage;
        TextView txtProductName;

        public ProductHolder(View itemView) {
            super(itemView);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ProductInfoActivity.class);
                    intent.putExtra("IdProduct", productInformation(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}