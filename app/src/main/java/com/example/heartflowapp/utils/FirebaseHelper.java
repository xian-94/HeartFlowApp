package com.example.heartflowapp.utils;

import android.telephony.ims.RegistrationManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseHelper() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // Callback for registration
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(String message);
    }

    // Normal CRUD callback
    public interface NormalCallBack {
        void onSuccess();

        void onFailure(String message);
    }

    /**
     * Register new
     *
     * @param email    new user email
     * @param password new user password
     * @param cb       callback
     */
    public void register(String email, String password, AuthCallback cb) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        cb.onSuccess(user);
                    } else {
                        cb.onFailure(task.getException().getMessage());
                    }
                });
    }

    public <T> void save(String collection, String id, T data, NormalCallBack cb) {
        Log.d("FirebaseHelper", "Saving to collection: " + collection + ", ID: " + id);
        Log.d("FirebaseHelper", "Data: " + data.toString());
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(l -> {
                    Log.d("FirebaseHelper", "Data saved successfully");
                    cb.onSuccess();
                })
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }





}
