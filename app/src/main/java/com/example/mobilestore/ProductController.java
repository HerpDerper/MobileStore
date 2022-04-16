package com.example.mobilestore;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mobilestore.Models.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    List<String> listManufacturers;
    List<String> listCategories;
    ArrayAdapter<String> adapterManufacturers;
    ArrayAdapter<String> adapterCategories;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText txtProductName, txtDescription, txtGuarantee, txtPrice, txtProductCount;
    private Spinner spnManufacturerName, spnCategoryName;
    public ImageView imgProductImage;
    private final View viewProduct;
    private final Activity activityProduct;
    Uri imageUri;
    private int ratingCount;
    private float rating;
    private String manufacturerName;
    private String categoryName;
    public String id;

    public ProductController(View viewProduct, Activity activityProduct) {
        this.viewProduct = viewProduct;
        this.activityProduct = activityProduct;
    }

    public void initialize() {
        txtProductName = viewProduct.findViewById(R.id.txtProductName);
        txtDescription = viewProduct.findViewById(R.id.txtDescription);
        txtGuarantee = viewProduct.findViewById(R.id.txtGuarantee);
        txtPrice = viewProduct.findViewById(R.id.txtPrice);
        txtProductCount = viewProduct.findViewById(R.id.txtProductCount);
        spnManufacturerName = viewProduct.findViewById(R.id.spnManufacturerName);
        spnCategoryName = viewProduct.findViewById(R.id.spnCategoryName);
        imgProductImage = viewProduct.findViewById(R.id.imgProductImage);
        setManufacturerData();
        setCategoryData();
    }

    private void setManufacturerData() {
        listManufacturers = new ArrayList<>();
        adapterManufacturers = new ArrayAdapter<>(activityProduct.getApplicationContext(), android.R.layout.simple_spinner_item, listManufacturers);
        adapterManufacturers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnManufacturerName.setAdapter(adapterManufacturers);
        firebaseFirestore.collection("Manufacturers").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                String manufacturerName = document.getString("manufacturerName");
                listManufacturers.add(manufacturerName);
            }
            adapterManufacturers.notifyDataSetChanged();
        });
    }

    private void setCategoryData() {
        listCategories = new ArrayList<>();
        adapterCategories = new ArrayAdapter<>(activityProduct.getApplicationContext(), android.R.layout.simple_spinner_item, listCategories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoryName.setAdapter(adapterCategories);
        firebaseFirestore.collection("Categories").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                String manufacturerName = document.getString("categoryName");
                listCategories.add(manufacturerName);
            }
            adapterCategories.notifyDataSetChanged();
        });
    }

    public void uploadImage() {
        if (TextUtils.isEmpty(txtProductName.getText())) {
            Toast.makeText(activityProduct, "Не введено название", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtProductCount.getText())) {
            Toast.makeText(activityProduct, "Не введено количество", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtPrice.getText())) {
            Toast.makeText(activityProduct, "Не введена цена", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtGuarantee.getText())) {
            Toast.makeText(activityProduct, "Не введена гарантия", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtDescription.getText())) {
            Toast.makeText(activityProduct, "Не введено описание", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) imgProductImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference reference = storageReference.child(((BitmapDrawable) imgProductImage.getDrawable()).getBitmap().toString().split("@")[1] + "_image");
        UploadTask uploadTask = reference.putBytes(data);
        Task<Uri> task = uploadTask.continueWithTask(task2 -> reference.getDownloadUrl()).addOnCompleteListener(task1 -> {
            imageUri = task1.getResult();
            insertProduct();
        });
    }

    private void insertProduct() {
        manufacturerName = spnManufacturerName.getSelectedItem().toString();
        categoryName = spnCategoryName.getSelectedItem().toString();
        DocumentReference productReference = firebaseFirestore.collection("Products").document();
        Product product = new Product(txtProductName.getText().toString().trim(), categoryName,
                txtDescription.getText().toString().trim(), txtGuarantee.getText().toString().trim(),
                manufacturerName, imageUri.toString(),
                Integer.parseInt(txtProductCount.getText().toString().trim()), 0, 0, Float.parseFloat(txtPrice.getText().toString().trim()));
        productReference.set(product);
    }

    public void uploadImage(String id) {
        if (TextUtils.isEmpty(txtProductName.getText())) {
            Toast.makeText(activityProduct, "Не введено название", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtProductCount.getText())) {
            Toast.makeText(activityProduct, "Не введено количество", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtPrice.getText())) {
            Toast.makeText(activityProduct, "Не введена цена", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtGuarantee.getText())) {
            Toast.makeText(activityProduct, "Не введена гарантия", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtDescription.getText())) {
            Toast.makeText(activityProduct, "Не введено описание", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) imgProductImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference reference = storageReference.child(((BitmapDrawable) imgProductImage.getDrawable()).getBitmap().toString().split("@")[1] + "_image");
        UploadTask uploadTask = reference.putBytes(data);
        Task<Uri> task = uploadTask.continueWithTask(task2 -> reference.getDownloadUrl()).addOnCompleteListener(task1 -> {
            imageUri = task1.getResult();
            updateProduct(id);
        });
    }

    private void updateProduct(String id) {
        manufacturerName = spnManufacturerName.getSelectedItem().toString();
        categoryName = spnCategoryName.getSelectedItem().toString();
        DocumentReference productReference = firebaseFirestore.collection("Products").document(id);
        Product product = new Product(txtProductName.getText().toString().trim(), categoryName,
                txtDescription.getText().toString().trim(), txtGuarantee.getText().toString().trim(),
                manufacturerName, imageUri.toString(),
                Integer.parseInt(txtProductCount.getText().toString().trim()), ratingCount, rating, Float.parseFloat(txtPrice.getText().toString().trim()));
        productReference.set(product);
    }

    public void setData(String id) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(id);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            txtDescription.setText(product.getDescription());
            txtGuarantee.setText(product.getGuarantee());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtProductCount.setText(String.valueOf(product.getProductCount()));
            txtProductName.setText(product.getProductName());
            rating = product.getRating();
            ratingCount = product.getRatingCount();
            Picasso.get()
                    .load(product.getProductImage())
                    .resize(90, 90)
                    .into(imgProductImage);
            manufacturerName = product.getManufacturerName();
            categoryName = product.getCategoryName();
            this.id = id;
        });
    }
}