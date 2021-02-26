package com.example.socialmediaapp.Daos;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.PostAdapter;
import com.example.socialmediaapp.models.Posts;
import com.example.socialmediaapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostDao
{
    //Declaring the number of threads
    private ExecutorService executor = Executors.newFixedThreadPool(3);

    //Getting the instance of the firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Getting the reference of the Collections
    private CollectionReference postsCollections = db.collection("posts");

    //Getting the FireBaseAuth
    private FirebaseAuth auth;

    //Function For Updating Likes inthe Database
    public void updateLikes(String postId)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUser = auth.getCurrentUser().getUid();

        PostDao postDao = new PostDao();
        postDao.getPostById(postId, new LikeButton()
        {
            @Override
            public void likedButtonInfo(Posts post)
            {
                //If the post is already Liked than
                if(post.getLikedBy().contains(currentUser))
                {
                    executor.execute(() ->
                    {
                        //Getting the post on which the user has Clicked
                        CollectionReference postsCollection = db.collection("posts");
                        postsCollection.document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot)
                            {
                                //Got the post now removing the current user
                                Posts post = documentSnapshot.toObject(Posts.class);
                                post.getLikedBy().remove(currentUser);

                                //Setting new information in the firebase
                                postsCollection.document(postId).set(post);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.i("Message", e.toString());
                                    }
                                });
                    });
                }
                //If the post is not Liked Than
                else
                {
                    executor.execute(() ->
                    {
                        CollectionReference postsCollection = db.collection("posts");
                        postsCollection.document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot)
                            {
                                Posts post = documentSnapshot.toObject(Posts.class);
                                post.getLikedBy().add(currentUser);
                                postsCollection.document(postId).set(post);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Log.i("Message", e.toString());
                                    }
                                });
                    });
                }
            }
        });
    }

    public void getPostById(String postId, LikeButton listener)
    {
        executor.execute(() ->
        {
            CollectionReference usersCollection = db.collection("posts");
            usersCollection.document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    listener.likedButtonInfo(documentSnapshot.toObject(Posts.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Message", e.toString());
                }
            });
        });
    }

    //Function for adding new Post
    public void addPost(String text)
    {
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        if(userId != null)
        {
            executor.execute(() ->
            {
                //Getting the instance of the UserDao
                UserDao dao = new UserDao();

                //Getting the current User From the Database Although
                // in this case we can directy get the user from auth
                dao.getUserByID(userId, new UserFilledListener()
                {
                    @Override
                    public void userFilledListener(User user)
                    {
                        //Getting the name of the user
                         String userName = user.getName();

                         //Getting the Current time in millis
                         Long currentTime = System.currentTimeMillis();

                         //Getting the url of the user
                         String userImageUrl = user.getImageUrl();

                         //Creating a new POst Object
                         Posts post = new Posts(text, currentTime, userName, userId, userImageUrl);

                         //Setting the data in the postsCollection
                         postsCollections.document().set(post);
                    }
                });
            });
        }
        else
        {
            Log.e("Error", "User is null");
        }
    }
}
