package com.zeez.nourishquest.ui.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zeez.nourishquest.R;

// Bottom sheet showing UK helplines.
// Appears when the user taps "Need to talk to someone?" on the home screen.
// Tapping a number opens the dialler, tapping a link opens the browser.
public class SupportBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Beat — eating disorder specific
        view.findViewById(R.id.btn_beat_call).setOnClickListener(v -> dial("08088010677"));
        view.findViewById(R.id.btn_beat_web).setOnClickListener(v -> openUrl("https://www.beateatingdisorders.org.uk"));

        // Samaritans — 24/7 emotional support
        view.findViewById(R.id.btn_samaritans_call).setOnClickListener(v -> dial("116123"));
        view.findViewById(R.id.btn_samaritans_web).setOnClickListener(v -> openUrl("https://www.samaritans.org"));

        // Mind — mental health support
        view.findViewById(R.id.btn_mind_call).setOnClickListener(v -> dial("03001233393"));
        view.findViewById(R.id.btn_mind_web).setOnClickListener(v -> openUrl("https://www.mind.org.uk"));

        // SANE — mental health crisis line
        view.findViewById(R.id.btn_sane_call).setOnClickListener(v -> dial("03003047000"));
        view.findViewById(R.id.btn_sane_web).setOnClickListener(v -> openUrl("https://www.sane.org.uk"));

        // Close button
        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
    }

    // Opens the phone dialler with the number pre-filled
    private void dial(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireContext().startActivity(intent);
        } catch (Exception e) {
            // No dialler available — fail silently
        }
    }

    // Opens a URL in the default browser
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireContext().startActivity(intent);
        } catch (Exception e) {
            // No browser available — fail silently
        }
    }
}