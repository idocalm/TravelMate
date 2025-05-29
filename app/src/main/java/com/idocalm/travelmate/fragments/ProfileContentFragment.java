package com.idocalm.travelmate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.auth.Auth;

public class ProfileContentFragment extends Fragment {


    public ProfileContentFragment() {
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



    private void shareInviteLink(String link, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Join me on tarvelmate! Enter my code: " + link);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Invite a friend"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_content, container, false);
        Button addFriendButton = view.findViewById(R.id.add_friend_btn);
        EditText addFriendInput = view.findViewById(R.id.add_friend_input);

        addFriendButton.setOnClickListener(v -> {
            String friendId = addFriendInput.getText().toString();
            if (friendId.isEmpty()) {
                addFriendInput.setError("Please enter a valid ID");
                return;
            }

            if (friendId.equals(Auth.getUser().getId().toString())) {
                addFriendInput.setError("You cannot add yourself as a friend");
                return;
            }

            // check if the friendId is already in the friends list
            if (Auth.getUser().getFriendsIds().contains(friendId)) {
                addFriendInput.setError("This user is already your friend");
                return;
            }

            Auth.getUser().addFriend(friendId, getContext());
            addFriendInput.setText("");
        });

        LinearLayout inviteFriends = view.findViewById(R.id.profile_friend_button);

        inviteFriends.setOnClickListener(v -> {
            shareInviteLink(Auth.getUser().getId().toString(), getContext());

        });

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