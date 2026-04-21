package com.zeez.nourishquest.ui.hunger;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.databinding.ItemHungerLogBinding;
import com.zeez.nourishquest.util.DateUtils;
import com.zeez.nourishquest.util.HungerScaleHelper;

public class HungerLogAdapter extends ListAdapter<HungerLog, HungerLogAdapter.ViewHolder> {

    // Interface for handling item deletion events
    public interface OnDeleteListener {
        void onDelete(HungerLog log);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.onDeleteListener = listener;
    }

    // Helper to retrieve item by position for the swipe handler
    public HungerLog getItemAt(int position) {
        return getItem(position);
    }

    public OnDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public HungerLogAdapter() {
        super(DIFF_CALLBACK);
    }

    // Utility to calculate updates between two lists efficiently
    private static final DiffUtil.ItemCallback<HungerLog> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HungerLog>() {
                @Override
                public boolean areItemsTheSame(@NonNull HungerLog a, @NonNull HungerLog b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull HungerLog a, @NonNull HungerLog b) {
                    return a.getScaleLevel() == b.getScaleLevel()
                            && a.getMealPhase().equals(b.getMealPhase())
                            && a.getTimestamp() == b.getTimestamp();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHungerLogBinding binding = ItemHungerLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHungerLogBinding binding;

        ViewHolder(ItemHungerLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Binds entity data to the item layout components
        void bind(HungerLog log) {
            binding.textLogTime.setText(DateUtils.formatTime(log.getTimestamp()));
            binding.textLogPhase.setText(log.getMealPhase().equals("BEFORE")
                    ? "Before meal" : "After meal");
            binding.textLogLevel.setText(String.valueOf(log.getScaleLevel()));
            binding.textLogLabel.setText(HungerScaleHelper.getLabel(log.getScaleLevel()));

            // Apply scale color to the status indicator
            binding.viewLevelDot.setBackgroundColor(
                    HungerScaleHelper.getColour(log.getScaleLevel()));

            // Conditional visibility for user notes
            if (log.getNote() != null && !log.getNote().isEmpty()) {
                binding.textLogNote.setVisibility(android.view.View.VISIBLE);
                binding.textLogNote.setText(log.getNote());
            } else {
                binding.textLogNote.setVisibility(android.view.View.GONE);
            }
        }
    }
}