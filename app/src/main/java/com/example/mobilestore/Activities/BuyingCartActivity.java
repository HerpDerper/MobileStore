package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilestore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuyingCartActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    EditText txtCVV, txtCardNumber, txtCardMonth, txtCardYear;
    TextView txtProductName, txtPrice;
    Calendar calendar = Calendar.getInstance();
    Bundle bundle;
    private float price = 0;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buying_cart);
        initialize();
        setTxtPrice();
        txtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int inputLength = txtCardNumber.getText().toString().length();
                if (count <= inputLength && (inputLength == 4 || inputLength == 9 || inputLength == 14)) {
                    txtCardNumber.setText(txtCardNumber.getText().toString() + " ");
                    txtCardNumber.setSelection(txtCardNumber.getText().length());
                } else if (count >= inputLength && (inputLength == 4 || inputLength == 9 || inputLength == 14)) {
                    txtCardNumber.setText(txtCardNumber.getText().toString()
                            .substring(0, txtCardNumber.getText().toString().length() - 1));
                    txtCardNumber.setSelection(txtCardNumber.getText().length());
                }
                count = inputLength;
            }
        });
    }

    private void initialize() {
        txtCVV = findViewById(R.id.txtCVV);
        txtCardNumber = findViewById(R.id.txtCardNumber);
        txtCardMonth = findViewById(R.id.txtCardMonth);
        txtCardYear = findViewById(R.id.txtCardYear);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        bundle = getIntent().getExtras();
    }

    public void buyClick(View view) {
        if (!txtCardNumber.getText().toString().trim().matches("[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}")) {
            Toast.makeText(this, "Некорректный ввод номера карты", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtCVV.getText().toString().trim().matches("[0-9]{3}")) {
            Toast.makeText(this, "Некорректный ввод CVV", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtCardMonth.getText().toString().trim().matches("(0?[1-9]|1[012])")) {
            Toast.makeText(this, "Некорректный ввод месяца", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtCardYear.getText().toString().trim().matches("[0-9]{2}")) {
            Toast.makeText(this, "Некорректный ввод года", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(txtCardYear.getText().toString()) < calendar.get(Calendar.YEAR) - 2000) {
            Toast.makeText(this, "Карта недействительна", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(txtCardYear.getText().toString()) == calendar.get(Calendar.YEAR) - 2000 && Integer.parseInt(txtCardMonth.getText().toString()) < calendar.get(Calendar.MONTH) + 1) {
            Toast.makeText(this, "Карта недействительна", Toast.LENGTH_SHORT).show();
            return;
        }
        updateProducts();
        finish();
        showMessage();
    }

    public void cancelClick(View view) {
        finish();
    }

    private void showMessage() {
        DocumentReference productReference = firebaseFirestore.collection("Users").document(currentUser.getUid());
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Toast.makeText(this,
                    "Поздравляем с успешной покупкой!\nТовары будут доставлены по вашему адресу: " + documentSnapshot.getString("address"),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProducts() {
        Query query = firebaseFirestore.collection("Carts").whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = firebaseFirestore.collection("Products").document(document.getString("productName"));
                    productReference.get().addOnSuccessListener(documentSnapshot -> {
                        productReference.update("productCount", documentSnapshot.getLong("productCount") - document.getLong("productCount"));
                    });
                    DocumentReference cartReference = firebaseFirestore.collection("Carts").document(document.getId());
                    cartReference.delete();
                }
            }
        });
    }

    private void setTxtPrice() {
        Query query = firebaseFirestore.collection("Carts").whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference productReference = firebaseFirestore.collection("Products").document(document.getString("productName"));
                    productReference.get().addOnSuccessListener(documentSnapshot -> {
                        price += documentSnapshot.getLong("price") * document.getLong("productCount");
                        txtPrice.setText(String.valueOf(price));
                        txtPrice.setText("К оплате: " + txtPrice.getText() + "₽");
                    });
                }
            }
        });
    }
}