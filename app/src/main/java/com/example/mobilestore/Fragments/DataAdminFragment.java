package com.example.mobilestore.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Adapters.ProductAdminAdapter;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.ProductController;

import com.example.mobilestore.R;
import com.example.mobilestore.databinding.FragmentDataAdminBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DataAdminFragment extends Fragment {

    static final int GALLERY_REQUEST = 1;
    View productView;
    ProductController productController;
    private FragmentDataAdminBinding binding;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton btnAddData;
    private RecyclerView recyclerView;
    private ProductAdminAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDataAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerAll;
        btnAddData = binding.btnAddData;
        btnAddData.setOnClickListener(this::showProductDialog);
        setDialog();
        setRecyclerView();
        adapter.startListening();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter.stopListening();
    }

    public void setDialog() {
        productView = getLayoutInflater().inflate(R.layout.product_view, null);
        productController = new ProductController(productView, getActivity());
        productController.initialize();
        productController.imgProductImage.setOnClickListener(view -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        });
    }

    private void showProductDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(productView).setTitle("Добавить товар");
        builder.setPositiveButton("Добавить", (dialogInterface, i) -> {
            productController.uploadImage();
        })
                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setRecyclerView() {
        Query query = firebaseFirestore.collection("Products");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        adapter = new ProductAdminAdapter(options, getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == getActivity().RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = imageReturnedIntent.getData();
                    productController.imgProductImage.setImageURI(selectedImage);
                    break;
            }
    }
}