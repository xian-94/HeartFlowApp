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

    public interface FetchCallBack<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }

    public <T> void add(String collection, String id, T data, NormalCallBack cb) {
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(l -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public <T> void get(String collection, String id, Class<T> obj, FetchCallBack<T> cb) {
        db.collection(collection).document(id).get()
                .addOnSuccessListener(d -> {
                    if (d.exists()) {
                        cb.onSuccess(d.toObject(obj));
                    } else {
                        cb.onFailure("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));

    }


}
