package com.example.myapplication.dao;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GenericDaoImpl<T> implements GenericDao {
    Context mContext;
    Class<T> mTypeClass;
    String mCollectionPath;
    CollectionReference mDocumentRef;

    protected GenericDaoImpl(Context context, Class<T> typeClass, String collectionPath) {
        this.mContext = context;
        this.mTypeClass = typeClass;
        this.mCollectionPath = collectionPath;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mDocumentRef = db.collection(collectionPath);
    }

}
