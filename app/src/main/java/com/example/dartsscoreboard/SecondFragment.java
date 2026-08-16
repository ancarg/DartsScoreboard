package com.example.dartsscoreboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dartsscoreboard.databinding.FragmentSecondBinding;
import com.example.dartsscoreboard.model.Leg;
import com.example.dartsscoreboard.model.Match;
import com.example.dartsscoreboard.model.Player;
import com.example.dartsscoreboard.model.PlayerLeg;
import com.example.dartsscoreboard.model.Round;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private Match currentMatch;
    private int selectedPlayerIndex = 0;
    private int actualTurnPlayerIndex = 0;
    private Leg currentLeg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitWarningDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

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

        currentMatch = initMatchModel();
        if (currentMatch != null && !currentMatch.getLegsList().isEmpty()) {
            currentLeg = currentMatch.getLegsList().get(0);
            updateCurrentPlayerState();
            updateUI();
            createPlayerButtons();
        }

        //add event handlers
        setupInputListeners();

        //handle back action from header button
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // No extra menu items needed
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    showExitWarningDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupInputListeners() {
        TextWatcher throwWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateThrows();
                calculateAndDisplayTotal();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        binding.edittextThrow1.addTextChangedListener(throwWatcher);
        binding.edittextThrow2.addTextChangedListener(throwWatcher);
        binding.edittextThrow3.addTextChangedListener(throwWatcher);

        MaterialButtonToggleGroup.OnButtonCheckedListener toggleListener = (group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.button_strike_1) {
                    binding.edittextThrow1.setText("0");
                } else if (checkedId == R.id.button_strike_2) {
                    binding.edittextThrow2.setText("0");
                } else if (checkedId == R.id.button_strike_3) {
                    binding.edittextThrow3.setText("0");
                }
            }
            calculateAndDisplayTotal();
        };

        binding.toggleGroupThrowType1.addOnButtonCheckedListener(toggleListener);
        binding.toggleGroupThrowType2.addOnButtonCheckedListener(toggleListener);
        binding.toggleGroupThrowType3.addOnButtonCheckedListener(toggleListener);

        binding.buttonSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void calculateAndDisplayTotal() {
        int total = 0;
        total += getValidatedThrowScore(binding.edittextThrow1, binding.toggleGroupThrowType1);
        total += getValidatedThrowScore(binding.edittextThrow2, binding.toggleGroupThrowType2);
        total += getValidatedThrowScore(binding.edittextThrow3, binding.toggleGroupThrowType3);
        
        binding.textviewRoundTotalScore.setText(String.valueOf(total));
    }

    private int getValidatedThrowScore(TextInputEditText editText, MaterialButtonToggleGroup toggleGroup) {
        String valueStr = editText.getText().toString();
        if (valueStr.isEmpty()) return 0;
        try {
            int value = Integer.parseInt(valueStr);
            if (value > 20) return 0;
            
            int multiplier = 1;
            int checkedId = toggleGroup.getCheckedButtonId();
            if (checkedId == R.id.button_double_1 || checkedId == R.id.button_double_2 || checkedId == R.id.button_double_3) {
                multiplier = 2;
            } else if (checkedId == R.id.button_triple_1 || checkedId == R.id.button_triple_2 || checkedId == R.id.button_triple_3) {
                multiplier = 3;
            } else if (checkedId == R.id.button_strike_1 || checkedId == R.id.button_strike_2 || checkedId == R.id.button_strike_3) {
                multiplier = 0;
            }
            return value * multiplier;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean validateThrows() {
        boolean isValid = true;
        isValid &= validateSingleThrow(binding.edittextThrow1, binding.layoutThrow1);
        isValid &= validateSingleThrow(binding.edittextThrow2, binding.layoutThrow2);
        isValid &= validateSingleThrow(binding.edittextThrow3, binding.layoutThrow3);
        return isValid;
    }

    private boolean validateSingleThrow(TextInputEditText editText, com.google.android.material.textfield.TextInputLayout layout) {
        String val = editText.getText().toString();
        if (val.isEmpty()) {
            layout.setError(null);
            return true;
        }
        try {
            int score = Integer.parseInt(val);
            if (score > 20) {
                layout.setError("Max 20");
                return false;
            }
            layout.setError(null);
            return true;
        } catch (NumberFormatException e) {
            layout.setError("Invalid");
            return false;
        }
    }

    //back confirmation message
    private void showExitWarningDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.exit_warning_title)
                .setMessage(R.string.exit_warning_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private Match initMatchModel() {
        if (getArguments() != null) {
            List<String> playerNames = getArguments().getStringArrayList("playerNames");
            int firstTo = getArguments().getInt("firstTo");
            int legSize = getArguments().getInt("legLength");

            Match match = new Match(firstTo * 2 - 1, legSize);
            Leg firstLeg = new Leg(match.getId(), 1);
            match.getLegsList().add(firstLeg);

            ArrayList<Player> players = new ArrayList<>();
            if (playerNames != null) {
                for (String name : playerNames) {
                    Player player = new Player(match.getId(), name);
                    player.setPlayerLegsList(new ArrayList<>());
                    
                    PlayerLeg pl = new PlayerLeg(match.getId(), player.getId(), firstLeg.getId(), legSize);
                    pl.setRoundsList(new ArrayList<>());
                    player.getPlayerLegsList().add(pl);
                    
                    players.add(player);
                }
            }
            match.setPlayersList(players);

            return match;
        }
        return null;
    }

    private void handleSubmit() {
        if (!validateThrows()) {
            Toast.makeText(getContext(), "Please fix errors", Toast.LENGTH_SHORT).show();
            return;
        }

        Player currentPlayer = currentMatch.getPlayersList().get(actualTurnPlayerIndex);
        PlayerLeg pl = getCurrentPlayerLeg(currentPlayer, currentLeg);

        if (pl == null) return;

        int totalRoundScore = 0;

        totalRoundScore += getValidatedThrowScore(binding.edittextThrow1, binding.toggleGroupThrowType1);
        totalRoundScore += getValidatedThrowScore(binding.edittextThrow2, binding.toggleGroupThrowType2);
        totalRoundScore += getValidatedThrowScore(binding.edittextThrow3, binding.toggleGroupThrowType3);

        Round round = new Round(currentLeg.getId());
        round.setThrowsTotalScore(String.valueOf(totalRoundScore));
        if (pl.getRoundsList() == null) pl.setRoundsList(new ArrayList<>());
        pl.getRoundsList().add(round);

        int newScore = pl.getCurrentScore() - totalRoundScore;
        if (newScore >= 0) {
            pl.setCurrentScore(newScore);
            if (newScore == 0) {
                Toast.makeText(getContext(), currentPlayer.getDisplayName() + " won the leg!", Toast.LENGTH_LONG).show();
                currentPlayer.setWonLegsNo(currentPlayer.getWonLegsNo() + 1);
                startNewLeg();
                return; // startNewLeg handles UI refresh and player resets
            }
        } else {
            Toast.makeText(getContext(), "Bust!", Toast.LENGTH_SHORT).show();
        }

        clearInputs();

        // Move to next player
        actualTurnPlayerIndex = (actualTurnPlayerIndex + 1) % currentMatch.getPlayersList().size();
        selectedPlayerIndex = actualTurnPlayerIndex;
        updateCurrentPlayerState();
        updateUI();
    }

    private void startNewLeg() {
        int nextLegNo = currentMatch.getLegsList().size() + 1;
        currentLeg = new Leg(currentMatch.getId(), nextLegNo);
        currentMatch.getLegsList().add(currentLeg);

        for (Player player : currentMatch.getPlayersList()) {
            PlayerLeg pl = new PlayerLeg(currentMatch.getId(), player.getId(), currentLeg.getId(), currentMatch.getLegSize());
            pl.setRoundsList(new ArrayList<>());
            player.getPlayerLegsList().add(pl);
        }

        actualTurnPlayerIndex = 0;
        selectedPlayerIndex = 0;
        clearInputs();
        updateCurrentPlayerState();
        updateUI();
    }

    //----------------------------------------------------------------------
    private void updateCurrentPlayerState() {
        for (int i = 0; i < currentMatch.getPlayersList().size(); i++) {
            Player p = currentMatch.getPlayersList().get(i);
            PlayerLeg pl = getCurrentPlayerLeg(p, currentLeg);
            if (pl != null) {
                pl.setCurrentPlayer(i == actualTurnPlayerIndex);
            }
        }
    }

    private void updateUI() {
        if (currentMatch == null || currentLeg == null) return;

        Player selectedPlayer = currentMatch.getPlayersList().get(selectedPlayerIndex);
        
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.leg_label, currentLeg.getDisplayNo()));
            }
        }

        binding.textviewPlayerName.setText(getString(R.string.player_display_name, selectedPlayer.getDisplayName()));
        binding.textviewPlayerWins.setText(getString(R.string.wins_label, selectedPlayer.getWonLegsNo()));
        
        PlayerLeg selectedPL = getCurrentPlayerLeg(selectedPlayer, currentLeg);
        if (selectedPL != null) {
            binding.textviewCurrentScore.setText(String.valueOf(selectedPL.getCurrentScore()));
        }
        
        // Reset round total score display for the current user's new turn
        binding.textviewRoundTotalScore.setText("0");
        
        // Enable/Disable inputs based on whether the selected player is the one whose turn it is
        boolean isCurrentTurn = (selectedPlayerIndex == actualTurnPlayerIndex);
        setInputsEnabled(isCurrentTurn);
        
        // Refresh player buttons to update icons
        createPlayerButtons();
    }

    private void setInputsEnabled(boolean enabled) {
        binding.edittextThrow1.setEnabled(enabled);
        binding.edittextThrow2.setEnabled(enabled);
        binding.edittextThrow3.setEnabled(enabled);
        binding.toggleGroupThrowType1.setEnabled(enabled);
        binding.toggleGroupThrowType2.setEnabled(enabled);
        binding.toggleGroupThrowType3.setEnabled(enabled);
        binding.buttonSubmit.setEnabled(enabled);
    }

    private PlayerLeg getCurrentPlayerLeg(Player player, Leg leg) {
        if (player.getPlayerLegsList() == null) return null;
        for (PlayerLeg pl : player.getPlayerLegsList()) {
            if (pl.getLegId().equals(leg.getId())) {
                return pl;
            }
        }
        return null;
    }



    private void clearInputs() {
        binding.edittextThrow1.setText("");
        binding.edittextThrow2.setText("");
        binding.edittextThrow3.setText("");
        binding.toggleGroupThrowType1.clearChecked();
        binding.toggleGroupThrowType2.clearChecked();
        binding.toggleGroupThrowType3.clearChecked();
        binding.textviewRoundTotalScore.setText("0");
        
        binding.layoutThrow1.setError(null);
        binding.layoutThrow2.setError(null);
        binding.layoutThrow3.setError(null);
    }

    private void createPlayerButtons() {
        binding.playersButtonsContainer.removeAllViews();
        List<Player> players = currentMatch.getPlayersList();
        LinearLayout currentRow = null;

        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) {
                currentRow = new LinearLayout(getContext());
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                currentRow.setLayoutParams(rowParams);
                binding.playersButtonsContainer.addView(currentRow);
            }

            Player player = players.get(i);
            MaterialButton playerButton = new MaterialButton(
                    getContext(),
                    null,
                    com.google.android.material.R.attr.materialButtonStyle
            );

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            params.setMargins(4, 4, 4, 4);
            playerButton.setLayoutParams(params);

            PlayerLeg pl = getCurrentPlayerLeg(player, currentLeg);
            String buttonText = player.getDisplayName();
            if (pl != null) {
                buttonText += " (" + pl.getCurrentScore() + ")";
            }
            playerButton.setText(buttonText);

            if (pl != null && pl.isCurrentPlayer()) {
                playerButton.setIconResource(R.drawable.ic_current_player_dart);
                playerButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            } else {
                playerButton.setIcon(null);
            }

            final int index = i;
            playerButton.setOnClickListener(v -> {
                selectedPlayerIndex = index;
                updateUI();
            });

            if (currentRow != null) {
                currentRow.addView(playerButton);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
