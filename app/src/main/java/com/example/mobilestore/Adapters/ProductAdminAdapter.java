package com.example.mobilestore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Activities.ProductInfoActivity;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.Activities.ProductAddUpdateActivity;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ProductAdminAdapter extends FirestoreRecyclerAdapter<Product, ProductAdminAdapter.ProductAdminHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public ProductAdminAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductAdminHolder holder, int position, @NonNull Product model) {
        holder.txtProductName.setText(model.getProductName());
        holder.txtCategoryName.setText(model.getCategoryName());
        holder.txtPrice.setText(String.valueOf(model.getPrice()));
        holder.txtManufacturerName.setText(model.getManufacturerName());
        Picasso.get()
                .load(model.getProductImage())
                .resize(90, 90)
                .into(holder.imgProductImage);
    }

    @NonNull
    @Override
    public ProductAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_product_admin,
                parent, false);
        return new ProductAdminHolder(v);
    }

    public String productInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ProductAdminHolder extends RecyclerView.ViewHolder {
        ImageView imgProductImage;
        TextView txtProductName, txtPrice, txtCategoryName, txtManufacturerName;
        Button btnMore;

        public ProductAdminHolder(View itemView) {
            super(itemView);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnMore.setOnClickListener(this::showPopupMenu);
            txtManufacturerName = itemView.findViewById(R.id.txtManufacturerName);
            itemView.setOnClickListener(view -> {
                Context context = itemView.getContext();
                context.startActivity(new Intent(context, ProductInfoActivity.class).putExtra("IdProduct",
                        (getAdapterPosition())).putExtra("Role", "Admin"));
            });
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
            popupMenu.inflate(R.menu.popupmenu_full);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.mnUpdate:
                        Context context = itemView.getContext();
                        context.startActivity(new Intent(context, ProductAddUpdateActivity.class).putExtra("IdProduct", productInformation(getAdapterPosition())));
                        return true;
                    case R.id.mnDelete:
                        deleteComments(productInformation(getAdapterPosition()));
                        deleteItem(getAdapterPosition());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    private void deleteComments(String id) {
        Query query = firebaseFirestore.collection("Comments")
                .whereEqualTo("productName", id);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentReference = firebaseFirestore.collection("Comments").document(document.getId());
                    commentReference.delete();
                }
            }
        });
    }
}