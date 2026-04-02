package com.zeez.nourishquest.ui.journal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zeez.nourishquest.data.entity.JournalEntry;
import com.zeez.nourishquest.databinding.ItemJournalEntryBinding;
import com.zeez.nourishquest.util.DateUtils;

// RecyclerView adapter for the journal history list.
// Shows a snippet of each entry — full text is truncated to 2 lines per field.
public class JournalHistoryAdapter extends ListAdapter<JournalEntry, JournalHistoryAdapter.ViewHolder> {

    public JournalHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<JournalEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<JournalEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull JournalEntry a, @NonNull JournalEntry b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull JournalEntry a, @NonNull JournalEntry b) {
                    return a.getTimestamp() == b.getTimestamp()
                            && a.getMood().equals(b.getMood());
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJournalEntryBinding binding = ItemJournalEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemJournalEntryBinding binding;

        ViewHolder(ItemJournalEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(JournalEntry entry) {
            binding.textEntryDate.setText(DateUtils.formatFull(entry.getTimestamp()));
            binding.textEntryMood.setText(moodEmoji(entry.getMood()) + " " + moodLabel(entry.getMood()));

            // Only show fields that were filled in
            showOrHide(binding.textBodyFeeling, "Body: ", entry.getBodyFeeling());
            showOrHide(binding.textChallengedThought, "Challenge: ", entry.getChallengedThought());
            showOrHide(binding.textBodyGratitude, "Grateful for: ", entry.getBodyGratitude());
        }

        // Shows a TextView with a prefix + content, or hides it if content is empty
        private void showOrHide(android.widget.TextView tv, String prefix, String content) {
            if (content != null && !content.isEmpty()) {
                tv.setVisibility(android.view.View.VISIBLE);
                tv.setText(prefix + content);
            } else {
                tv.setVisibility(android.view.View.GONE);
            }
        }

        private String moodEmoji(String mood) {
            if (mood == null) return "😐";
            switch (mood) {
                case "GREAT":     return "✨";
                case "GOOD":      return "😊";
                case "OKAY":      return "😐";
                case "DIFFICULT": return "😞";
                case "ROUGH":     return "🌧️";
                default:          return "😐";
            }
        }

        private String moodLabel(String mood) {
            if (mood == null) return "Okay";
            return mood.charAt(0) + mood.substring(1).toLowerCase();
        }
    }
}
