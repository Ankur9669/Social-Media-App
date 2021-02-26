package com.example.socialmediaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialmediaapp.Daos.PostDao;

public class AddNewPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);

        EditText editText = findViewById(R.id.postDescriptionEditTextView);
        Button postButton = findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String text = editText.getText().toString();
                if(!text.isEmpty())
                {
                    PostDao dao = new PostDao();
                    dao.addPost(text);
                    finish();
                }
                else
                {
                    Toast.makeText(getBaseContext(),
                            "Please Enter Something in the EditText",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }
}