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

public class LevelsRecViewAdapter extends RecyclerView.Adapter<LevelsRecViewAdapter.ViewHolder> {

    private ArrayList<Level> levels = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(levels.get(position));
    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView levelView;
        private final TextView scoreView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            levelView = itemView.findViewById(R.id.level);
            scoreView = itemView.findViewById(R.id.score);
        }
        public void update(Level level) {
            levelView.setText(level.level);
            scoreView.setText(level.score);
            double score = Double.parseDouble(level.score);
            if (score < 60) {
                scoreView.setTextColor(Color.RED);
            } else if (score < 80) {
                scoreView.setTextColor(Color.YELLOW);
            } else {
                scoreView.setTextColor(Color.GREEN);
            }
        }
    }
    public static class Level {
        public String level;
        public String score;

        public Level(String level, String score) {
            this.level = level;
            this.score = score;
        }

        public String getLevel() {
            return level;
        }
    }
}
