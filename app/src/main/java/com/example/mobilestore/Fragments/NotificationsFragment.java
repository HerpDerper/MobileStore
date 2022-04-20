package com.example.mobilestore.Fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobilestore.Activities.UpdateCurrentUserInfoActivity;
import com.example.mobilestore.Activities.LogInActivity;
import com.example.mobilestore.Models.User;
import com.example.mobilestore.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    CircleImageView imgAvatar;
    TextView txtLogin;
    Button btnShowInformation, btnChangeInformation, btnLogOut, btnDeleteAccount;
    private User user;
    private final int CAMERA_REQUEST = 1;
    private final int PIC_CROP = 2;
    Uri imageUri;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initialize();
        setData();
        setOnClick();
        return root;
    }

    private void initialize() {
        imgAvatar = binding.imgAvatar;
        txtLogin = binding.txtLogin;
        btnShowInformation = binding.btnShowInformation;
        btnChangeInformation = binding.btnChangeInformation;
        btnLogOut = binding.btnLogOut;
        btnDeleteAccount = binding.btnDeleteAccount;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setData() {
        DocumentReference userReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            Picasso.get()
                    .load(user.getAvatar())
                    .into(imgAvatar);
            txtLogin.setText(user.getLogin());
        });
    }

    private void setOnClick() {
        imgAvatar.setOnClickListener(view -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, CAMERA_REQUEST);
        });
        btnChangeInformation.setOnClickListener(view -> startActivity(new Intent(getActivity(), UpdateCurrentUserInfoActivity.class).putExtra("IdUser", firebaseAuth.getCurrentUser().getUid())));

        btnDeleteAccount.setOnClickListener(view -> {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            dialog.setTitle("Удаление данных")
                    .setMessage("Вы действительно хотите удалить ваш аккаунт?")
                    .setPositiveButton("Да", (dialogInterface, i) -> {
                        deleteCommentLikes();
                        deleteComments();
                        deleteCarts();
                        DocumentReference userReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                        userReference.delete();
                        firebaseAuth.getCurrentUser().delete();
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), LogInActivity.class));
                    })
                    .setNegativeButton("Нет", (dialogInterface, i) -> dialogInterface.cancel());
            dialog.create().show();
        });

        btnLogOut.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LogInActivity.class));
        });

        btnShowInformation.setOnClickListener(view -> {
            StringBuilder builder = new StringBuilder();
            builder.append("Имя: ").append(user.getUserName()).append("\n");
            builder.append("Фамилия: ").append(user.getUserSurname()).append("\n");
            builder.append("Email: ").append(user.getEmail()).append("\n");
            builder.append("Логин: ").append(user.getLogin()).append("\n");
            builder.append("Адрес: ").append(user.getAddress()).append("\n");
            builder.append("Дата рождения: ").append(user.getDateOfBirth()).append("\n");
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(true);
            dialog.setTitle("Информация");
            dialog.setMessage(builder.toString());
            dialog.show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST) {
                imageUri = data.getData();
                performCrop();
            } else if (requestCode == PIC_CROP) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                CircleImageView picView = (CircleImageView) binding.imgAvatar;
                picView.setImageBitmap(thePic);
                uploadImage();
            }
        } catch (Exception exception) {
        }
    }

    private void uploadImage() {
        Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference reference = storageRef.child(((BitmapDrawable) imgAvatar.getDrawable()).getBitmap().toString().split("@")[1] + "_image");
        UploadTask uploadTask = reference.putBytes(data);
        Task<Uri> task = uploadTask.continueWithTask(task1 -> reference.getDownloadUrl()).addOnCompleteListener(task12 -> {
            imageUri = task12.getResult();
            DocumentReference userReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
            userReference.update("avatar", imageUri.toString());
        });
    }

    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException exception) {
        }
    }

    private void deleteCommentLikes() {
        Query query = firebaseFirestore.collection("CommentLikes")
                .whereEqualTo("userName", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentLikeReference = firebaseFirestore.collection("CommentLikes").document(document.getId());
                    commentLikeReference.delete();
                }
            }
        });
    }

    private void deleteComments() {
        Query query = firebaseFirestore.collection("Comments")
                .whereEqualTo("userName", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentReference = firebaseFirestore.collection("Comments").document(document.getId());
                    commentReference.delete();
                }
            }
        });
    }

    private void deleteCarts() {
        Query query = firebaseFirestore.collection("Carts")
                .whereEqualTo("userName", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference commentReference = firebaseFirestore.collection("Carts").document(document.getId());
                    commentReference.delete();
                }
            }
        });
    }
}