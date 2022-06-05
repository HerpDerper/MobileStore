package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mobilestore.Models.Product;
import com.example.mobilestore.R;
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

public class AddUpdateProductActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    EditText txtProductName, txtDescription, txtGuarantee, txtPrice, txtProductCount;
    Spinner spnManufacturerName, spnCategoryName;
    Button btnProductAddUpdate;
    ImageView imgProductImage;
    Bundle bundle;
    List<String> listManufacturers;
    List<String> listCategories;
    ArrayAdapter<String> adapterManufacturers;
    ArrayAdapter<String> adapterCategories;
    private int ratingCount;
    private float rating;
    private String IdProduct;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_product);
        initialize();
        setManufacturerData();
        setCategoryData();
    }

    public void productAddUpdateClick(View view) {
        if (TextUtils.isEmpty(txtProductName.getText())) {
            Toast.makeText(this, "Не введено название", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtProductCount.getText())) {
            Toast.makeText(this, "Не введено количество", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtPrice.getText())) {
            Toast.makeText(this, "Не введена цена", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtGuarantee.getText())) {
            Toast.makeText(this, "Не введена гарантия", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtDescription.getText())) {
            Toast.makeText(this, "Не введено описание", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(txtProductCount.getText().toString()) >= Integer.MAX_VALUE || Integer.parseInt(txtProductCount.getText().toString()) <= Integer.MIN_VALUE) {
            Toast.makeText(this, "Некорректный ввод количества", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Float.parseFloat(txtPrice.getText().toString()) >= Float.MAX_VALUE || Float.parseFloat(txtPrice.getText().toString()) <= Float.MIN_VALUE) {
            Toast.makeText(this, "Некорректный ввод цены", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btnProductAddUpdate.getText().equals("Добавить")) {
            uploadImage();
        } else {
            uploadImage(IdProduct);
        }
        finish();
    }


    public void cancelClick(View view) {
        finish();
    }

    public void setImageClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    private void initialize() {
        txtProductName = findViewById(R.id.txtProductName);
        txtDescription = findViewById(R.id.txtDescription);
        txtGuarantee = findViewById(R.id.txtGuarantee);
        txtPrice = findViewById(R.id.txtPrice);
        txtProductCount = findViewById(R.id.txtProductCount);
        spnManufacturerName = findViewById(R.id.spnManufacturerName);
        spnCategoryName = findViewById(R.id.spnCategoryName);
        imgProductImage = findViewById(R.id.imgProductImage);
        btnProductAddUpdate = findViewById(R.id.btnProductAddUpdate);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            IdProduct = bundle.getString("IdProduct");
            getIncomingIntent(IdProduct);
            btnProductAddUpdate.setText("Изменить");
        }
    }

    private void setManufacturerData() {
        listManufacturers = new ArrayList<>();
        adapterManufacturers = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listManufacturers);
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
        adapterCategories = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listCategories);
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

    private void insertProduct() {
        DocumentReference productReference = firebaseFirestore.collection("Products").document();
        Product product = new Product(txtProductName.getText().toString().trim(), spnCategoryName.getSelectedItem().toString(),
                txtDescription.getText().toString().trim(), txtGuarantee.getText().toString().trim(),
                spnManufacturerName.getSelectedItem().toString(), imageUri.toString(),
                Integer.parseInt(txtProductCount.getText().toString().trim()), 0, 0, Float.parseFloat(txtPrice.getText().toString().trim()));
        productReference.set(product);
    }

    public void uploadImage() {
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

    public void uploadImage(String id) {
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
        DocumentReference productReference = firebaseFirestore.collection("Products").document(id);
        Product product = new Product(txtProductName.getText().toString().trim(), spnCategoryName.getSelectedItem().toString(),
                txtDescription.getText().toString().trim(), txtGuarantee.getText().toString().trim(),
                spnManufacturerName.getSelectedItem().toString(), imageUri.toString(),
                Integer.parseInt(txtProductCount.getText().toString().trim()), ratingCount, rating, Float.parseFloat(txtPrice.getText().toString().trim()));
        productReference.set(product);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK)
            if (requestCode == 1) {
                Uri selectedImage = imageReturnedIntent.getData();
                imgProductImage.setImageURI(selectedImage);
            }
    }

    public void getIncomingIntent(String id) {
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
                    .into(imgProductImage);
        });
    }
}