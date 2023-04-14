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
 * Subclass of {@link RecyclerView}, specified for displaying practices data (full scores data).
 * */
public class PracticesRecViewAdapter extends RecyclerView.Adapter<PracticesRecViewAdapter.ViewHolder> {

    /**
     * The data for this recycle view.
     */
    private ArrayList<Practice> practices = new ArrayList<>();

    /**
     * This method inflates the layout into view and wrap it into the {@link ViewHolder}.
     * @param parent the parent {@link ViewGroup}.
     * @param viewType the type of the view.
     * @return a {@link ViewHolder} instance.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practice_layout, parent, false);
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
        holder.update(practices.get(position));
    }

    /**
     * @return the data items count.
     */
    @Override
    public int getItemCount() {
        return practices.size();
    }

    /**
     * This method sets the dataset.
     * @param practices the new dataset.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setPractices(ArrayList<Practice> practices) {
        this.practices = practices;
        notifyDataSetChanged();
    }

    /**
     * View holder class, specific for levels recycle view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * the level's view.
         */
        private final TextView level;

        /**
         * the expression's view.
         */
        private final TextView exp;

        /**
         * the success's view.
         */
        private final TextView success;

        /**
         * Constructor that initialize the views.
         * @param itemView the item view.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            level = itemView.findViewById(R.id.level);
            exp = itemView.findViewById(R.id.practice);
            success = itemView.findViewById(R.id.success);
        }

        /**
         * This method updates the data inside this {@link LevelsRecViewAdapter.ViewHolder}.
         * @param practice the data.
         */
        @SuppressLint("SetTextI18n")
        public void update(Practice practice){
            level.setText(practice.level + "");
            exp.setText(practice.exp);
            success.setText(practice.success?"Correct":"Wrong");
            success.setTextColor(practice.success?Color.GREEN:Color.RED);
        }
    }
    /**
     * This class represents practice score data.
     * Objects of this type will be sent to the {@link RecyclerView}.
     */
    public static class Practice {
        /**
         * The practice represented, as mathematical expression.
         */
        public String exp;
        /**
         * The practice's level.
         */
        public int level;
        /**
         * Whether the user succeed or not.
         */
        boolean success;

        /**
         * Basic constructor.
         * @param exp the expression.
         * @param level the level.
         * @param success the success.
         */
        public Practice(String exp, int level, boolean success) {
            this.exp = exp;
            this.level = level;
            this.success = success;
        }
    }
}
