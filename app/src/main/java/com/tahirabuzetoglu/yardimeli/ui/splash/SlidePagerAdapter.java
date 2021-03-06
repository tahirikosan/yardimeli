package com.tahirabuzetoglu.yardimeli.ui.splash;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.tahirabuzetoglu.yardimeli.R;

import java.util.List;

public class SlidePagerAdapter extends RecyclerView.Adapter<SlidePagerAdapter.ViewHolder> {

    List<SlideModel> list;
    Context context;

    public SlidePagerAdapter(List<SlideModel> list) {
        this.list=list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        ImageView ivPoster;
        TextView tvTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    @NonNull
    @Override
    public SlidePagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SlidePagerAdapter.ViewHolder holder, int position) {
        SlideModel m = list.get(position);
        holder.ivPoster.setImageResource(m.getImageID());
        holder.tvTitle.setText(m.getTitle());

        switch (position){
            case 0:
                holder.layout.setBackgroundColor(Color.parseColor("#f5dcfa"));
                break;
            case 1:
                holder.layout.setBackgroundColor(Color.parseColor("#ffedd5"));
                break;
            case 2:
                holder.layout.setBackgroundColor(Color.parseColor("#e9effb"));
                break;
            case 3:
                holder.layout.setBackgroundColor(Color.parseColor("#f4f8fb"));
                break;
            case 4:
                holder.layout.setBackgroundColor(Color.parseColor("#8cfffb"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
