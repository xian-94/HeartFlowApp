package com.example.heartflowapp.controller;

import android.widget.Toast;

import com.example.heartflowapp.model.BloodType;
import com.example.heartflowapp.model.Donor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

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

    public <T> void update(String collection, String id, T data, NormalCallBack cb) {
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(l -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public <T> void add(String collection, T data, NormalCallBack cb) {
        db.collection(collection).add(data)
                .addOnSuccessListener(l -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public <T> void get(String collection, String id, Class<T> obj, FetchCallBack<T> cb) {
        db.collection(collection).document(id).get()
                .addOnSuccessListener(d -> {
                    if (d.exists()) {
                        T retrievedObject = d.toObject(obj);
                        // Handle convert blood type
                        if (retrievedObject instanceof Donor) {
                            Donor donor = (Donor) retrievedObject;
                            String bloodTypeText = d.getString("type");
                            if (bloodTypeText != null) {
                                donor.setType(BloodType.parseBloodTpe(bloodTypeText));

                            }
                        }
                        cb.onSuccess(retrievedObject);
                    }
                })
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));

    }

    public void getOneField(String collection, String id, FetchCallBack<Map<String, Object>> cb) {
        db.collection(collection).document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        cb.onSuccess(documentSnapshot.getData());
                    } else {
                        cb.onFailure("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }

    public CollectionReference getRef(String collection) {
        return db.collection(collection);
    }

    public void addToArray(String collection, String documentId, String field, String value, NormalCallBack cb) {
        db.collection(collection)
                .document(documentId)
                .update(field, FieldValue.arrayUnion(value))
                .addOnSuccessListener(aVoid -> cb.onSuccess())
                .addOnFailureListener(e -> cb.onFailure(e.getMessage()));
    }


}
