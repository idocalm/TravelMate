package com.idocalm.travelmate.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;
import com.idocalm.travelmate.utils.GalleryManager;

public class ProfileAccountFragment extends Fragment {


    ImageView profileImage;
    public ProfileAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        GalleryManager galleryManager = new GalleryManager(getContext());


        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = galleryManager.getImageUri(result.getData());
                        profileImage.setImageURI(imageUri);

                    }
                });

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = galleryManager.getImageUri(null);
                    profileImage.setImageURI(imageUri);
                }
            });

        ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        galleryManager.launchCamera(cameraLauncher);
                    } else {
                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });


        View view = inflater.inflate(R.layout.fragment_profile_account, container, false);

        profileImage = view.findViewById(R.id.profile_img);
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


        name.setText(Auth.getUser().getName());
        currency.setText(Auth.getUser().getCurrencyString());


        profileImage.setOnLongClickListener(v -> {
            galleryManager.openDialog(galleryLauncher, cameraLauncher, permissionLauncher);
            return true;
        });

        return view;
    }
}