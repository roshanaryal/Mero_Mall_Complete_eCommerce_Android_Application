package com.roshanaryal.meromall.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roshanaryal.meromall.Interface.onItemClickListner;
import com.roshanaryal.meromall.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView titleTextView, quantityTextView, priceTextView;
    public onItemClickListner mOnItemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.cart_list_title_product);
        quantityTextView = itemView.findViewById(R.id.cart_list_quantity);
        priceTextView = itemView.findViewById(R.id.cart_list_price);
        itemView.setOnClickListener(this);

    }

    public void setOnClickListner(onItemClickListner itemClickListner) {
        mOnItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        //mOnItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }
}
