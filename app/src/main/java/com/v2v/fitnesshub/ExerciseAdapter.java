package com.v2v.fitnesshub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ExerciseItem item);
    }

    private final Context context;
    private List<ExerciseItem> exerciseList;
    private final OnItemClickListener listener;

    public ExerciseAdapter(Context context, List<ExerciseItem> exerciseList, OnItemClickListener listener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    public void updateList(List<ExerciseItem> newList) {
        this.exerciseList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ViewHolder holder, int position) {
        ExerciseItem item = exerciseList.get(position);
        holder.textExerciseName.setText(item.getName());

        Glide.with(context)
                .load(item.getThumbnailUrl())
                .into(holder.imageThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumbnail;
        TextView textExerciseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageThumbnail = itemView.findViewById(R.id.imageThumbnail);
            textExerciseName = itemView.findViewById(R.id.textExerciseName);
        }
    }
}
