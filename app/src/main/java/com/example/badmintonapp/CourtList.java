package com.example.badmintonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.badmintonapp.Interface.ItemClickListener;
import com.example.badmintonapp.Model.Court;
import com.example.badmintonapp.ViewHolder.CourtViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourtList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference courtList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Court, CourtViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_list);
        getSupportActionBar().hide();

        //Firebase
        database = FirebaseDatabase.getInstance();
        courtList = database.getReference("Courts");

        recyclerView = findViewById(R.id.recycler_court);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId != null)
        {
            loadListCourt(categoryId);
        }
    }

    private void loadListCourt(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Court, CourtViewHolder>(Court.class,R.layout.court_item,CourtViewHolder.class,
                courtList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(CourtViewHolder courtViewHolder, Court court, int i) {
                courtViewHolder.txtcourtName.setText(court.getName());
                Glide.with(getBaseContext()).load(court.getImage()).into(courtViewHolder.court_image);
                Court selectedCourt = court;
                courtViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activity
                        Intent courtDetail = new Intent(CourtList.this,CourtDetail.class);
                        courtDetail.putExtra("CourtId", adapter.getRef(position).getKey());
                        startActivity(courtDetail);
                    }
                });
            }
        };
        //Adapter

        recyclerView.setAdapter(adapter);
    }
}