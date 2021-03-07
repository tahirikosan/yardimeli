package com.tahirabuzetoglu.yardimeli.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {


    private OnItemClickListener mListener;
    private List<Post> mPostList;

    PostHolder myHolder;
    private String type;

    public PostAdapter(List<Post> postList){
        mPostList = postList;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        myHolder = holder;

        Post currentPost = mPostList.get(position);
        holder.tvDescription.setText(currentPost.getDescription());
        holder.tvOwner.setText(currentPost.getOwnerName());

        // set like count view
        if(currentPost.getLikeCount() >= 1){
            holder.tvLikeCount.setText(currentPost.getLikeCount() + " beğenme");
        }else{
            holder.tvLikeCount.setText(" ");
        }

        // set images
        setPicture(currentPost, holder);
        setLikeBtnColor(currentPost, holder);
        setDate(currentPost, holder);



        // changeLikeBtnColor(position);

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }


    class PostHolder extends RecyclerView.ViewHolder{
        private TextView tvDescription;
        private TextView tvOwner;
        private TextView tvLikeCount;
        private TextView tvDate;
        private ImageView ivPicture;
        private ImageView ivOwner;
        private ImageButton ibLike;
        private ImageButton ibComment;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            tvDescription = itemView.findViewById(R.id.tv_post_desc);
            tvOwner = itemView.findViewById(R.id.tv_post_owner);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivPicture = itemView.findViewById(R.id.iv_post_image);
            ivOwner = itemView.findViewById(R.id.iv_owner_image);
            ibLike = itemView.findViewById(R.id.ib_post_like);
            ibComment = itemView.findViewById(R.id.ib_post_comment);

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onLikeButtonClicked(position, ibLike, tvLikeCount);
                        }
                    }
                }
            });

            ibComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onCommentButtonClicked(position);
                        }
                    }
                }
            });

            ivOwner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onOwnerImageClicked(position);
                        }
                    }
                }
            });

            ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onPostClicked(position);
                        }
                    }
                }
            });


        }
    }

    //set picture image resource
    private void setPicture(Post post, PostHolder holder){
        Picasso.get().load(post.getImageUrl()).fit().centerInside().into(holder.ivPicture);
        Picasso.get().load(post.getOwnerImageUrl()).into(holder.ivOwner);

    }

    // on start handle like btn color
    public void setLikeBtnColor(Post post, PostHolder holder){

        if(post.getLikes() != null){
            if(post.isUserLiked()){
                holder.ibLike.setImageResource(R.drawable.ic_heart_red);
            }else{
                holder.ibLike.setImageResource(R.drawable.ic_heart_black);
            }
        }
    }

    public void setDate(Post post, PostHolder holder){
        holder.tvDate.setText(getDate(post.getDate()));
    }

    private String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "Tarih getirilemedi";
        }
    }


    public interface OnItemClickListener{
        void onLikeButtonClicked(int position, ImageButton imageButton, TextView textView);
        void onCommentButtonClicked(int position);
        void onOwnerImageClicked(int position);
        void onPostClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}

