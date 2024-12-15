package com.example.heartflowapp.controller;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseManager {

    private final FirebaseFirestore db;

    public DatabaseManager() {
        this.db = FirebaseFirestore.getInstance();
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

    public <T> void add(String collection, String id, T data, NormalCallBack cb) {
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(l -> {
                    cb.onSuccess();
                })
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }


}
