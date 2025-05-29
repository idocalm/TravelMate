package com.idocalm.travelmate.auth;


import static com.idocalm.travelmate.MainActivity.RC_SIGN_IN;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idocalm.travelmate.HomeActivity;
import com.idocalm.travelmate.MainActivity;
import com.idocalm.travelmate.R;
import com.idocalm.travelmate.RegisterActivity;
import com.idocalm.travelmate.models.User;

public class Auth {

    private static volatile User instance = null;

    public static User instantiateUser(Activity act) {
        if (instance == null) {
            synchronized (User.class) {

                if (instance != null) {
                    Log.d("Auth", "User already instantiated.");
                    return instance;
                }

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Log.d("Auth", "ERROR: Instantiate user failed - user is null.");
                    Intent intent = new Intent(act, MainActivity.class);
                    act.startActivity(intent);
                    act.finish();
                    return null;
                }

                DocumentReference ref = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                instance = User.fromDocument(documentSnapshot);

                                Intent intent = new Intent(act, HomeActivity.class);

                                act.startActivity(intent);
                                act.finish();
                            } else {
                                Intent intent = new Intent(act, RegisterActivity.class);
                                act.startActivity(intent);
                                act.finish();
                            }
                        } else {
                            Log.d("Auth", "ERROR: Instantiate user failed - db task is not successful.");
                        }
                    }
                });
            }
        }
        return instance;
    }

    public static void attemptDejaVu(Activity act) {
        if (Auth.isLoggedIn()) {
            Log.d("DejaVu", "DejaVu attempt NOW.");
            if (instance == null) {
                instance = instantiateUser(act);
            } else {
                Log.d("DejaVu", "DejaVu attempt - user already exists.");
                Intent intent = new Intent(act, HomeActivity.class);
                act.startActivity(intent);
                act.finish();
            }
        }
    }

public static void logOut(Activity act) {
    FirebaseAuth.getInstance().signOut();
    Intent intent = new Intent(act, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    act.startActivity(intent);
    act.finish();
    instance = null;
}


    public static User getUser() {
        return instance;
    }

    public static void loginWithGoogle(MainActivity activity, Runnable onSuccess, Runnable onFailure) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .build();

        activity.mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Intent signInIntent = activity.mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);

        // Handle the result in onActivityResult of MainActivity
        activity.setSignInResultHandler((GoogleSignInAccount account) -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Auth.instantiateUser(activity);
                            onSuccess.run();
                        } else {
                            onFailure.run();
                        }
                    });
        }, onFailure);
    }

    public static void login(Activity activity, String email, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                Auth.instantiateUser(activity);
                onSuccess.run();
            })
            .addOnFailureListener(e -> {
                // No user found - register it
                register(email, password, onSuccess, onFailure);
            });
    }

    public static void register(String email, String password, Runnable onSuccess, Runnable onFailure) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                onSuccess.run();
            })
            .addOnFailureListener(e -> {
                onFailure.run();
            });
    }

    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

}
