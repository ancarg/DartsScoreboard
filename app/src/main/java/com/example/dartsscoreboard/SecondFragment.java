package com.example.dartsscoreboard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
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
import com.example.dartsscoreboard.model.Set;
import com.google.android.gms.ads.AdRequest;
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
    private Set currentSet;
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
        if (currentMatch != null && !currentMatch.getSetsList().isEmpty()) {
            currentSet = currentMatch.getSetsList().get(0);
            currentLeg = currentSet.getLegsList().get(0);
            updateUI();
        }

        //add event handlers
        setupInputListeners();

        // AdRequest adRequest = new AdRequest.Builder().build();
        // binding.adView.loadAd(adRequest);

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
            boolean isCurrentTurn = (selectedPlayerIndex == actualTurnPlayerIndex);
            if (isCurrentTurn) {
                if (checkedId == R.id.button_strike_1) {
                    if (isChecked) {
                        binding.edittextThrow1.setText("0");
                        binding.edittextThrow1.setEnabled(false);
                    } else {
                        binding.edittextThrow1.setText("");
                        binding.edittextThrow1.setEnabled(true);
                    }
                } else if (checkedId == R.id.button_strike_2) {
                    if (isChecked) {
                        binding.edittextThrow2.setText("0");
                        binding.edittextThrow2.setEnabled(false);
                    } else {
                        binding.edittextThrow2.setText("");
                        binding.edittextThrow2.setEnabled(true);
                    }
                } else if (checkedId == R.id.button_strike_3) {
                    if (isChecked) {
                        binding.edittextThrow3.setText("0");
                        binding.edittextThrow3.setEnabled(false);
                    } else {
                        binding.edittextThrow3.setText("");
                        binding.edittextThrow3.setEnabled(true);
                    }
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
            int setsToWinMatch = getArguments().getInt("setsToWinMatch");
            int legsToWinSet = getArguments().getInt("legsToWinSet");
            int legSize = getArguments().getInt("legLength");

            Match match = new Match(setsToWinMatch, legsToWinSet, legSize);
            Set firstSet = new Set(match.getId(), 1);
            Leg firstLeg = new Leg(match.getId(), firstSet.getId(), 1);
            firstSet.getLegsList().add(firstLeg);
            match.getSetsList().add(firstSet);

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

    private boolean isAnyThrowEmpty() {
        return binding.edittextThrow1.getText().toString().trim().isEmpty() ||
                binding.edittextThrow2.getText().toString().trim().isEmpty() ||
                binding.edittextThrow3.getText().toString().trim().isEmpty();
    }

    private void handleSubmit() {
        if (isAnyThrowEmpty()) {
            Toast.makeText(getContext(), R.string.throws_remaining, Toast.LENGTH_SHORT).show();
            return;
        }

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
                currentLeg.setWinnerId(currentPlayer.getId());
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
        updateUI();
    }

    private void startNewLeg() {
        // Check if current set is finished
        int winnerLegsCount = 0;
        String lastWinnerId = currentLeg.getWinnerId();
        for (Leg leg : currentSet.getLegsList()) {
            if (lastWinnerId != null && lastWinnerId.equals(leg.getWinnerId())) {
                winnerLegsCount++;
            }
        }

        if (winnerLegsCount >= currentMatch.getLegsNo()) {
            // Player won the set
            for (Player p : currentMatch.getPlayersList()) {
                if (p.getId().equals(lastWinnerId)) {
                    p.setWonSetsNo(p.getWonSetsNo() + 1);
                    Toast.makeText(getContext(), p.getDisplayName() + " won the set!", Toast.LENGTH_LONG).show();
                }
                // Reset legs won in the set for all players
                p.setWonLegsNo(0);
            }
            // Start new set
            int nextSetNo = currentMatch.getSetsList().size() + 1;
            currentSet = new Set(currentMatch.getId(), nextSetNo);
            currentMatch.getSetsList().add(currentSet);
        }

        // Calculate cumulative leg number for display
        int totalLegsCount = 0;
        for (Set s : currentMatch.getSetsList()) {
            totalLegsCount += s.getLegsList().size();
        }
        int nextLegDisplayNo = totalLegsCount + 1;

        currentLeg = new Leg(currentMatch.getId(), currentSet.getId(), nextLegDisplayNo);
        currentSet.getLegsList().add(currentLeg);

        for (Player player : currentMatch.getPlayersList()) {
            PlayerLeg pl = new PlayerLeg(currentMatch.getId(), player.getId(), currentLeg.getId(), currentMatch.getLegSize());
            pl.setRoundsList(new ArrayList<>());
            player.getPlayerLegsList().add(pl);
        }

        actualTurnPlayerIndex = (nextLegDisplayNo - 1) % currentMatch.getPlayersList().size();
        selectedPlayerIndex = actualTurnPlayerIndex;
        clearInputs();
        updateUI();
    }

    private void updateUI() {
        if (currentMatch == null || currentLeg == null) return;

        Player selectedPlayer = currentMatch.getPlayersList().get(selectedPlayerIndex);
        
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.set_leg_label, currentSet.getDisplayNo(), currentLeg.getDisplayNo()));
            }
        }

        Player activePlayer = currentMatch.getPlayersList().get(actualTurnPlayerIndex);
        binding.textviewActivePlayerName.setText(activePlayer.getDisplayName());

        PlayerLeg selectedPL = getCurrentPlayerLeg(selectedPlayer, currentLeg);
        if (selectedPL != null) {
            binding.textviewCurrentScore.setText(String.valueOf(selectedPL.getCurrentScore()));
        }
        
        // Reset round total score display for the current user's new turn
        binding.textviewRoundTotalScore.setText("0");
        
        // Enable/Disable inputs based on whether the selected player is the one whose turn it is
        boolean isCurrentTurn = (selectedPlayerIndex == actualTurnPlayerIndex);
        setInputsEnabled(isCurrentTurn);
        
        if (isCurrentTurn) {
            binding.edittextThrow1.requestFocus();
        }
        
        updatePlayerTable();
    }

    private void setInputsEnabled(boolean enabled) {
        binding.edittextThrow1.setEnabled(enabled && binding.toggleGroupThrowType1.getCheckedButtonId() != R.id.button_strike_1);
        binding.edittextThrow2.setEnabled(enabled && binding.toggleGroupThrowType2.getCheckedButtonId() != R.id.button_strike_2);
        binding.edittextThrow3.setEnabled(enabled && binding.toggleGroupThrowType3.getCheckedButtonId() != R.id.button_strike_3);
        
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
        binding.edittextThrow1.setEnabled(true);
        binding.edittextThrow2.setEnabled(true);
        binding.edittextThrow3.setEnabled(true);

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

    private void updatePlayerTable() {
        if (currentMatch == null || currentLeg == null) return;

        // Update header with sets to win
        binding.headerPlayer.setText(getString(R.string.first_to_sets_label, currentMatch.getSetsNo()));

        // Remove all rows except the header
        int childCount = binding.tablePlayersScore.getChildCount();
        if (childCount > 1) {
            binding.tablePlayersScore.removeViews(1, childCount - 1);
        }

        List<Player> players = currentMatch.getPlayersList();
        int startingPlayerIndex = (currentLeg.getDisplayNo() - 1) % players.size();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerLeg pl = getCurrentPlayerLeg(player, currentLeg);

            TableRow row = new TableRow(getContext());
            row.setPadding(0, 8, 0, 8);
            
            // Highlight current turn player or selected player
            if (i == actualTurnPlayerIndex) {
                row.setBackgroundResource(R.color.highlight_turn);
            } else if (i == selectedPlayerIndex) {
                 row.setBackgroundResource(R.color.highlight_selected);
            }

            final int index = i;
            row.setOnClickListener(v -> {
                selectedPlayerIndex = index;
                updateUI();
            });

            // Column 1: Player Name + Dart Icon if starter
            TextView nameTxt = new TextView(getContext());
            nameTxt.setText(player.getDisplayName());
            nameTxt.setPadding(8, 8, 8, 8);
            if (i == startingPlayerIndex) {
                nameTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_current_player_dart, 0, 0, 0);
                nameTxt.setCompoundDrawablePadding(8);
            }
            row.addView(nameTxt);

            // Column 2: Sets
            TextView setsTxt = new TextView(getContext());
            setsTxt.setText(String.valueOf(player.getWonSetsNo()));
            setsTxt.setGravity(Gravity.CENTER);
            setsTxt.setPadding(8, 8, 8, 8);
            row.addView(setsTxt);

            // Column 3: Legs
            TextView legsTxt = new TextView(getContext());
            legsTxt.setText(String.valueOf(player.getWonLegsNo()));
            legsTxt.setGravity(Gravity.CENTER);
            legsTxt.setPadding(8, 8, 8, 8);
            row.addView(legsTxt);

            // Column 4: Score
            TextView scoreTxt = new TextView(getContext());
            int score = (pl != null) ? pl.getCurrentScore() : currentMatch.getLegSize();
            scoreTxt.setText(String.valueOf(score));
            scoreTxt.setGravity(Gravity.CENTER);
            scoreTxt.setPadding(8, 8, 8, 8);
            scoreTxt.setTextSize(18);
            scoreTxt.setTypeface(null, Typeface.BOLD);
            row.addView(scoreTxt);

            binding.tablePlayersScore.addView(row);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
