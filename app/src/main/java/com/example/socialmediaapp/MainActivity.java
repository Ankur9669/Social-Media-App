package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.socialmediaapp.Daos.LikeButton;
import com.example.socialmediaapp.Daos.PostDao;
import com.example.socialmediaapp.models.Posts;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements Listener {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private PostAdapter madapter;

    private FloatingActionButton addPostButton;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        ColorDrawable drawable = new ColorDrawable(Color.parseColor("#7295e0"));

        actionBar.setBackgroundDrawable(drawable);


        addPostButton = findViewById(R.id.addPost);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getBaseContext(), AddNewPost.class);
                startActivity(intent);
            }
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView()
    {
        CollectionReference postsCollections = db.collection("posts");

        Query query = postsCollections.orderBy("timeInMillis", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Posts> options = new FirestoreRecyclerOptions
                .Builder<Posts>()
                .setQuery(query, Posts.class)
                .build();

        madapter = new PostAdapter(options, this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(madapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        madapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        madapter.stopListening();
    }

    @Override
    public void OnLikeButtonClick(String postId)
    {
        PostDao postDao = new PostDao();
        postDao.updateLikes(postId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.signOut)
        {
            //Building a alert Button
            builder = new AlertDialog.Builder(this);
            builder.setMessage("Are You Sure Want To Sign Out").
                    setCancelable(true).
                    setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setIcon(R.drawable.ic_baseline_warning_24);

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("SignOut");
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}