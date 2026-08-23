package com.example.dartsscoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dartsscoreboard.databinding.FragmentWinnerBinding;

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
                for (int i = 0; i < names.size(); i++) {
                    addPlayerSummaryCard(names.get(i), averages[i], sets[i]);
                }
            }
        }
    }

    private void addPlayerSummaryCard(String name, float average, int setsWon) {
        View cardView = getLayoutInflater().inflate(R.layout.item_player_summary, binding.layoutPlayersSummary, false);

        TextView nameTv = cardView.findViewById(R.id.textview_summary_player_name);
        TextView avgTv = cardView.findViewById(R.id.textview_summary_player_average);
        TextView setsTv = cardView.findViewById(R.id.textview_summary_player_sets);

        nameTv.setText(name);
        avgTv.setText(getString(R.string.average_label, String.format(Locale.getDefault(), "%.2f", average)));
        setsTv.setText(String.valueOf(setsWon));

        binding.layoutPlayersSummary.addView(cardView);
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
