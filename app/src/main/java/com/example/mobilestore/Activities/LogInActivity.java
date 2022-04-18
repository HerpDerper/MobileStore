package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobilestore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    CheckBox showPassword;
    EditText txtEmail, txtPassword;
    Button btnEnter, btnRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initialize();
        showPassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });
    }

    private void initialize() {
        showPassword = findViewById(R.id.showPassword);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnEnter = findViewById(R.id.btnEnter);
        btnRegistration = findViewById(R.id.btnRegistration);
    }

    public void enterClick(View view) {
        firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentReference userReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                        userReference.get().addOnSuccessListener(documentSnapshot -> {
                            switch (documentSnapshot.getString("roleName")) {
                                case "Пользователь":
                                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                    finish();
                                    break;
                                case "Администратор":
                                    startActivity(new Intent(LogInActivity.this, MainAdminActivity.class));
                                    finish();
                                    break;
                                case "Продавец":
                                    startActivity(new Intent(LogInActivity.this, MainSellerActivity.class));
                                    finish();
                                    break;
                            }
                        });
                    } else
                        Toast.makeText(getApplicationContext(), "Неправильный логин или пароль", Toast.LENGTH_SHORT).show();
                });
    }

    public void registrationClick(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }
}