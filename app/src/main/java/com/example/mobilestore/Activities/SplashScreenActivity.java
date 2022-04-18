package com.example.mobilestore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilestore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        if (currentUser != null) {
            DocumentReference userReference = firebaseFirestore.collection("Users").document(currentUser.getUid());
            userReference.get().addOnSuccessListener(documentSnapshot -> {
                switch (documentSnapshot.getString("roleName")) {
                    case "Пользователь":
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                        break;
                    case "Администратор":
                        startActivity(new Intent(SplashScreenActivity.this, MainAdminActivity.class));
                        finish();
                        break;
                    case "Продавец":
                        Toast.makeText(SplashScreenActivity.this, "Кря", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            });
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreenActivity.this, LogInActivity.class));
                finish();
            }, 500);
        }
    }
}