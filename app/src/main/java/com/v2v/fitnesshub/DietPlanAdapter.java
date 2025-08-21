package com.v2v.fitnesshub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DietPlanAdapter extends RecyclerView.Adapter<DietPlanAdapter.ViewHolder> {

    ArrayList<DietPlanModel> list;
    Context context;

    public DietPlanAdapter(ArrayList<DietPlanModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diet_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DietPlanModel model = list.get(position);
        holder.tvTitle.setText(model.getTitle());
        holder.ivImage.setImageResource(model.getImage());

        holder.itemView.findViewById(R.id.btnViewPlan).setOnClickListener(v -> {
            Intent intent = new Intent(context, DietPlanDetailsActivity.class);
            intent.putExtra("title", model.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivPlanImage);
            tvTitle = itemView.findViewById(R.id.tvPlanTitle);
        }
    }
}
