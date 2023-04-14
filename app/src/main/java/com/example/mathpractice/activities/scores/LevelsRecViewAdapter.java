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

/**
 * Subclass of {@link RecyclerView}, specified for displaying levels scores data.
 * */
public class LevelsRecViewAdapter extends RecyclerView.Adapter<LevelsRecViewAdapter.ViewHolder> {

    /**
     * The data for this recycle view.
     */
    private ArrayList<Level> levels = new ArrayList<>();

    /**
     * This method inflates the layout into view and wrap it into the {@link ViewHolder}.
     * @param parent the parent {@link ViewGroup}.
     * @param viewType the type of the view.
     * @return a {@link ViewHolder} instance.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method give the data to the view holder.
     * @param holder the holder to update.
     * @param position the data's position.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(levels.get(position));
    }

    /**
     * @return the data items count.
     */
    @Override
    public int getItemCount() {
        return levels.size();
    }

    /**
     * This method sets the dataset.
     * @param levels the new dataset.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
        notifyDataSetChanged();
    }

    /**
     * View holder class, specific for levels recycle view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The level's view.
         */
        private final TextView levelView;

        /**
         * The score's view.
         */
        private final TextView scoreView;

        /**
         * Constructor that initialize the views.
         * @param itemView the item view.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            levelView = itemView.findViewById(R.id.level);
            scoreView = itemView.findViewById(R.id.score);
        }

        /**
         * This method updates the data inside this {@link ViewHolder}.
         * @param level the data.
         */
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

    /**
     * This class represents level score data.
     * Objects of this type will be sent to the {@link RecyclerView}.
     */
    public static class Level {
        /**
         * The level represented.
         */
        public String level;
        /**
         * The score on the level represented.
         */
        public String score;

        /**
         * Basic constructor.
         * @param level the level.
         * @param score the score.
         */
        public Level(String level, String score) {
            this.level = level;
            this.score = score;
        }

        /**
         * Level's getter.
         * @return the level.
         */
        public String getLevel() {
            return level;
        }
    }
}
