package com.roshanaryal.meromall.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roshanaryal.meromall.Model.OrderModal;
import com.roshanaryal.meromall.R;
import com.roshanaryal.meromall.ViewHolder.AdminNewProductViewHolder;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        mRecyclerView=findViewById(R.id.admin_new_product_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

      //  onStart();

        loadData();




    }

    private void loadData() {
        mReference= FirebaseDatabase.getInstance().getReference().child("Orders");
        FirebaseRecyclerOptions<OrderModal> options=new FirebaseRecyclerOptions.Builder<OrderModal>().setQuery(mReference,OrderModal.class).build();

        FirebaseRecyclerAdapter<OrderModal, AdminNewProductViewHolder> adapter=new FirebaseRecyclerAdapter<OrderModal, AdminNewProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminNewProductViewHolder holder, final int position, @NonNull final OrderModal model)
            {
                holder.nameTextView.setText("To :"+model.getName());
                holder.priceTextView.setText("Total amount :"+model.getTotalAmount());
                holder.phoneTextView.setText("Phone :"+model.getPhone());
                holder.adressTextView.setText("Adress: "+model.getAdress()+","+model.getCity());
                holder.dateTimeTextView.setText("Ordered at :"+model.getDate()+" "+model.getTime());
                holder.viewDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uid=getRef(position).getKey();
                        Intent intent=new Intent(AdminNewOrdersActivity.this, AdminUserProductActivity.class);
                        intent.putExtra("uid",uid);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence option[]=new CharSequence[]
                                {
                                        "Yes"
                                        ,"No"
                                };
                        final AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrdersActivity.this);
                        builder.setTitle("Have you shipped this order product");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    String uid=getRef(position).getKey();
                                    RemoveOrder(uid);

                                }
                                else if (which==1)
                                {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminNewProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout_item,parent,false);
                return new AdminNewProductViewHolder(view) ;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uid) {
        mReference.child(uid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(AdminNewOrdersActivity.this, "Removed::from list", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(AdminNewOrdersActivity.this, "Error: Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();





    }
}
