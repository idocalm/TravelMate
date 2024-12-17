package com.idocalm.travelmate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.models.Trip;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ManageTripActivity extends AppCompatActivity {


    public void setImage(Bitmap image) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                ImageView imageView = findViewById(R.id.trip_image);
                imageView.setImageBitmap(image);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_trip);

        TextView name = findViewById(R.id.trip_name);

        // get the trip from the intent
        String id  = getIntent().getStringExtra("trip_id");

        Toast.makeText(this, "Trip ID: " + id, Toast.LENGTH_SHORT).show();

        // get the trip from the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Trip trip = new Trip(
                        task.getResult().getId(),
                        task.getResult().getString("name"),
                        task.getResult().getString("destination"),
                        task.getResult().getString("owner"),
                        task.getResult().getString("description"),
                        task.getResult().getString("image"),
                        (ArrayList<String>) task.getResult().get("members"),
                        task.getResult().getTimestamp("start_date"),
                        task.getResult().getTimestamp("end_date"),
                        task.getResult().getTimestamp("created_at"),
                        task.getResult().getTimestamp("last_edited"),
                        task.getResult().getTimestamp("last_opened")
                );

                name.setText(trip.getName());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(trip.getImage());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            setImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

                thread.start();

            }
        });

    }
}