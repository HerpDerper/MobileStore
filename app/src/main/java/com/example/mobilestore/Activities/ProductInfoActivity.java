package com.example.mobilestore.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Adapters.CommentUserAdapter;
import com.example.mobilestore.Models.Cart;
import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.Models.Product;
import com.example.mobilestore.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ProductInfoActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imgProductImage;
    TextView txtPrice, txtExtraInfo, txtDescription, txtRating;
    RatingBar rtnRating;
    Button btnDeleteComment, btnAddComment, btnBuy, btnAddToCart;
    RecyclerView recyclerView;
    private CommentUserAdapter adapter;
    Bundle bundle;
    private String IdProduct;
    private int documentCount;
    private String IdComment;
    private int productCount;
    private float price;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        initialize();
        setRecyclerView();
        adapter.startListening();
        Query query = firebaseFirestore.collection("Comments")
                .whereEqualTo("productName", IdProduct)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    IdComment = document.getId();
                }
                documentCount = task.getResult().size();
                if (documentCount == 0) {
                    btnDeleteComment.setVisibility(View.INVISIBLE);
                } else {
                    btnDeleteComment.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIncomingIntent();
    }

    private void initialize() {
        imgProductImage = findViewById(R.id.imgProductImage);
        txtPrice = findViewById(R.id.txtPrice);
        txtExtraInfo = findViewById(R.id.txtExtraInfo);
        txtDescription = findViewById(R.id.txtDescription);
        txtRating = findViewById(R.id.txtRating);
        rtnRating = findViewById(R.id.rtnRating);
        btnAddComment = findViewById(R.id.btnAddComment);
        btnDeleteComment = findViewById(R.id.btnDeleteComment);
        btnBuy = findViewById(R.id.btnBuy);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        recyclerView = findViewById(R.id.recyclerComments);
        bundle = getIntent().getExtras();
        IdProduct = bundle.getString("IdProduct");
        if (bundle.getString("Role") != null) {
            btnBuy.setVisibility(View.GONE);
            btnAddToCart.setVisibility(View.GONE);
            role = bundle.getString("Role");
        }
    }

    public void buyClick(View view) {
        startActivity(new Intent(getApplicationContext(), BuyingActivity.class)
                .putExtra("IdProduct", IdProduct)
                .putExtra("productCount", productCount)
                .putExtra("price", price)
                .putExtra("productName", getTitle().toString()));
    }

    public void addCommentClick(View view) {
        if (role != null)
            startActivity(new Intent(this, AddCommentActivity.class).putExtra("IdProduct", IdProduct).putExtra("Role", role));
        else
            startActivity(new Intent(this, AddCommentActivity.class).putExtra("IdProduct", IdProduct));
        finish();
    }

    public void deleteCommentClick(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Удаление комментария")
                .setMessage("Вы действительно хотите удалить ваш комментарий с оценкой?")
                .setPositiveButton("Да", (dialogInterface, i) -> {
                    deleteCommentLikes();
                    deleteComment();
                    btnDeleteComment.setVisibility(View.INVISIBLE);
                })
                .setNegativeButton("Нет", (dialogInterface, i) -> dialogInterface.cancel());
        dialog.create().show();
    }

    public void addToCartClick(View view) {
        Query query = firebaseFirestore.collection("Carts")
                .whereEqualTo("productName", IdProduct)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String IdCart = "";
                for (QueryDocumentSnapshot document : task.getResult()) {
                    IdCart = document.getId();
                }
                documentCount = task.getResult().size();
                if (documentCount == 0) {
                    DocumentReference cartReference = firebaseFirestore.collection("Carts").document();
                    Cart cart = new Cart(currentUser.getUid(), IdProduct, 1);
                    cartReference.set(cart);
                    Toast.makeText(ProductInfoActivity.this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference cartReference = firebaseFirestore.collection("Carts").document(IdCart);
                    cartReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (productCount < Integer.parseInt(documentSnapshot.get("productCount").toString()) + 1) {
                                Toast.makeText(ProductInfoActivity.this, "Товар не добавлен в корзину", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(ProductInfoActivity.this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();
                            cartReference.update("productCount", Integer.parseInt(documentSnapshot.get("productCount").toString()) + 1);
                        }
                    });
                }
            }
        });
    }

    private void getIncomingIntent() {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(IdProduct);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            txtExtraInfo.setText("Категория: " + product.getCategoryName() + "\nПроизводитель:" + product.getManufacturerName() + "\nГарантия: " + product.getGuarantee());
            Picasso.get()
                    .load(product.getProductImage())
                    .into(imgProductImage);
            price = product.getPrice();
            txtPrice.setText(price + "₽");
            txtRating.setText(String.format("%.1f", product.getRating()) + " (" + product.getRatingCount() + " оценок)");
            txtDescription.setText(product.getDescription());
            rtnRating.setRating(product.getRating());
            productCount = product.getProductCount();
            setTitle(product.getProductName());
            if (productCount == 0) {
                btnBuy.setVisibility(View.GONE);
            }
        });
    }

    private void setRecyclerView() {
        Query query = firebaseFirestore.collection("Comments").whereEqualTo("productName", IdProduct);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        adapter = new CommentUserAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void updateProduct(float rating) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(IdProduct);
        productReference.get().addOnSuccessListener(documentSnapshot -> {
            Product product = documentSnapshot.toObject(Product.class);
            float oldRating = product.getRating();
            int ratingCount = product.getRatingCount();
            int ratingCountNew = ratingCount - 1;
            float ratingAll = oldRating * ratingCount;
            ratingAll -= rating;
            ratingAll = ratingAll / (ratingCountNew);
            productReference.update("ratingCount", ratingCountNew);
            if (ratingCountNew == 0) {
                ratingAll = 0;
            }
            productReference.update("rating", ratingAll);
            txtRating.setText(String.format("%.1f", product.getRating()) + " (" + product.getRatingCount() + " оценок)");
            rtnRating.setRating(ratingAll);
        });
    }

    private void deleteComment() {
        DocumentReference commentReference = firebaseFirestore.collection("Comments").document(IdComment);
        commentReference.get().addOnSuccessListener(documentSnapshot -> {
            Comment comment = documentSnapshot.toObject(Comment.class);
            float rating = comment.getRating();
            updateProduct(rating);
            commentReference.delete();
        });
    }

    private void deleteCommentLikes() {
        Query query = firebaseFirestore.collection("CommentLikes")
                .whereEqualTo("commentName", IdComment)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document(document.getId());
                    commentLikeReference.delete();
                }
            }
        });
    }
}