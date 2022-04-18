package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mobilestore.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Spinner spnRoleName;
    List<String> listRoles;
    ArrayAdapter<String> adapterRoles;
    private String IdUser;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        initialize();
        setRoleData();
    }

    private void initialize() {
        spnRoleName = findViewById(R.id.spnRoleName);
        bundle = getIntent().getExtras();
        IdUser = bundle.getString("IdUser");
    }

    public void updateUserClick(View view) {
        updateUserRole(IdUser);
        finish();
    }

    public void cancelClick(View view) {
        finish();
    }

    private void updateUserRole(String id) {
        DocumentReference userReference = firebaseFirestore.collection("Users").document(id);
        userReference.update("roleName", spnRoleName.getSelectedItem().toString());
    }

    private void setRoleData() {
        listRoles = new ArrayList<>();
        adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listRoles);
        adapterRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRoleName.setAdapter(adapterRoles);
        firebaseFirestore.collection("Roles").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                String manufacturerName = document.getString("roleName");
                listRoles.add(manufacturerName);
            }
            adapterRoles.notifyDataSetChanged();
        });
    }
}