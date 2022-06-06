package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilestore.Models.Manufacturer;
import com.example.mobilestore.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AddUpdateManufacturerActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    EditText txtManufacturerName, txtAddress;
    Button btnManufacturerAddUpdate;
    Bundle bundle;
    private String IdManufacturer;
    private String manufacturerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_manufacturer);
        initialize();
    }

    private void initialize() {
        txtManufacturerName = findViewById(R.id.txtManufacturerName);
        txtAddress = findViewById(R.id.txtAddress);
        btnManufacturerAddUpdate = findViewById(R.id.btnManufacturerAddUpdate);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            IdManufacturer = bundle.getString("IdManufacturer");
            getIncomingIntent(IdManufacturer);
            btnManufacturerAddUpdate.setText("Изменить");
        }
    }

    public void manufacturerAddUpdateClick(View view) {
        if (!txtManufacturerName.getText().toString().trim().matches("[a-zA-Zа-яА-Я]{1,30}")) {
            Toast.makeText(this, "Некорректный ввод наименования", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtManufacturerName.getText())) {
            Toast.makeText(this, "Не введено наименование", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(txtAddress.getText())) {
            Toast.makeText(this, "Не введен адрес", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btnManufacturerAddUpdate.getText().equals("Добавить")) {
            insertManufacturer();
        } else {
            updateManufacturer(IdManufacturer);
        }
        finish();
    }

    public void cancelClick(View view) {
        finish();
    }

    private void insertManufacturer() {
        DocumentReference manufacturerReference = firebaseFirestore.collection("Manufacturers").document();
        Manufacturer manufacturer = new Manufacturer(txtManufacturerName.getText().toString().trim(), txtAddress.getText().toString().trim());
        manufacturerReference.set(manufacturer);
    }

    private void updateManufacturer(String id) {
        DocumentReference manufacturerReference = firebaseFirestore.collection("Manufacturers").document(id);
        Manufacturer manufacturer = new Manufacturer(txtManufacturerName.getText().toString().trim(), txtAddress.getText().toString().trim());
        manufacturerReference.set(manufacturer);
        updateProducts();
    }

    private void updateProducts() {
        Query query = firebaseFirestore.collection("Products")
                .whereEqualTo("manufacturerName", manufacturerName);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = firebaseFirestore.collection("Products").document(document.getId());
                    productReference.update("manufacturerName", txtManufacturerName.getText().toString().trim());
                }
            }
        });
    }

    private void getIncomingIntent(String id) {
        DocumentReference productReference = firebaseFirestore.collection("Manufacturers").document(id);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Manufacturer manufacturer = documentSnapshot.toObject(Manufacturer.class);
            manufacturerName = manufacturer.getManufacturerName();
            txtManufacturerName.setText(manufacturerName);
            txtAddress.setText(manufacturer.getAddress());
        });
    }
}