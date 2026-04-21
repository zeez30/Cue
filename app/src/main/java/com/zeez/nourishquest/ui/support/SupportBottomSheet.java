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

        // Beat: Eating disorder support
        view.findViewById(R.id.btn_beat_call).setOnClickListener(v -> dial("08088010677"));
        view.findViewById(R.id.btn_beat_web).setOnClickListener(v -> openUrl("https://www.beateatingdisorders.org.uk"));

        // Samaritans: 24/7 support
        view.findViewById(R.id.btn_samaritans_call).setOnClickListener(v -> dial("116123"));
        view.findViewById(R.id.btn_samaritans_web).setOnClickListener(v -> openUrl("https://www.samaritans.org"));

        // Mind: Mental health information
        view.findViewById(R.id.btn_mind_call).setOnClickListener(v -> dial("03001233393"));
        view.findViewById(R.id.btn_mind_web).setOnClickListener(v -> openUrl("https://www.mind.org.uk"));

        // SANE: Crisis support
        view.findViewById(R.id.btn_sane_call).setOnClickListener(v -> dial("03003047000"));
        view.findViewById(R.id.btn_sane_web).setOnClickListener(v -> openUrl("https://www.sane.org.uk"));

        // Modal dismissal
        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
    }

    /**
     * Triggers an implicit intent to open the system dialler.
     * Uses the tel: URI scheme for number population.
     */
    private void dial(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireContext().startActivity(intent);
        } catch (Exception e) {
            // Error handling for devices without telephony support
        }
    }

    /**
     * Triggers an implicit intent to open a web resource.
     * Delegates the request to the default system browser.
     */
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireContext().startActivity(intent);
        } catch (Exception e) {
            // Error handling for devices without a web browser
        }
    }
}