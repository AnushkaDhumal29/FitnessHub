package com.v2v.fitnesshub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.ViewHolder> {

    private final List<MealItem> mealList;

    public DietAdapter(List<MealItem> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diet_item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealItem meal = mealList.get(position);
        holder.tvMealTitle.setText(meal.getTitle());
        holder.tvMealDesc.setText(meal.getDescription());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealTitle, tvMealDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealTitle = itemView.findViewById(R.id.tvMealTitle);
            tvMealDesc = itemView.findViewById(R.id.tvMealDesc);
        }
    }
}
