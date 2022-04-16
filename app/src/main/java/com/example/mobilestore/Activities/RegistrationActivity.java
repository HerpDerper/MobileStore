package com.example.mobilestore.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobilestore.Models.User;
import com.example.mobilestore.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Calendar date;
    DatePickerDialog.OnDateSetListener picker;
    CheckBox showPassword;
    EditText txtLogin, txtPassword, txtAddress, txtEmail, txtUserName, txtUserSurname, txtDateOfBirth;
    Button btnChosePicture, btnRegister;
    ImageView imgAvatar;
    final int CAMERA_REQUEST = 1;
    final int PIC_CROP = 2;
    Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initialize();
        showPassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });
        picker = (datePicker, year, monthOfYear, dauOfMonth) -> {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dauOfMonth);
            txtDateOfBirth.setText(DateUtils.formatDateTime(getApplicationContext(),
                    date.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        };
        txtDateOfBirth.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                new DatePickerDialog(RegistrationActivity.this, picker,
                        date.get(Calendar.YEAR),
                        date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH)).show();
            }
            return true;
        });
        txtDateOfBirth.setOnFocusChangeListener((view, b) -> {
        });
    }

    private void initialize() {
        date = Calendar.getInstance();
        showPassword = findViewById(R.id.showPassword);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserSurname = findViewById(R.id.txtUserSurname);
        txtDateOfBirth = findViewById(R.id.txtDateOfBirth);
        btnChosePicture = findViewById(R.id.btnChosePicture);
        btnRegister = findViewById(R.id.btnRegister);
    }

    public void imageChoseClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, CAMERA_REQUEST);
    }

    public void registerClick(View view) {
        if (TextUtils.isEmpty(txtUserSurname.getText()) || TextUtils.isEmpty(txtUserName.getText()) || TextUtils.isEmpty(txtEmail.getText())
                || TextUtils.isEmpty(txtAddress.getText()) || TextUtils.isEmpty(txtDateOfBirth.getText())
                || TextUtils.isEmpty(txtLogin.getText()) || TextUtils.isEmpty(txtPassword.getText())) {
            Toast.makeText(this, "Вы заполнили не все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtUserName.getText().toString().trim().matches("[a-zA-Zа-яА-Я]{1,30}")) {
            Toast.makeText(this, "Некорректный ввод имени", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtUserSurname.getText().toString().trim().matches("[a-zA-Zа-яА-Я]{1,30}")) {
            Toast.makeText(this, "Некорректный ввод фамилии", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtEmail.getText().toString().trim().matches("[a-zA-Z0-9]{3,20}@[a-zA-Z0-9]{3,15}[.][a-zA-Z]{2,5}")) {
            Toast.makeText(this, "Некорректный ввод Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (date.getTimeInMillis() >= System.currentTimeMillis()) {
            Toast.makeText(this, "Некорректный ввод даты", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!txtPassword.getText().toString().trim().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()â€\"[{}]:;'.,?/*~$^+=<>]).{8,16}$")) {
            Toast.makeText(this, "Пароль должен быть от 8 до 16 символов, содержать спицсимволы, цифры строчные и прописные латинские буквы", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                imageUri = data.getData();
                performCrop();
            } else if (requestCode == PIC_CROP) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                ImageView picView = (ImageView) findViewById(R.id.imgAvatar);
                picView.setImageBitmap(thePic);
            }
        }
    }

    private void uploadImage() {
        Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        final StorageReference reference = storageReference.child(((BitmapDrawable) imgAvatar.getDrawable()).getBitmap().toString().split("@")[1] + "_image");
        UploadTask uploadTask = reference.putBytes(data);
        Task<Uri> task = uploadTask.continueWithTask(task12 -> reference.getDownloadUrl()).addOnCompleteListener(task1 -> {
            imageUri = task1.getResult();
            createNewUser();
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

    private void createNewUser() {
        Toast.makeText(this, "Подождите, идет регистрация", Toast.LENGTH_SHORT).show();
        firebaseAuth.createUserWithEmailAndPassword(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim()).
                addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentReference userReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
                        User user = new User(
                                txtUserSurname.getText().toString().trim(), txtUserName.getText().toString().trim(),
                                txtEmail.getText().toString().trim().toLowerCase(Locale.ROOT), txtLogin.getText().toString(), "User",
                                txtAddress.getText().toString(), txtDateOfBirth.getText().toString(), imageUri.toString());
                        userReference.set(user);
                        Toast.makeText(this, "Вы успешно зарегистрированны", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LogInActivity.class));
                        finish();
                    } else
                        Toast.makeText(this, "Ошибка, пользователь с введенным Email адресом уже существует", Toast.LENGTH_SHORT).show();
                });
    }
}