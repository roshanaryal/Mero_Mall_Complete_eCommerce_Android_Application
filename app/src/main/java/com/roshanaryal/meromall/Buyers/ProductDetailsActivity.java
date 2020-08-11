package com.roshanaryal.meromall.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roshanaryal.meromall.Model.ProductsModal;
import com.roshanaryal.meromall.Model.Users;
import com.roshanaryal.meromall.Prevalent.Prevalent;
import com.roshanaryal.meromall.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ProductDetailsActivity extends AppCompatActivity {

    private String pid;
    DatabaseReference mReference;
    private ImageView productImage;
    private TextView nameTextView,descriptionTextView,priceTextView;
    private ElegantNumberButton mNumberButton;
    private Button addToCartBtn;

    String name,price,description;
    private String state="";
    private Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Intent intent=getIntent();
        pid=intent.getStringExtra("pid");
        Paper.init(this);
       currentUser= Paper.book().read(Prevalent.currentOnlineUser);


        mReference= FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
        productImage=findViewById(R.id.imageView_detail);
        nameTextView=findViewById(R.id.product_name_detail);
        descriptionTextView=findViewById(R.id.product_description_detail);
        priceTextView=findViewById(R.id.product_price_detail);
        mNumberButton=findViewById(R.id.number_btn);
        addToCartBtn=findViewById(R.id.add_to_cart_button);

        updateProduct();

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("Item shipped" )| state.equals("Item is ready to be shipped")) {
                    Toast.makeText(ProductDetailsActivity.this, "You can purchase item once the order is placed succesfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addToCartList();
                }
            }
        });

    }

    private void addToCartList() {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendarForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy" );
        saveCurrentDate=currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a" );
        saveCurrentTime=currentTime.format(calendarForTime.getTime());

        final DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("pid",pid);
        productHashMap.put("pname",name);
        productHashMap.put("price",price);
      //  productHashMap.put("description",description);
        productHashMap.put("date",saveCurrentDate);
        productHashMap.put("time",saveCurrentTime);
        productHashMap.put("quantity",mNumberButton.getNumber());
        productHashMap.put("discount","");
        //productHashMap.put("pid",pid);


        cartRef.child("User View")
                .child(currentUser.getPhone())
                .child("Products")
                .child(pid)
                .updateChildren(productHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        cartRef.child("Admin View")
                                .child(currentUser.getPhone())
                                .child("Products")
                                .child(pid)
                                .updateChildren(productHashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProductDetailsActivity.this, "Added to cart sucesfully", Toast.LENGTH_SHORT).show();

                                        }
                                        finish();

                                    }
                                });

                    }
                    else
                    {
                        Toast.makeText(ProductDetailsActivity.this, "Something went wrong!!! try again", Toast.LENGTH_SHORT).show();
                    }
                    }
                });

    }

    private void updateProduct() {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductsModal modal = snapshot.getValue(ProductsModal.class);
                if (modal!=null)
                {

                    Picasso.get().load(modal.getImage()).into(productImage);
                    name=modal.getPname();
                    price=modal.getPrice();
                    description=modal.getDescription();
                    nameTextView.setText(modal.getPname());
                    descriptionTextView.setText(modal.getDescription());
                    priceTextView.setText("Price:: $"+modal.getPrice());

                }
                else
                {
                    Toast.makeText(ProductDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        checkOrdersState();
    }

    private void checkOrdersState(){
        Paper.init(this);
        Users curentUser=Paper.book().read(Prevalent.currentOnlineUser);
        DatabaseReference ordersRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(curentUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String state=snapshot.child("state").getValue().toString();
                    if (state.equals("shipped"))
                    {
                        state="Item shipped";
                    }
                    else if (state.equals("not shipped"))
                    {
                        state="Item is ready to be shipped";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
