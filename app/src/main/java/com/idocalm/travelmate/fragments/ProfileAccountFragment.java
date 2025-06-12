package com.idocalm.travelmate.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.adapters.FriendsListAdapter;
import com.idocalm.travelmate.enums.CurrencyType;
import com.idocalm.travelmate.models.User;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileAccountFragment extends Fragment {


    ImageView profileImage;
    public ProfileAccountFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_account, container, false);
        profileImage = view.findViewById(R.id.profile_img);

        ViewPager friendsList = view.findViewById(R.id.friends_list);
        ArrayList<String> friends = Auth.getUser().getFriendsIds();
        ArrayList<User> friendsListData = new ArrayList<>();

        int totalFriends = friends.size();

        AtomicInteger completedCount = new AtomicInteger(0);

        for (String id : friends) {
            final String friendId = id;

            FirebaseFirestore.getInstance().collection("users").document(friendId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User friend = User.fromDocument(documentSnapshot);
                            friendsListData.add(friend);
                            Log.d("ProfileAccountFragment", "Friend added: " + friend.getName());
                        } else {
                            Log.d("ProfileAccountFragment", "Friend not found: " + friendId);
                        }

                        if (completedCount.incrementAndGet() == totalFriends) {
                            // All fetches done!
                            FriendsListAdapter adapter = new FriendsListAdapter(getContext(), friendsListData);
                            friendsList.setAdapter(adapter);
                            Log.d("ProfileAccountFragment", "All friends fetched, adapter set.");
                        }
                    })
                    .addOnFailureListener(e -> {

                        if (completedCount.incrementAndGet() == totalFriends) {
                            FriendsListAdapter adapter = new FriendsListAdapter(getContext(), friendsListData);
                            friendsList.setAdapter(adapter);

                        }
                    });
        }



        // attempt to fetch profile image from the db and load it into the image view
        if (Auth.getUser().getProfileImage() != null) {
            Glide.with(getContext())
                    .load(Auth.getUser().getProfileImage())
                    .into(profileImage);
        } else {
            Log.d("ProfileAccountFragment", "No profile image found");
        }


        profileImage.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Long click to change profile photo", Toast.LENGTH_SHORT).show();
        });

        TextView name = view.findViewById(R.id.profile_name);
        TextView currency = view.findViewById(R.id.profile_currency);

        ImageView editName = view.findViewById(R.id.edit_name);
        ImageView editCurrency = view.findViewById(R.id.edit_currency);

        editName.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Edit Name");
            builder.setMessage("Enter your new name");

            // Set up the input
            final EditText input = new EditText(getContext());
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                String newName = input.getText().toString();
                name.setText(newName);
                Auth.getUser().setName(newName);
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        editCurrency.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Edit Currency");

            // show all the CurrencyType options and let the user select one
            String[] currencyOptions = {"USD", "EUR", "ILS"};
            builder.setItems(currencyOptions, (dialog, which) -> {
                String selectedCurrency = currencyOptions[which];
                currency.setText(selectedCurrency);
                Auth.getUser().setCurrencyType(CurrencyType.valueOf(selectedCurrency));
            });

            // Set up the buttons
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


            builder.show();
        });

        name.setText(Auth.getUser().getName());
        currency.setText(Auth.getUser().getCurrencyString());

        profileImage.setOnLongClickListener(v -> {
            showUrlInputDialog();
            return true;
        });

        return view;
    }

    private void showUrlInputDialog() {

        AlertDialog.Builder urlDialogBuilder = new AlertDialog.Builder(getContext());
        urlDialogBuilder.setTitle("Enter Image URL");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        urlDialogBuilder.setView(input);

        urlDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageUrl = input.getText().toString().trim();
                if (!imageUrl.isEmpty()) {
                    Log.d("GalleryManager", "Image URL: " + imageUrl);
                    handleImageUrl(imageUrl);
                } else {
                    Toast.makeText(getContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        urlDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        urlDialogBuilder.show();
    }

    private void handleImageUrl(String imageUrl) {
        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .into(profileImage);
        Auth.getUser().setProfile(imageUrl);
    }
}