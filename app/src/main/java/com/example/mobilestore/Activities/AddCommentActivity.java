package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddCommentActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    EditText txtText;
    RatingBar rtnRating;
    Calendar date;
    Bundle bundle;
    private String IdProduct;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        initialize();
    }

    private void initialize() {
        date = Calendar.getInstance();
        txtText = findViewById(R.id.txtText);
        rtnRating = findViewById(R.id.rtnRating);
        bundle = getIntent().getExtras();
        IdProduct = bundle.getString("IdProduct");
        if (bundle.getString("Role") != null) role = bundle.getString("Role");
    }

    public void addCommentClick(View view) {
        if (rtnRating.getRating() == 0) {
            Toast.makeText(this, "Поставьте оценку", Toast.LENGTH_SHORT).show();
            return;
        }
        if (txtText.getText().length() == 0) {
            Toast.makeText(this, "Введите текст комментария", Toast.LENGTH_SHORT).show();
            return;
        }
        float rating = rtnRating.getRating();
        updateProduct(rating);
        addComment(rating);
        if (role != null)
            startActivity(new Intent(AddCommentActivity.this, ProductInfoActivity.class).putExtra("IdProduct", IdProduct).putExtra("Role", role));
        else
            startActivity(new Intent(AddCommentActivity.this, ProductInfoActivity.class).putExtra("IdProduct", IdProduct));
        finish();
    }

    public void cancelCommentClick(View view) {
        if (role != null)
            startActivity(new Intent(AddCommentActivity.this, ProductInfoActivity.class).putExtra("IdProduct", IdProduct).putExtra("Role", role));
        else
            startActivity(new Intent(AddCommentActivity.this, ProductInfoActivity.class).putExtra("IdProduct", IdProduct));
        finish();
    }

    private void updateProduct(float rating) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(IdProduct);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            float oldRating = product.getRating();
            int ratingCount = product.getRatingCount();
            float ratingAll = oldRating * ratingCount;
            ratingAll += rating;
            ratingAll = ratingAll / (ratingCount + 1);
            productReference.update("ratingCount", ratingCount + 1);
            productReference.update("rating", ratingAll);
        });
    }

    private void addComment(float rating) {
        DocumentReference commentReference = firebaseFirestore.collection("Comments").document();
        Comment comment = new Comment(txtText.getText().toString(),
                IdProduct,
                currentUser.getUid(),
                DateUtils.formatDateTime(getApplicationContext(), date.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR),
                0,
                rating);
        commentReference.set(comment);
    }
}