package com.idocalm.travelmate.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.idocalm.travelmate.R;

public class ProfileContentFragment extends Fragment {


    public ProfileContentFragment() {
        // Required empty public constructor
    }

    public interface ContentListener {
        void onAccountClick();
        void onSettingsClick();
    }

    private ContentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ContentListener) {
            listener = (ContentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ContentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);

        FrameLayout profileAccount = view.findViewById(R.id.profile_account_button);
        FrameLayout profileSettings = view.findViewById(R.id.profile_settings_button);

        profileAccount.setOnClickListener(v -> {
            listener.onAccountClick();
        });

        profileSettings.setOnClickListener(v -> {
            listener.onSettingsClick();
        });


        return view;
    }

}