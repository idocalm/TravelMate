package com.idocalm.travelmate.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idocalm.travelmate.MainActivity;
import com.idocalm.travelmate.R;


public class ProfileSettingsFragment extends Fragment {


    public ProfileSettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        Button passwordResetButton = view.findViewById(R.id.change_password_btn);
        Button deleteAccountButton = view.findViewById(R.id.delete_account_btn);

        passwordResetButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                    });

        });

        deleteAccountButton.setOnClickListener(v -> {
            // show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), "USER_CURRENT_PASSWORD");

                        user.reauthenticate(credential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                    // redirect to login screen or main activity
                                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                                    startActivity(intent);
                                                    if (getActivity() != null)
                                                        getActivity().finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("ProfileSettingsFragment", "Failed to delete account", e);
                                                    Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        return view;
    }
}