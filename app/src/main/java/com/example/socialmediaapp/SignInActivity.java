package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialmediaapp.Daos.UserDao;
import com.example.socialmediaapp.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 132 ;
    private static final String TAG = "Message";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ExecutorService executor = Executors.newFixedThreadPool(2);
    private SignInButton signInButton;
    private ProgressBar progressBar;

    @Override
    protected void onStart()
    {
        super.onStart();
        //If the User Has Signed In Previously we Would directky load MainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //On Clicking the SignIn Button
        signInButton = findViewById(R.id.signINButton);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);

                signIn();
            }
        });
    }

    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task)
    {
        try
        {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        }
        catch (ApiException e)
        {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(this, "Sign In failed", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Google sign in failed", e);
            // ...
        }
    }


    private void firebaseAuthWithGoogle(String idToken)
    {
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.GONE);


        AuthCredential crediential = GoogleAuthProvider.getCredential(idToken, null);
        //Doing the work on another Thread
        executor.execute(() ->
        {
            mAuth.signInWithCredential(crediential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                executor.shutdown();
                                updateUI(user);
                            }
                            else
                            {
                                executor.shutdown();
                                updateUI(null);
                            }
                        }
                    });
        });
    }

    private void updateUI(FirebaseUser firebaseUser)
    {
        if(firebaseUser != null)
        {
            signInButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);


            User user = new User(firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getPhotoUrl().toString());

            UserDao dao = new UserDao();
            dao.addUser(user);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
            Log.i(TAG, "if the User is not null");
        }
        else
        {
            signInButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(this, "Something", Toast.LENGTH_SHORT).show();
        }
    }
}