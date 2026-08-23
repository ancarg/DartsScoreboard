package com.example.dartsscoreboard;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dartsscoreboard.databinding.FragmentWinnerBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class WinnerFragment extends Fragment {

    private FragmentWinnerBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToStart();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWinnerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            ArrayList<String> names = getArguments().getStringArrayList("playerNames");
            float[] averages = getArguments().getFloatArray("playerAverages");
            int[] sets = getArguments().getIntArray("playerSets");

            if (names != null && averages != null && sets != null) {
                int purpleColor = ContextCompat.getColor(requireContext(), R.color.winner_purple);
                int darkerGrayColor = ContextCompat.getColor(requireContext(), R.color.others_darker_gray);

                boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
                int strokeColor = isDarkMode ? getThemeColor(com.google.android.material.R.attr.colorOutline) : Color.BLACK;

                for (int i = 0; i < names.size(); i++) {
                    boolean isWinner = (i == 0);
                    int bgColor = isWinner ? purpleColor : darkerGrayColor;
                    int contentTextColor = isWinner ? Color.WHITE : Color.BLACK;
                    
                    addPlayerSummaryCard(names.get(i), averages[i], sets[i], bgColor, contentTextColor, strokeColor);
                }
            }
        }
    }

    private void addPlayerSummaryCard(String name, float average, int setsWon, int bgColor, int textColor, int strokeColor) {
        View view = getLayoutInflater().inflate(R.layout.item_player_summary, binding.layoutPlayersSummary, false);
        MaterialCardView cardView = (MaterialCardView) view;

        TextView nameTv = cardView.findViewById(R.id.textview_summary_player_name);
        TextView avgTv = cardView.findViewById(R.id.textview_summary_player_average);
        TextView setsTv = cardView.findViewById(R.id.textview_summary_player_sets);
        View setsLayout = cardView.findViewById(R.id.layout_sets);

        cardView.setCardBackgroundColor(ColorStateList.valueOf(bgColor));
        cardView.setStrokeColor(ColorStateList.valueOf(strokeColor));
        
        nameTv.setTextColor(textColor);
        avgTv.setTextColor(textColor);
        
        // Font of the value (won sets) should be the color of the card background
        setsTv.setTextColor(bgColor);
        
        // Sets box always has white background, with stroke matching the requirements
        if (setsLayout.getBackground() instanceof GradientDrawable) {
            GradientDrawable gd = (GradientDrawable) setsLayout.getBackground();
            gd.setColor(Color.WHITE);
            gd.setStroke((int) (1 * getResources().getDisplayMetrics().density), strokeColor);
        }

        nameTv.setText(name);
        avgTv.setText(getString(R.string.average_label, String.format(Locale.getDefault(), "%.2f", average)));
        setsTv.setText(String.valueOf(setsWon));

        binding.layoutPlayersSummary.addView(cardView);
    }

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        if (getContext() != null && getContext().getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return Color.GRAY;
    }

    private void navigateToStart() {
        NavHostFragment.findNavController(this).popBackStack(R.id.FirstFragment, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
