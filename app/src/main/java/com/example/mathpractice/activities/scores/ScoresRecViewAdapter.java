package com.example.mathpractice.activities.scores;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mathpractice.R;

import java.util.ArrayList;

public class ScoresRecViewAdapter extends RecyclerView.Adapter<ScoresRecViewAdapter.ViewHolder> {

    private ArrayList<Score> scores = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Score score = scores.get(position);
        holder.level.setText(score.getLevel() + "");
        holder.score.setText(score.getScore() + "");
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setScores(ArrayList<Score> scores) {
        this.scores = scores;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView level;
        private final TextView score;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            level = itemView.findViewById(R.id.level);
            score = itemView.findViewById(R.id.score);
        }
    }
}
