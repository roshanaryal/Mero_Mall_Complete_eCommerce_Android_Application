package com.roshanaryal.meromall.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.roshanaryal.meromall.HomeActivity;
import com.roshanaryal.meromall.MainActivity;
import com.roshanaryal.meromall.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirts, sportsTShirsts, femaleDresses, sweaters, glasses, walletBagsPurses, hatsCap, shoes,
            headPhoneHandFree, Laptops, watches, mobilePhones;

    private List<String> mStringList = new ArrayList<String>(Arrays.asList("tShirts", "Sports tShirsts", "Female Dresses", "Sweaters", "Glasses", "Wallet Bags Purses", "Hats Caps", "shoes", "Headphones Handfree"
            , "Laptops", "Watches", "Mobile Phones"));

    public static   List<ImageView> mImageViewArrayList;

    private Button logoutBtn,checkNewProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts=findViewById(R.id.t_shirts);
        sportsTShirsts=findViewById(R.id.sports_t_shirts);
        femaleDresses=findViewById(R.id.female_t_shirts);
        sweaters=findViewById(R.id.sweater);
        glasses=findViewById(R.id.glasses);
        walletBagsPurses=findViewById(R.id.pursess_bag);
        hatsCap=findViewById(R.id.hat);
        shoes=findViewById(R.id.shoes);
        headPhoneHandFree=findViewById(R.id.headphones);
        Laptops=findViewById(R.id.laptops);
        watches=findViewById(R.id.watches);
        mobilePhones=findViewById(R.id.mobiles);

      mImageViewArrayList = new ArrayList<>(Arrays.asList(tShirts, sportsTShirsts, femaleDresses, sweaters, glasses, walletBagsPurses, hatsCap, shoes, headPhoneHandFree
                , Laptops, watches, mobilePhones));


        for (int i = 0; i<mImageViewArrayList.size(); i++){
            final int currentItem=i;
            mImageViewArrayList.get(i).setOnClickListener(new View.OnClickListener() {
                int c=currentItem;
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category",mStringList.get(currentItem));
                    startActivity(intent);
                }
            });
        }

        logoutBtn=findViewById(R.id.admin_logout_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this, MainActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkNewProductBtn=findViewById(R.id.check_new_orders_button);
        checkNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class));
            }
        });

        Button maintain_products_btn=findViewById(R.id.maintain_products_admin);
        maintain_products_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);

            }
        });

    }
}
