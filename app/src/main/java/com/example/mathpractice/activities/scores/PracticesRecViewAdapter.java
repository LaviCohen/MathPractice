package com.example.mathpractice.activities.scores;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.R;

import java.util.ArrayList;

public class PracticesRecViewAdapter extends RecyclerView.Adapter<PracticesRecViewAdapter.ViewHolder> {

    private ArrayList<Practice> practices = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practice_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(practices.get(position));
    }

    @Override
    public int getItemCount() {
        return practices.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPractices(ArrayList<Practice> practices) {
        this.practices = practices;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView level;
        private final TextView exp;
        private final TextView success;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            level = itemView.findViewById(R.id.level);
            exp = itemView.findViewById(R.id.practice);
            success = itemView.findViewById(R.id.success);
        }
        @SuppressLint("SetTextI18n")
        public void update(Practice p){
            level.setText(p.level + "");
            exp.setText(p.exp);
            success.setText(p.success?"Correct":"Wrong");
            success.setTextColor(p.success?Color.GREEN:Color.RED);
        }
    }
    public static class Practice {
        public String exp;
        public int level;
        boolean success;

        public Practice(String exp, int level, boolean success) {
            this.exp = exp;
            this.level = level;
            this.success = success;
        }
    }
}
