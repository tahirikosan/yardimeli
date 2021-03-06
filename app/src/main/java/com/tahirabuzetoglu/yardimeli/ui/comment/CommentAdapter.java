package com.tahirabuzetoglu.yardimeli.ui.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private OnItemClickListener mListener;
    private List<Comment> mCommentList;


    public CommentAdapter(Context context , List<Comment> CommentList){
        this.context = context;
        mCommentList = CommentList;
    }


    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        Comment currentComment = mCommentList.get(position);
        holder.tvOwnerName.setText(currentComment.getUsername() + " : ");
        holder.tvComment.setText(currentComment.getComment());

    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    class CommentHolder extends RecyclerView.ViewHolder{
        private TextView tvOwnerName;
        private TextView tvComment;
        private ImageButton ibOptions;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
            tvComment = itemView.findViewById(R.id.tv_owner_comment);
            ibOptions = itemView.findViewById(R.id.ib_comment_options);


            ibOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onOptionsClicked(position, ibOptions);
                        }
                    }
                }
            });

        }
    }




    public interface OnItemClickListener{
        void onOptionsClicked(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}
