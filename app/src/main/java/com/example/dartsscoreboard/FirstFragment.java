package com.example.dartsscoreboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dartsscoreboard.databinding.FragmentFirstBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private final List<Integer> playerButtonIds = new ArrayList<>();
    private int playerSequence = 2; // Starts with 2 players
    private final int maxNoPlayers = 4; //maximum number of players
    private int defaultFirstTo = 8; //the default value for first to x legs config
    private final int[] legLength = new int[]  {301, 501}; //the length of a leg
    private int selectedLegLength = 501;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize with existing buttons
        playerButtonIds.add(binding.buttonPlayer1.getId());
        playerButtonIds.add(binding.buttonPlayer2.getId());

        binding.buttonPlayer1.setOnClickListener(v -> showEditPlayerDialog(binding.buttonPlayer1));
        binding.buttonPlayer2.setOnClickListener(v -> showEditPlayerDialog(binding.buttonPlayer2));

        binding.buttonAddPlayer.setOnClickListener(v -> addPlayer());

        binding.edittextFirstTo.setText(String.valueOf(defaultFirstTo));
        binding.edittextFirstTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().isEmpty()) {
                    try {
                        defaultFirstTo = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        // Handle invalid input if necessary
                    }
                }
            }
        });

        setupLegLengthDropdown();

        binding.buttonStart.setOnClickListener(v -> startGame());
    }

    private void setupLegLengthDropdown() {
        List<String> options = new ArrayList<>();
        for (int length : legLength) {
            options.add(String.valueOf(length));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, options);
        binding.dropdownLegLength.setAdapter(adapter);
        
        // Set default selection
        binding.dropdownLegLength.setText(String.valueOf(selectedLegLength), false);
        
        binding.dropdownLegLength.setOnItemClickListener((parent, view, position, id) -> {
            selectedLegLength = legLength[position];
        });
    }

    private void startGame() {
        List<String> playerNames = new ArrayList<>();
        // Get name from button_player_1 if visible
        if (binding.buttonPlayer1.getVisibility() == View.VISIBLE) {
            playerNames.add(binding.buttonPlayer1.getText().toString());
        }
        // Get name from button_player_2 if visible
        if (binding.buttonPlayer2.getVisibility() == View.VISIBLE) {
            playerNames.add(binding.buttonPlayer2.getText().toString());
        }
        
        // Get names from dynamically added buttons
        int childCount = binding.playerButtonsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = binding.playerButtonsContainer.getChildAt(i);
            if (child instanceof Button && child.getId() != binding.buttonPlayer1.getId() && child.getId() != binding.buttonPlayer2.getId()) {
                if (child.getVisibility() == View.VISIBLE) {
                    playerNames.add(((Button) child).getText().toString());
                }
            }
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("playerNames", new ArrayList<>(playerNames));
        bundle.putInt("firstTo", defaultFirstTo);
        bundle.putInt("legLength", selectedLegLength);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

    private void addPlayer() {
        if (playerButtonIds.size() >= this.maxNoPlayers) {
            Toast.makeText(requireContext(), R.string.max_players_reached, Toast.LENGTH_SHORT).show();
            return;
        }

        playerSequence++;
        MaterialButton newPlayerButton = new MaterialButton(requireContext());
        int newId = View.generateViewId();
        newPlayerButton.setId(newId);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        newPlayerButton.setLayoutParams(params);
        
        newPlayerButton.setText(getString(R.string.player_label, playerSequence));
        newPlayerButton.setOnClickListener(v -> showEditPlayerDialog(newPlayerButton));

        binding.playerButtonsContainer.addView(newPlayerButton);
        playerButtonIds.add(newId);
    }

    private void showEditPlayerDialog(Button targetButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_player, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.edittext_player_name);
        Button buttonRemove = dialogView.findViewById(R.id.button_remove_player);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonDone = dialogView.findViewById(R.id.button_done);

        editTextName.setText(targetButton.getText());

        AlertDialog dialog = builder.create();

        buttonRemove.setOnClickListener(v -> {
            if (playerButtonIds.size() <= 1) {
                Toast.makeText(requireContext(), R.string.cannot_remove_last_player, Toast.LENGTH_SHORT).show();
            } else {
                binding.playerButtonsContainer.removeView(targetButton);
                playerButtonIds.remove(Integer.valueOf(targetButton.getId()));
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonDone.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            if (!newName.isEmpty()) {
                targetButton.setText(newName);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        playerButtonIds.clear();
    }

}
