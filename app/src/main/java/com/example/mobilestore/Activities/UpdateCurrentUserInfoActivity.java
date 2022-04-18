package com.example.mobilestore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.mobilestore.Models.User;
import com.example.mobilestore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class UpdateCurrentUserInfoActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    Calendar date;
    CheckBox showPassword;
    EditText txtLogin, txtPassword, txtAddress, txtEmail, txtUserName, txtUserSurname, txtDateOfBirth;
    Button btnChangeInformation;
    DatePickerDialog.OnDateSetListener picker;
    Bundle bundle;
    private String IdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_current_user_info);
        initialize();
        setData();
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
                new DatePickerDialog(UpdateCurrentUserInfoActivity.this, picker,
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
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserSurname = findViewById(R.id.txtUserSurname);
        txtDateOfBirth = findViewById(R.id.txtDateOfBirth);
        btnChangeInformation = findViewById(R.id.btnChangeInformation);
        bundle = getIntent().getExtras();
        if(bundle.getString("IdUser")!=null){
            IdUser = bundle.getString("IdUser");
        }
    }

    private void setData() {
        DocumentReference userReference;
        if(IdUser!=null){
            userReference = firebaseFirestore.collection("Users").document(IdUser);
        }
        else{
            userReference = firebaseFirestore.collection("Users").document(currentUser.getUid());
        }
        userReference.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            txtLogin.setText(user.getLogin());
            txtAddress.setText(user.getAddress());
            txtEmail.setText(user.getEmail());
            txtUserName.setText(user.getUserName());
            txtDateOfBirth.setText(user.getDateOfBirth());
            txtUserSurname.setText(user.getUserSurname());
        });
    }

    public void changeInformationClick(View view) {
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
        updateUser();
    }

    private void updateUser() {
        Toast.makeText(this, "Подождите, идет обновление данных", Toast.LENGTH_SHORT).show();
        currentUser.updateEmail(txtEmail.getText().toString().trim()).addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentReference userReference = firebaseFirestore.collection("Users").document(currentUser.getUid());
                userReference.update("address", txtAddress.getText().toString().trim());
                userReference.update("dateOfBirth", txtDateOfBirth.getText().toString().trim());
                userReference.update("email", txtEmail.getText().toString().trim());
                userReference.update("login", txtLogin.getText().toString().trim());
                userReference.update("userName", txtUserName.getText().toString().trim());
                userReference.update("userSurname", txtUserSurname.getText().toString().trim());
                currentUser.updatePassword(txtPassword.getText().toString().trim());
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else
                Toast.makeText(this, "Ошибка, пользователь с введенным Email адресом уже существует", Toast.LENGTH_SHORT).show();
        });
    }
}