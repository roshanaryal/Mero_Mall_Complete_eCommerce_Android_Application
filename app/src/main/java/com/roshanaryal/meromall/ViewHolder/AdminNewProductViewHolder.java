package com.roshanaryal.meromall.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roshanaryal.meromall.R;

public class AdminNewProductViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
    public TextView nameTextView,phoneTextView,priceTextView,adressTextView,dateTimeTextView;
   public Button viewDetail;
    public AdminNewProductViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView=itemView.findViewById(R.id.user_name_admin);
        phoneTextView=itemView.findViewById(R.id.phone_number_admin);
        priceTextView=itemView.findViewById(R.id.total_price_admin);
        adressTextView=itemView.findViewById(R.id.adress);
        dateTimeTextView=itemView.findViewById(R.id.order_date_time_admin_new);
        viewDetail=itemView.findViewById(R.id.show_order_products_btn);

    }

    @Override
    public void onClick(View v) {


    }
}
