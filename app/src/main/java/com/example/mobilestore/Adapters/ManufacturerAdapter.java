package com.example.mobilestore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Activities.AddUpdateCategoryActivity;
import com.example.mobilestore.Activities.AddUpdateManufacturerActivity;
import com.example.mobilestore.Models.Manufacturer;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ManufacturerAdapter extends FirestoreRecyclerAdapter<Manufacturer, ManufacturerAdapter.ManufacturerHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public ManufacturerAdapter(@NonNull FirestoreRecyclerOptions<Manufacturer> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ManufacturerAdapter.ManufacturerHolder holder, int position, @NonNull Manufacturer model) {
        holder.txtManufacturerName.setText(model.getManufacturerName());
        holder.txtAddress.setText(model.getAddress());
    }

    public String manufacturerInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public ManufacturerAdapter.ManufacturerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_manufacturer,
                parent, false);
        return new ManufacturerAdapter.ManufacturerHolder(view);
    }

    class ManufacturerHolder extends RecyclerView.ViewHolder {

        TextView txtManufacturerName, txtAddress;
        Button btnMore;

        public ManufacturerHolder(View itemView) {
            super(itemView);
            txtManufacturerName = itemView.findViewById(R.id.txtManufacturerName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
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
                        context.startActivity(new Intent(context, AddUpdateManufacturerActivity.class).putExtra("IdManufacturer", manufacturerInformation(getAdapterPosition())));
                        return true;
                    case R.id.mnDelete:
                        deleteProducts(txtManufacturerName.getText().toString());
                        deleteItem(getAdapterPosition());
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    private void deleteProducts(String id) {
        Query query = firebaseFirestore.collection("Products")
                .whereEqualTo("manufacturerName", id);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = firebaseFirestore.collection("Products").document(document.getId());
                    deleteComments(document.getId());
                    deleteCarts(document.getId());
                    productReference.delete();
                }
            }
        });
    }

    private void deleteComments(String id) {
        Query query = firebaseFirestore.collection("Comments")
                .whereEqualTo("productName", id);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentReference = firebaseFirestore.collection("Comments").document(document.getId());
                    deleteCommentLikes(document.getId());
                    commentReference.delete();
                }
            }
        });
    }

    private void deleteCarts(String id) {
        Query query = firebaseFirestore.collection("Carts")
                .whereEqualTo("productName", id);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference cartReference = firebaseFirestore.collection("Carts").document(document.getId());
                    cartReference.delete();
                }
            }
        });
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