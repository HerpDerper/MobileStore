package com.example.mobilestore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Activities.AddUpdateCategoryActivity;
import com.example.mobilestore.Activities.AddUpdateManufacturerActivity;
import com.example.mobilestore.Activities.AddUpdateProductActivity;
import com.example.mobilestore.Adapters.CartAdminAdapter;
import com.example.mobilestore.Adapters.CategoryAdapter;
import com.example.mobilestore.Adapters.CommentAdminAdapter;
import com.example.mobilestore.Adapters.ManufacturerAdapter;
import com.example.mobilestore.Adapters.ProductAdminAdapter;
import com.example.mobilestore.Adapters.UserAdapter;
import com.example.mobilestore.Models.Cart;
import com.example.mobilestore.Models.Category;
import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.Models.Manufacturer;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.Models.User;
import com.example.mobilestore.R;
import com.example.mobilestore.databinding.FragmentDataAdminBinding;
import com.example.mobilestore.databinding.FragmentDataSellerBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DataSellerFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FragmentDataSellerBinding binding;
    FloatingActionButton btnAddData;
    RecyclerView recyclerView;
    private ProductAdminAdapter adapterProduct;
    private CategoryAdapter adapterCategory;
    private ManufacturerAdapter adapterManufacturer;
    private String adapterName = "Products";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDataSellerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerAll;
        btnAddData = binding.btnAddData;
        btnAddData.setOnClickListener(view -> {
            switch (adapterName) {
                case "Products": {
                    startActivity(new Intent(getActivity().getApplicationContext(), AddUpdateProductActivity.class));
                    break;
                }
                case "Categories": {
                    startActivity(new Intent(getActivity().getApplicationContext(), AddUpdateCategoryActivity.class));
                    break;
                }
                case "Manufacturers": {
                    startActivity(new Intent(getActivity().getApplicationContext(), AddUpdateManufacturerActivity.class));
                    break;
                }
            }
        });
        setAdapters();
        setRecyclerView(adapterName);
        adapterProduct.startListening();
        adapterCategory.startListening();
        adapterManufacturer.startListening();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.optionsmenu_seller, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnProducts: {
                adapterName = "Products";
                btnAddData.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.mnCategories: {
                adapterName = "Categories";
                btnAddData.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.mnManufacturers: {
                adapterName = "Manufacturers";
                btnAddData.setVisibility(View.VISIBLE);
                break;
            }
        }
        setRecyclerView(adapterName);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapterProduct.stopListening();
        adapterCategory.stopListening();
        adapterManufacturer.stopListening();
    }

    private void setAdapters() {
        Query query = firebaseFirestore.collection("Products");
        FirestoreRecyclerOptions<Product> optionsProduct = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        query = firebaseFirestore.collection("Categories");
        FirestoreRecyclerOptions<Category> optionsCategory = new FirestoreRecyclerOptions.Builder<Category>()
                .setQuery(query, Category.class)
                .build();
        query = firebaseFirestore.collection("Manufacturers");
        FirestoreRecyclerOptions<Manufacturer> optionsManufacturer = new FirestoreRecyclerOptions.Builder<Manufacturer>()
                .setQuery(query, Manufacturer.class)
                .build();

        adapterProduct = new ProductAdminAdapter(optionsProduct);
        adapterCategory = new CategoryAdapter(optionsCategory);
        adapterManufacturer = new ManufacturerAdapter(optionsManufacturer);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void setRecyclerView(String adapterName) {
        switch (adapterName) {

            case "Products": {
                recyclerView.setAdapter(adapterProduct);
                break;
            }
            case "Categories": {
                recyclerView.setAdapter(adapterCategory);
                break;
            }
            case "Manufacturers": {
                recyclerView.setAdapter(adapterManufacturer);
                break;
            }
        }
    }
}
