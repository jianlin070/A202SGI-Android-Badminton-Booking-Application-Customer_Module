package com.example.badmintonapp.ViewHolder;

import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.badmintonapp.Common.Common;
import com.example.badmintonapp.Interface.ItemClickListener;
import com.example.badmintonapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
View.OnCreateContextMenuListener{

    public TextView txt_orderId, txt_orderPrice, txt_orderCategory, txt_orderTime;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_orderId = itemView.findViewById(R.id.order_id);
        txt_orderPrice = itemView.findViewById(R.id.order_price);
        txt_orderCategory = itemView.findViewById(R.id.order_category);
        txt_orderTime = itemView.findViewById(R.id.order_time);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        TextView orderId = view.findViewById(R.id.order_id);
        TextView menuId = view.findViewById(R.id.order_category);
        contextMenu.add(0,0,getAdapterPosition(), Common.Rate);
        Common.currentOrderId = orderId.getText().toString();
        Common.currentOrderMenuId = menuId.getText().toString();
    }
}
