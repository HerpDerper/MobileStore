package com.example.mobilestore.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Models.Category;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryHolder> {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryAdapter.CategoryHolder holder, int position, @NonNull Category model) {
        holder.txtCategoryName.setText(  model.getCategoryName());
    }

    public String categoryInformation(int position) {
        return getSnapshots().getSnapshot(position).getReference().getId();
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_category,
                parent, false);
        return new CategoryAdapter.CategoryHolder(view);
    }

    class CategoryHolder extends RecyclerView.ViewHolder {

        TextView txtCategoryName;
        Button btnMore;

        public CategoryHolder(View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnMore.setOnClickListener(this::showPopupMenu);
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
            popupMenu.inflate(R.menu.popupmenu_full);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.mnUpdate:

                        return true;
                    case R.id.mnDelete:
                        deleteProducts(txtCategoryName.getText().toString());
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
                .whereEqualTo("categoryName", id);
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