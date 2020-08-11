package com.roshanaryal.meromall.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roshanaryal.meromall.Interface.onItemClickListner;
import com.roshanaryal.meromall.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productTitle, productDescription,productPrice;
    public ImageView productImage;
    public onItemClickListner mOnItemClickListner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.product_name_rv);
        productDescription = itemView.findViewById(R.id.product_description_rv);
        productImage = itemView.findViewById(R.id.product_image_rv);
        productPrice=itemView.findViewById(R.id.product_price_rv);
    }

    public void setOnItemClickListner(onItemClickListner itemClickListner) {
        this.mOnItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mOnItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }
}
