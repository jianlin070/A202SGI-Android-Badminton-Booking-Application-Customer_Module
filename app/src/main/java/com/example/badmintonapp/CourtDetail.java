package com.example.badmintonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.badmintonapp.Common.Common;
import com.example.badmintonapp.Database.Database;
import com.example.badmintonapp.Model.Court;
import com.example.badmintonapp.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CourtDetail extends AppCompatActivity {

    TextView court_name,court_price,court_description;
    ImageView court_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    String courtId="";

    //Dropdown Menu
    Spinner spinner;
    String[] time = {"0800-1000","1000-1200","1200-1400","1400-1600","1600-1800","1800-2000","2000-2200","2200-2400"};


    FirebaseDatabase database;
    DatabaseReference courts;


    Court currentCourt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_detail);
        getSupportActionBar().hide();
        spinner = findViewById(R.id.spinner);

        //Dropdown Menu implementations
        //TESTING
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CourtDetail.this, android.R.layout.simple_spinner_item,time){
            @Override
            public boolean isEnabled(int position) {

                return true;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Firebase
        database = FirebaseDatabase.getInstance();
        courts = database.getReference("Courts");

        //Init View
        btnCart = findViewById(R.id.btnCart);

//        if (Common.currentCourt != null)
//            btnCart.setEnabled(false);
//        else
//            btnCart.setEnabled(true);


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        courtId,
                        currentCourt.getName(),
                        currentCourt.getPrice(),
                        spinner.getSelectedItem().toString(),
                        currentCourt.getMenuId()));

                Common.currentCourt = currentCourt.getMenuId();
                Toast.makeText(CourtDetail.this,"Added to Cart",Toast.LENGTH_SHORT).show();
                if (Common.currentCourt != null)
                    btnCart.setEnabled(false);
            }
        });

        court_description = findViewById(R.id.court_description);
        court_name = findViewById(R.id.court_name);
        court_price = findViewById(R.id.court_price);
        court_image = findViewById(R.id.img_court);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get court id from Intent
        if(getIntent() != null)
            courtId = getIntent().getStringExtra("CourtId");
        if(!courtId.isEmpty())
        {
            getDetailCourt(courtId);
        }
    }

    private void getDetailCourt(String courtId) {
        courts.child(courtId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentCourt = snapshot.getValue(Court.class);

                Glide.with(getBaseContext()).load(currentCourt.getImage()).into(court_image);

                collapsingToolbarLayout.setTitle(currentCourt.getName());

                court_price.setText(currentCourt.getPrice());

                court_name.setText(currentCourt.getName());

                court_description.setText(currentCourt.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}