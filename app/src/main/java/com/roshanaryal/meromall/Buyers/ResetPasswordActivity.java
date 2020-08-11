package com.roshanaryal.meromall.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshanaryal.meromall.LoginActivity;
import com.roshanaryal.meromall.Model.Users;
import com.roshanaryal.meromall.Prevalent.Prevalent;
import com.roshanaryal.meromall.R;

import java.util.HashMap;

import io.paperdb.Paper;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check;
    private TextView title;
    private EditText phoneEditText, qn1EditText, qn2EditText;
    private Button verifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        title = findViewById(R.id.header_text_reset_password);
        phoneEditText = findViewById(R.id.phone_edit_reset);
        qn1EditText = findViewById(R.id.question_1);
        qn2EditText = findViewById(R.id.question_2);
        verifyBtn = findViewById(R.id.verifyBtn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (check.equals("setting")) {
            Users mUsers = Paper.book().read(Prevalent.currentOnlineUser);
            final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mUsers.getPhone());
            Toast.makeText(this, "From setting", Toast.LENGTH_SHORT).show();
            title.setText("Set Question");
            phoneEditText.setVisibility(View.GONE);
            verifyBtn.setText("Set");

            ref1.child("Security Questions").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String qn1 = snapshot.child("answer1").getValue().toString();
                        String qn2 = snapshot.child("answer2").getValue().toString();
                        qn1EditText.setText(qn1);
                        qn2EditText.setText(qn2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qn1 = qn1EditText.getText().toString();
                    String qn2 = qn2EditText.getText().toString();

                    if (TextUtils.isEmpty(qn1EditText.getText().toString()) && TextUtils.isEmpty(qn2EditText.getText().toString())) {
                        Toast.makeText(ResetPasswordActivity.this, "please answer two question", Toast.LENGTH_SHORT).show();
                    } else {
                        Paper.init(ResetPasswordActivity.this);

                        HashMap<String, Object> userDataMap = new HashMap<>();
                        userDataMap.put("answer1", qn1.toLowerCase());
                        userDataMap.put("answer2", qn2.toLowerCase());

                        ref1.child("Security Questions").
                                updateChildren(userDataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ResetPasswordActivity.this, "Security question updated succesfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(ResetPasswordActivity.this, "Somehing went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            });
        } else if (check.equals("login")) {
            title.setText("Reset Password");
            phoneEditText.setVisibility(View.VISIBLE);
            verifyBtn.setText("Verify");
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ResetPassword();
                }
            });
            // ResetPassword();
        }
    }

    private void ResetPassword() {
        final String phone = phoneEditText.getText().toString();
        final String qn1 = qn1EditText.getText().toString();
        final String qn2 = qn2EditText.getText().toString();
        if (TextUtils.isEmpty(qn1EditText.getText().toString()) && TextUtils.isEmpty(qn2EditText.getText().toString()) && TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(ResetPasswordActivity.this, "enter all required field", Toast.LENGTH_SHORT).show();
        } else {

            final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("Security Questions")) {
                            String answer1 = snapshot.child("Security Questions").child("answer1").getValue().toString();
                            String answer2 = snapshot.child("Security Questions").child("answer2").getValue().toString();

                            if (!qn1.equals(answer1)) {
                                Toast.makeText(ResetPasswordActivity.this, "first answer incorrect", Toast.LENGTH_SHORT).show();
                            } else if (!qn2.equals(answer2)) {
                                Toast.makeText(ResetPasswordActivity.this, "second answer incorrect", Toast.LENGTH_SHORT).show();
                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("Enter new password");
                                final EditText passwordEdittext = new EditText(ResetPasswordActivity.this);
                                passwordEdittext.setHint("Enter new password");
                                passwordEdittext.setPadding(20,20,20,20);
                                builder.setView(passwordEdittext);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        if (TextUtils.isEmpty(passwordEdittext.getText().toString())) {
                                            Toast.makeText(ResetPasswordActivity.this, "password field cannot be empty", Toast.LENGTH_SHORT).show();
                                        } else {

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("password", passwordEdittext.getText().toString());
                                            ref1.child("password").setValue(passwordEdittext.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(ResetPasswordActivity.this, "Password changed succesfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ResetPasswordActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();


                            }

                        }

                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "No user found with this phone :" + phone, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
