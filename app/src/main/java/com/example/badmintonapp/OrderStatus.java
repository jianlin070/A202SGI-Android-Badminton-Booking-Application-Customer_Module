package com.example.badmintonapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.badmintonapp.Common.Common;
import com.example.badmintonapp.Interface.ItemClickListener;
import com.example.badmintonapp.Model.Court;
import com.example.badmintonapp.Model.Order;
import com.example.badmintonapp.Model.Rating;
import com.example.badmintonapp.Model.Request;
import com.example.badmintonapp.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.List;

public class OrderStatus extends AppCompatActivity implements RatingDialogListener {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;
    DatabaseReference ratings;

    String menuId= "";

    ImageView barcodeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        getSupportActionBar().hide();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        ratings = database.getReference("Rating");


        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        barcodeImg = findViewById(R.id.barcode);

        loadOrders(Common.currentUser.getPhone());
    }


    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.orderhistory_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int i) {
                orderViewHolder.txt_orderId.setText(adapter.getRef(i).getKey());

                List<Order> courts = request.getCourts();
                String courtString = "";
                for (int j=0; j<courts.size(); j++){
                    if(j!=courts.size()-1)
                        courtString += courts.get(j).getTimeslot() + ", ";
                    else
                        courtString += courts.get(j).getTimeslot();

                }
                orderViewHolder.txt_orderTime.setText(courtString);

                orderViewHolder.txt_orderPrice.setText(request.getTotal());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference categoryTable = database.getReference("Category");

                String categoryName = courts.get(0).getMenuId();

                categoryTable.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(categoryName).exists()){
                            Court court = snapshot.child(categoryName).getValue(Court.class);
                            orderViewHolder.txt_orderCategory.setText(court.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Hold orderId in new string
                        String orderId = orderViewHolder.txt_orderId.getText().toString().trim();
                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            //Initialize bit matrix
                            BitMatrix matrix = writer.encode(orderId, BarcodeFormat.QR_CODE, 300,300);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);
                            //Set bitmap into image
                            barcodeImg.setImageBitmap(bitmap);
                            //Initialize input manager
                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(orderViewHolder.txt_orderId.getApplicationWindowToken(),0);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.Rate)){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ratingsTbl = database.getReference("Rating");
            ratingsTbl.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(Common.currentOrderId).exists()){
                        Toast.makeText(OrderStatus.this, "This order has already given feedback!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        showRatingDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        return true;
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Bad","Neutral","Good","Very Good"))
                .setDefaultRating(1)
                .setTitle("How is your experience?")
                .setDescription("We would like to hear from you!")
                .setTitleTextColor(R.color.primaryColor)
                .setDescriptionTextColor(R.color.primaryColor)
                .setHint("Comment")
                .setHintTextColor(R.color.white)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.grey)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(OrderStatus.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int starValue, @NonNull String comment) {

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating rating = new Rating(Common.currentOrderId,
                        Common.currentOrderMenuId,
                        String.valueOf(starValue),
                        comment.trim(),
                        Common.currentUser.getName());
                ratings.child(Common.currentOrderId).setValue(rating);
                Toast.makeText(OrderStatus.this, "Thank you for the feedback!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }


    @Override
    public void onNeutralButtonClicked() {

    }
}