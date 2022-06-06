package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilestore.Models.Category;
import com.example.mobilestore.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AddUpdateCategoryActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    EditText txtCategoryName;
    Button btnCategoryAddUpdate;
    Bundle bundle;
    private String IdCategory;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_category);
        initialize();
    }

    private void initialize() {
        txtCategoryName = findViewById(R.id.txtCategoryName);
        btnCategoryAddUpdate = findViewById(R.id.btnCategoryAddUpdate);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            IdCategory = bundle.getString("IdCategory");
            getIncomingIntent(IdCategory);
            btnCategoryAddUpdate.setText("Изменить");
        }
    }

    public void categoryAddUpdateClick(View view) {
        if (!txtCategoryName.getText().toString().trim().matches("[a-zA-Zа-яА-Я]{1,30}")) {
            Toast.makeText(this, "Некорректный ввод наименования", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtCategoryName.getText())) {
            Toast.makeText(this, "Не введено наименования", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btnCategoryAddUpdate.getText().equals("Добавить")) {
            insertCategory();
        } else {
            updateCategory(IdCategory);
        }
        finish();
    }

    public void cancelClick(View view) {
        finish();
    }

    private void insertCategory() {
        DocumentReference categoryReference = firebaseFirestore.collection("Categories").document();
        Category category = new Category(txtCategoryName.getText().toString().trim());
        categoryReference.set(category);
    }

    private void updateCategory(String id) {
        DocumentReference categoryReference = firebaseFirestore.collection("Categories").document(id);
        Category category = new Category(txtCategoryName.getText().toString().trim());
        categoryReference.set(category);
        updateProducts();
    }

    private void updateProducts() {
        Query query = firebaseFirestore.collection("Products")
                .whereEqualTo("categoryName", categoryName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = firebaseFirestore.collection("Products").document(document.getId());
                    productReference.update("categoryName", txtCategoryName.getText().toString().trim());
                }
            }
        });
    }

    private void getIncomingIntent(String id) {
        DocumentReference productReference = firebaseFirestore.collection("Categories").document(id);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Category category = documentSnapshot.toObject(Category.class);
            categoryName = category.getCategoryName();
            txtCategoryName.setText(categoryName);
        });
    }
}