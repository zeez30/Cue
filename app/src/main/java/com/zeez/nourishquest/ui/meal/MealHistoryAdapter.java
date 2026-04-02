package com.zeez.nourishquest.ui.meal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.databinding.ItemMealHistoryBinding;
import com.zeez.nourishquest.util.DateUtils;

// RecyclerView adapter for the meal history list.
// Uses ListAdapter + DiffUtil so only changed rows rebind on LiveData updates.
public class MealHistoryAdapter extends ListAdapter<MealEntry, MealHistoryAdapter.ViewHolder> {

    public MealHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MealEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MealEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull MealEntry a, @NonNull MealEntry b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull MealEntry a, @NonNull MealEntry b) {
                    return a.getSatisfactionRating() == b.getSatisfactionRating()
                            && a.getFoodDescription().equals(b.getFoodDescription())
                            && a.getTimestamp() == b.getTimestamp();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealHistoryBinding binding = ItemMealHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMealHistoryBinding binding;

        ViewHolder(ItemMealHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MealEntry entry) {
            binding.textMealTime.setText(DateUtils.formatFull(entry.getTimestamp()));
            binding.textMealDescription.setText(entry.getFoodDescription());
            binding.textMealSatisfaction.setText(
                    "Satisfaction: " + entry.getSatisfactionRating() + "/10");

            // Build the meta line — skip meal type if it's null (rows from before v2 migration)
            String mealType = mealTypeLabel(entry.getMealType());
            String context  = contextLabel(entry.getEatingContext());
            String emotion  = emotionLabel(entry.getEmotionalState());

            String meta = mealType.isEmpty()
                    ? context + " · " + emotion
                    : mealType + " · " + context + " · " + emotion;
            binding.textMealContext.setText(meta);
        }

        private String mealTypeLabel(String raw) {
            if (raw == null || raw.isEmpty()) return "";
            switch (raw) {
                case "BREAKFAST": return "Breakfast";
                case "LUNCH":     return "Lunch";
                case "DINNER":    return "Dinner";
                case "SNACK":     return "Snack";
                default:          return raw.charAt(0) + raw.substring(1).toLowerCase();
            }
        }

        private String contextLabel(String raw) {
            if (raw == null) return "";
            switch (raw) {
                case "HOME":       return "Home";
                case "RESTAURANT": return "Restaurant";
                case "DESK":       return "At desk";
                case "CAR":        return "On the go";
                case "SOCIAL":     return "Social";
                default:           return "Other";
            }
        }

        private String emotionLabel(String raw) {
            if (raw == null) return "";
            return raw.charAt(0) + raw.substring(1).toLowerCase();
        }
    }
}
