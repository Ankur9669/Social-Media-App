package com.example.socialmediaapp.Daos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDao
{
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersCollections = db.collection("users");

    public void addUser(User user)
    {
        if(user != null)
        {
            executor.execute(() ->
            {
                usersCollections.document(user.getId()).set(user);
            });
        }
    }

    public void getUserByID(String id, UserFilledListener listener)
    {
        //Getting the Details about the user on the background thread
        executor.execute(() ->
        {
           usersCollections.document(id).get().
                   addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                   {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot)
               {
                   if(documentSnapshot != null)
                   {
                       User temp = new User();
                       temp = documentSnapshot.toObject(User.class);;
                       listener.userFilledListener(temp);
                   }
               }
           }).addOnFailureListener(new OnFailureListener()
           {
               @Override
               public void onFailure(@NonNull Exception e)
               {
                    Log.e("Exception", e.toString());
               }
           });
        });
    }
}
//Creating the interface because calling firebase is a Asychronos task
interface UserFilledListener
{
    void userFilledListener(User user);
}
