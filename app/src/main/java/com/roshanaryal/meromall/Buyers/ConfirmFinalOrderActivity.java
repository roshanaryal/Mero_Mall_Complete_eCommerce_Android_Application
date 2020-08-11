package com.roshanaryal.meromall.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roshanaryal.meromall.HomeActivity;
import com.roshanaryal.meromall.Model.Users;
import com.roshanaryal.meromall.Prevalent.Prevalent;
import com.roshanaryal.meromall.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText phoneEditText,nameEditText,adressEditText,cityEditText;
    private Button confirmButton;
    int totalprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        phoneEditText=findViewById(R.id.phone_edit_confirm);
        nameEditText=findViewById(R.id.name_edit_confirm);
        adressEditText=findViewById(R.id.adress_edit_confirm);
        cityEditText=findViewById(R.id.city_edit_confirm);
        confirmButton=findViewById(R.id.confirm_button_confirm);
        Paper.init(this);

        totalprice=getIntent().getIntExtra("total",0);
      //  Toast.makeText(this, "total: $" +String.valueOf(totalprice), Toast.LENGTH_SHORT).show();

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    if (checkEditText())
                    {
                        confirmOrder();
                    }
            }
        });

    }

    private void confirmOrder()
    {
        String saveCurrentTime,saveCurrentDate;
        Calendar calendarForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy" );
        saveCurrentDate=currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a" );
        saveCurrentTime=currentTime.format(calendarForTime.getTime());
        final Users users=Paper.book().read(Prevalent.currentOnlineUser);

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(users.getPhone());

        final HashMap<String,Object> orderFinalHashMap=new HashMap<>();
        orderFinalHashMap.put("totalAmount",String.valueOf(totalprice));
        orderFinalHashMap.put("name",nameEditText.getText().toString());
        orderFinalHashMap.put("phone",phoneEditText.getText().toString());
        //  productHashMap.put("description",description);
        orderFinalHashMap.put("adress",adressEditText.getText().toString());
        orderFinalHashMap.put("city",cityEditText.getText().toString());
        orderFinalHashMap.put("date",saveCurrentDate);
        orderFinalHashMap.put("time",saveCurrentTime);
        orderFinalHashMap.put("state","not shipped");
        reference.updateChildren(orderFinalHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    DatabaseReference removeRef=FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                            .child(users.getPhone())
                            .child("Products");
                           removeRef .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been confiremed", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        else   if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "enter city", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}
