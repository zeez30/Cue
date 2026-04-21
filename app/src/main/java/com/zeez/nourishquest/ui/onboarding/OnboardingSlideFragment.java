package com.zeez.nourishquest.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeez.nourishquest.R;
import com.zeez.nourishquest.util.PrefsManager;

public class OnboardingSlideFragment extends Fragment {

    private static final String ARG_PAGE = "page";

    private static final String[] ICONS = { "◈", "◉", "◇", "✦", "▶" };

    private static final String[] TITLES = {
        "HEY, WELCOME",
        "WHAT THIS APP DOES",
        "THE HUNGER SCALE",
        "NO PRESSURE",
        "ALMOST THERE"
    };

    private static final String[] BODIES = {
        "Cue is built around Intuitive Eating - a way of eating "
        + "that ditches the diet rules and helps you reconnect with your "
        + "body's own signals.\n\n"
        + "No meal plans. No calorie counts. No guilt.",

        "This app tracks three things:\n\n"
        + "  ◈  How hungry or full you are\n"
        + "  ◈  How satisfying your meals are\n"
        + "  ◈  How your body feels day to day\n\n"
        + "It will never ask for:\n\n"
        + "  ✕  Calories or macros\n"
        + "  ✕  Your weight\n"
        + "  ✕  Whether a food was 'good' or 'bad'\n\n"
        + "Everything stays on your phone. Nothing goes anywhere.",

        "The hunger scale goes from 1 to 10.\n\n"
        + "1 means you're running on empty - headache, can't focus. "
        + "10 means you're so full it's uncomfortable.\n\n"
        + "The point isn't to hit a number - it's just to notice "
        + "where you are before and after you eat.\n\n"
        + "That's it. That's the whole thing.",

        "You don't have to use this every day.\n\n"
        + "The streak counter tracks days you checked in - not days "
        + "you ate 'correctly', because that's not a thing here.\n\n"
        + "Come back when it's useful. Skip it when life is busy. "
        + "There's no way to fall behind.",

        "Nearly done.\n\n"
        + "If you want, pop your name in below - it'll show up on the "
        + "home screen. Completely optional.\n\n"
        + "A daily check-in reminder has been set up for you. "
        + "You can change or turn it off anytime in your notification settings."
    };

    public static OnboardingSlideFragment newInstance(int page) {
        OnboardingSlideFragment fragment = new OnboardingSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding_slide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int page = getArguments() != null ? getArguments().getInt(ARG_PAGE, 0) : 0;

        ((TextView) view.findViewById(R.id.text_slide_icon)).setText(ICONS[page]);
        ((TextView) view.findViewById(R.id.text_slide_title)).setText(TITLES[page]);
        ((TextView) view.findViewById(R.id.text_slide_body)).setText(BODIES[page]);

        // Show the name input only on the last slide
        LinearLayout nameSection = view.findViewById(R.id.name_section);
        if (page == 4) {
            nameSection.setVisibility(View.VISIBLE);
        } else {
            nameSection.setVisibility(View.GONE);
        }
    }

    // Save the name when the last slide is navigated away from or the fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getArguments() != null && getArguments().getInt(ARG_PAGE) == 4) {
            View v = getView();
            if (v != null) {
                EditText nameField = v.findViewById(R.id.edit_player_name);
                if (nameField != null && nameField.getText() != null) {
                    String name = nameField.getText().toString().trim();
                    if (!name.isEmpty()) {
                        new PrefsManager(requireContext()).setPlayerName(name);
                    }
                }
            }
        }
    }
}
