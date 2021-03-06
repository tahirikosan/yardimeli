package com.tahirabuzetoglu.yardimeli.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;


import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostHolder> {

    private OnItemClickListener listener;
    private List<Post> postList;

    public UserPostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public UserPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);
        return new UserPostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostHolder holder, int position) {
        Post post = postList.get(position);

        // set images
        Picasso.get().load(post.getImageUrl()).resize(270,270).centerCrop().into(holder.ivUserPost);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class UserPostHolder extends RecyclerView.ViewHolder{
        private ImageView ivUserPost;

        public UserPostHolder(@NonNull View itemView) {
            super(itemView);

            ivUserPost = itemView.findViewById(R.id.iv_user_post);

            ivUserPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onImageClick(position);
                        }
                    }
                }
            });

        }
    }

    public interface OnItemClickListener{
        void onImageClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
