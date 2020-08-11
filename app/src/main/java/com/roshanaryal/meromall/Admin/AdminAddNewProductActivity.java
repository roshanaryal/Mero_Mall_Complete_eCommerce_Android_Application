package com.roshanaryal.meromall.Admin;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roshanaryal.meromall.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button addNewProductBtn;
    private ImageView slectImageView;
    private EditText getName, getDescription, getPrice;
    public static final int GALLERY_PICK_REQUEST_CODE = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category");
        Toast.makeText(this, "adding " + intent.getStringExtra("category"), Toast.LENGTH_SHORT).show();

        //
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef=FirebaseDatabase.getInstance().getReference().child("Products");


        //
        loadingbar = new ProgressDialog(this);
        //find
        addNewProductBtn = findViewById(R.id.btn_add_new_product);
        slectImageView = findViewById(R.id.slect_product_image);
        getName = findViewById(R.id.product_name);
        getDescription = findViewById(R.id.product_description);
        getPrice = findViewById(R.id.product_price);

        slectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void validateProductData() {
        Description = getDescription.getText().toString();
        Pname = getName.getText().toString();
        Price = getPrice.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product image is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Product name required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Product description required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Product price required", Toast.LENGTH_SHORT).show();
        } else {
            storeProductInformation();
            loadingbar.setTitle("Adding product...");
            loadingbar.setMessage(getString(R.string.please_wait_text));
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
        }
    }

    private void storeProductInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = saveCurrentDate+saveCurrentTime;
        productRandomKey=productRandomKey.replace("."," ").replace("$"," ").replace("#"," ").replace("@"," ");


        //
        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful())
                                {
                                    downloadImageUrl=task.getResult().toString();
                                    Toast.makeText(AdminAddNewProductActivity.this, "got product image", Toast.LENGTH_SHORT).show();
                                    saveProductInfoTodatabase();
                                }
                            }
                        });
                    }
                });


    }

    private void saveProductInfoTodatabase() {
        HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("pid",productRandomKey);
        productHashMap.put("date",saveCurrentDate);
        productHashMap.put("time",saveCurrentTime);
        productHashMap.put("description",Description);
        productHashMap.put("image",downloadImageUrl);
        productHashMap.put("category",categoryName);
        productHashMap.put("price",Price);
        productHashMap.put("pname",Pname);

        productRef.child(productRandomKey).updateChildren(productHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product added succesfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));
                    finish();

                }
                else {
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product cannot  added error:"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                slectImageView.setImageURI(imageUri);

            }
        }
    }
}
