package com.example.mobilestore;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.Adapters.CommentAdapter;
import com.example.mobilestore.Models.Cart;
import com.example.mobilestore.Models.Comment;
import com.example.mobilestore.Models.Product;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ProductInfoActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Comments");
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView imgProductImage;
    TextView txtPrice, txtExtraInfo, txtDescription, txtRating;
    RatingBar rtnRating;
    Button btnDeleteComment, btnAddComment;
    private CommentAdapter adapter;
    private RecyclerView recyclerView;
    Bundle bundle;
    private String IdProduct;
    private int documentCount;
    private String IdComment;
    private int productCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        initialize();
        getIncomingIntent();
        setRecyclerView();
        adapter.startListening();
        Query query = collectionReference
                .whereEqualTo("productName", IdProduct)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
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
        recyclerView = findViewById(R.id.recyclerComments);
        bundle = getIntent().getExtras();
        IdProduct = bundle.getString("IdProduct");
    }

    public void buyClick(View view) {

    }

    public void addCommentClick(View view) {
        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra("IdProduct", IdProduct);
        startActivity(intent);
        finish();
    }

    public void deleteCommentClick(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Удаление комментария")
                .setMessage("Вы действительно хотите удалить ваш комментарий с оценкой?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCommentLikes();
                        deleteComment();
                        btnDeleteComment.setVisibility(View.INVISIBLE);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialog.create().show();
    }

    public void addToCartClick(View view) {
        Query query = firebaseFirestore.collection("Carts")
                .whereEqualTo("productName", IdProduct)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
    }

    private void getIncomingIntent() {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(IdProduct);
        productReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                txtExtraInfo.setText(product.getCategoryName() + "\n" + product.getManufacturerName());
                Picasso.get()
                        .load(product.getProductImage())
                        .into(imgProductImage);
                txtPrice.setText(product.getPrice() + "₽");
                txtRating.setText(String.format("%.1f", product.getRating()) + " (" + product.getRatingCount() + " оценок)");
                txtDescription.setText(product.getDescription());
                rtnRating.setRating(product.getRating());
                productCount = product.getProductCount();
                setTitle(product.getProductName());
            }
        });
    }

    private void setRecyclerView() {
        Query query = collectionReference.whereEqualTo("productName", IdProduct);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        adapter = new CommentAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void updateProduct(float rating) {
        DocumentReference productReference = firebaseFirestore.collection("Products").document(IdProduct);
        productReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
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
                txtRating.setText(ratingAll + " (" + String.valueOf(ratingCountNew) + " оценок)");
                rtnRating.setRating(ratingAll);
            }
        });
    }

    private void deleteComment() {
        DocumentReference commentReference = collectionReference.document(IdComment);
        commentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Comment comment = documentSnapshot.toObject(Comment.class);
                float rating = comment.getRating();
                updateProduct(rating);
                commentReference.delete();
            }
        });
    }

    private void deleteCommentLikes() {
        Query query = firebaseFirestore.collection("CommentLikes")
                .whereEqualTo("commentName", IdComment)
                .whereEqualTo("userName", currentUser.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document(document.getId());
                        commentLikeReference.delete();
                    }
                }
            }
        });
    }
}