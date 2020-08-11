package com.roshanaryal.meromall.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.roshanaryal.meromall.HomeActivity;
import com.roshanaryal.meromall.Model.Users;
import com.roshanaryal.meromall.Prevalent.Prevalent;
import com.roshanaryal.meromall.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    private EditText phoneEditText, nameEditText, adressEditText;
    private CircleImageView mImageView;
    private TextView updateTextView, closeTextView, changeImageTextView;
    private Uri imageUri;
    private String myUri="";
    private StorageReference profilePicreference;
    private String checker="";
    private StorageTask uploadTask;
    private Button setSerurityQn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        phoneEditText = findViewById(R.id.setting_edit_phone);
        nameEditText = findViewById(R.id.setting_edit_name);
        adressEditText = findViewById(R.id.setting_edit_adress);
        mImageView = findViewById(R.id.setting_profile_image);
        updateTextView = findViewById(R.id.update_setting);
        closeTextView = findViewById(R.id.close_setting);
        changeImageTextView = findViewById(R.id.setting_change_profile_image_text);

        //
        profilePicreference= FirebaseStorage.getInstance().getReference();

        //paper
        Paper.init(this);

        UserinfoDisplay();

        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    UserInfoSave();

                }
                else {
                    UpdateOnlyUserInfo();
                }
            }
        });

        changeImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });

        //sec qn
        setSerurityQn=findViewById(R.id.set_decurity_question);
        setSerurityQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","setting");
                startActivity(intent);
            }
        });

    }

    private void UpdateOnlyUserInfo()
    {

        if (checker=="")
        {
            if (checkEditText())
            {
                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Updating profile ");
                progressDialog.setMessage("please wait, while updating profile");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Users users=Paper.book().read(Prevalent.currentOnlineUser);


                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(users.getPhone());
                HashMap<String,Object> userHashMap=new HashMap<>();
                userHashMap.put("name",nameEditText.getText().toString());
                userHashMap.put("adress",adressEditText.getText().toString());
                userHashMap.put("phoneOrder",phoneEditText.getText().toString());
                reference.updateChildren(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Profile updated succesfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            finish();

                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Something went wrong:: try again", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                            // finish();

                        }

                    }
                });

            }
        }

    }



    private void UserInfoSave()
    {

        if (checkEditText())
        {
            if (checker.equals("clicked"))
            {
                UploadImage();
            }

        }

    }

    private void UploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Updating profile ");
        progressDialog.setMessage("please wait, while updating profile");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri!=null)
        {
            final Users users1=Paper.book().read(Prevalent.currentOnlineUser);
            final StorageReference fileref=profilePicreference.child(users1.getPhone()+".jpg");
            uploadTask=fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl=task.getResult();
                        myUri=downloadUrl.toString();

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users").child(users1.getPhone());
                        HashMap<String,Object> userHashMap=new HashMap<>();
                        userHashMap.put("name",nameEditText.getText().toString());
                        userHashMap.put("adress",adressEditText.getText().toString());
                        userHashMap.put("phoneOrder",phoneEditText.getText().toString());
                        userHashMap.put("image",myUri);

                        reference.updateChildren(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Profile updated succesfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                                    finish();

                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Something went wrong:: try again", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                                   // finish();

                                }

                            }
                        });

                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Something went wrong:: try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this, "image is not slected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkEditText() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "enter name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "enter phone", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(adressEditText.getText().toString()))
        {
            Toast.makeText(this, "enter adress", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            mImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error: try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void UserinfoDisplay()
    {
        final Users users= Paper.book().read(Prevalent.currentOnlineUser);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(users.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String password=snapshot.child("password").getValue().toString();
                        String adress=snapshot.child("adress").getValue().toString();

                        //original Phne
                        String oPhone=users.getPhone();

                        Picasso.get().load(image).into(mImageView);
                        nameEditText.setText(name);
                        phoneEditText.setText(phone);
                        adressEditText.setText(adress);
                        Paper.book().write(Prevalent.hasImageKey,true);
                        Paper.book().delete(Prevalent.currentOnlineUser);
                        Users users1=new Users(name,oPhone,password,image,adress);
                        Paper.book().write(Prevalent.currentOnlineUser,users1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
