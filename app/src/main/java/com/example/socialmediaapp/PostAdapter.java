package com.example.socialmediaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmediaapp.models.Posts;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class PostAdapter extends FirestoreRecyclerAdapter<Posts, PostAdapter.ViewHolder>
{
    private Listener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostAdapter(@NonNull FirestoreRecyclerOptions<Posts> options, Listener listener)
    {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Posts model)
    {
        //Binding userImage with the recyclerView
        Glide.with(holder.userImage.getContext())
                .load(model.getImageUrl())
                .circleCrop()
                .into(holder.userImage);

        //Getting the Name Of the User
        holder.userName.setText(model.getUserName());

        //Getting the Size of the ArrayList
        holder.likeCount.setText("" + model.getLikedBy().size());

        //Getting the Text Of The Post
        holder.postText.setText(model.getTextToDisplay());

        //Converting the Long Time Into String And Setting it to the holder
        String timeToDisplay = Utils.timeToDisplay(model.getTimeInMillis());
        holder.createdAt.setText(timeToDisplay);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

        if(model.getLikedBy().contains(currentUserId))
        {
            holder.likeButton.setImageDrawable(ContextCompat
                    .getDrawable(holder.likeButton.getContext(), R.drawable.ic_liked));
        }
        else
        {
            holder.likeButton.setImageDrawable(ContextCompat
                    .getDrawable(holder.likeButton.getContext(), R.drawable.ic_unliked));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.likeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String postId = getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getId();
                listener.OnLikeButtonClick(postId);
            }
        });
        return viewHolder;
    }



    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView userImage;
        TextView userName;
        TextView createdAt;
        TextView postText;
        ImageView likeButton;
        TextView likeCount;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            createdAt = itemView.findViewById(R.id.createdAt);
            postText = itemView.findViewById(R.id.postText);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeCount = itemView.findViewById(R.id.likeCount);
        }
    }
}

interface Listener
{
    public void OnLikeButtonClick(String postId);
}
