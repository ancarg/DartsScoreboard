package com.example.dartsscoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dartsscoreboard.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );

        displayGameSummary();
    }

    private void displayGameSummary() {
        if (getArguments() != null) {
            List<String> playerNames = getArguments().getStringArrayList("playerNames");
            int firstTo = getArguments().getInt("firstTo");
            int legLength = getArguments().getInt("legLength");

            StringBuilder summary = new StringBuilder();
            summary.append("Players (").append(playerNames != null ? playerNames.size() : 0).append("):\n");
            if (playerNames != null) {
                for (String name : playerNames) {
                    summary.append("- ").append(name).append("\n");
                }
            }
            summary.append("\nFirst to: ").append(firstTo).append(" legs");
            summary.append("\nLeg length: ").append(legLength);

            binding.textviewGameSummary.setText(summary.toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}