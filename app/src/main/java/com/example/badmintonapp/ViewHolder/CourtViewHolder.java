package com.example.badmintonapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.badmintonapp.Interface.ItemClickListener;
import com.example.badmintonapp.R;

public class CourtViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtcourtName;
    public ImageView court_image;

    private ItemClickListener itemClickListener;


    public CourtViewHolder(View itemView) {
        super(itemView);

        txtcourtName = itemView.findViewById(R.id.court_name);
        court_image = itemView.findViewById(R.id.court_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
