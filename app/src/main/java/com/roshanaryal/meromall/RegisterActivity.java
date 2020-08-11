package com.roshanaryal.meromall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText phoneEditText, userNameEditText, passwordEditText;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signUpButton = findViewById(R.id.sign_up);
        phoneEditText = findViewById(R.id.register_phone_number_input);
        passwordEditText = findViewById(R.id.register_password_input);
        userNameEditText = findViewById(R.id.register_user_name_input);
        loadingbar = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        String phone = phoneEditText.getText().toString();
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "please enter name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please enter phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show();

        } else {
            loadingbar.setTitle("Creating account");
            loadingbar.setMessage("please wait..processing");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            ValidatPhoneNumber(userName, phone, password);
        }

    }

    private void ValidatPhoneNumber(final String userName, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("Users").child(phone).exists())) {
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Phone number is already in use\nplease login or create account eith new phonew number", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();

                } else {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", userName);
                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "User Create", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                        finish();
                                    } else {
                                        loadingbar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "something went wrong\n please try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
