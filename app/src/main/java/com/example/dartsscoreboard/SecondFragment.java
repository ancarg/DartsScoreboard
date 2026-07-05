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
    private int currentPlayerIndex = 0;
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
                calculateAndDisplayTotal();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        binding.edittextThrow1.addTextChangedListener(throwWatcher);
        binding.edittextThrow2.addTextChangedListener(throwWatcher);
        binding.edittextThrow3.addTextChangedListener(throwWatcher);

        MaterialButtonToggleGroup.OnButtonCheckedListener toggleListener = (group, checkedId, isChecked) -> {
            calculateAndDisplayTotal();
        };

        binding.toggleGroupThrowType1.addOnButtonCheckedListener(toggleListener);
        binding.toggleGroupThrowType2.addOnButtonCheckedListener(toggleListener);
        binding.toggleGroupThrowType3.addOnButtonCheckedListener(toggleListener);

        binding.buttonSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void calculateAndDisplayTotal() {
        int total = 0;
        total += getThrowScore(binding.edittextThrow1, binding.toggleGroupThrowType1);
        total += getThrowScore(binding.edittextThrow2, binding.toggleGroupThrowType2);
        total += getThrowScore(binding.edittextThrow3, binding.toggleGroupThrowType3);
        
        binding.textviewRoundTotalScore.setText(String.valueOf(total));
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
        Player currentPlayer = currentMatch.getPlayersList().get(currentPlayerIndex);
        PlayerLeg pl = getCurrentPlayerLeg(currentPlayer, currentLeg);

        if (pl == null) return;

        int totalRoundScore = 0;

        totalRoundScore += getThrowScore(binding.edittextThrow1, binding.toggleGroupThrowType1);
        totalRoundScore += getThrowScore(binding.edittextThrow2, binding.toggleGroupThrowType2);
        totalRoundScore += getThrowScore(binding.edittextThrow3, binding.toggleGroupThrowType3);

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
            }
        } else {
            Toast.makeText(getContext(), "Bust!", Toast.LENGTH_SHORT).show();
        }

        clearInputs();

        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % currentMatch.getPlayersList().size();
        updateCurrentPlayerState();
        updateUI();
    }

    //----------------------------------------------------------------------
    private void updateCurrentPlayerState() {
        for (int i = 0; i < currentMatch.getPlayersList().size(); i++) {
            Player p = currentMatch.getPlayersList().get(i);
            PlayerLeg pl = getCurrentPlayerLeg(p, currentLeg);
            if (pl != null) {
                pl.setCurrentPlayer(i == currentPlayerIndex);
            }
        }
    }

    private void updateUI() {
        if (currentMatch == null || currentLeg == null) return;

        Player currentPlayer = currentMatch.getPlayersList().get(currentPlayerIndex);
        
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.leg_label, currentLeg.getDisplayNo()));
            }
        }

        binding.textviewPlayerName.setText(getString(R.string.player_display_name, currentPlayer.getDisplayName()));
        binding.textviewPlayerWins.setText(getString(R.string.wins_label, currentPlayer.getWonLegsNo()));
        
        PlayerLeg currentPL = getCurrentPlayerLeg(currentPlayer, currentLeg);
        if (currentPL != null) {
            binding.textviewCurrentScore.setText(String.valueOf(currentPL.getCurrentScore()));
            
            if (currentPL.getRoundsList() != null && !currentPL.getRoundsList().isEmpty()) {
                Round lastRound = currentPL.getRoundsList().get(currentPL.getRoundsList().size() - 1);
                binding.textviewRoundTotalScore.setText(lastRound.getThrowsTotalScore());
            } else {
                binding.textviewRoundTotalScore.setText("0");
            }
        }
        
        // Refresh player buttons to update icons
        createPlayerButtons();
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



    private int getThrowScore(TextInputEditText editText, MaterialButtonToggleGroup toggleGroup) {
        String valueStr = editText.getText().toString();
        if (valueStr.isEmpty()) return 0;

        int value = Integer.parseInt(valueStr);
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
    }

    private void clearInputs() {
        binding.edittextThrow1.setText("");
        binding.edittextThrow2.setText("");
        binding.edittextThrow3.setText("");
        binding.toggleGroupThrowType1.clearChecked();
        binding.toggleGroupThrowType2.clearChecked();
        binding.toggleGroupThrowType3.clearChecked();
        binding.textviewRoundTotalScore.setText("0");
    }

    private void createPlayerButtons() {
        binding.playersButtonsContainer.removeAllViews();
        for (int i = 0; i < currentMatch.getPlayersList().size(); i++) {
            Player player = currentMatch.getPlayersList().get(i);
            
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
            params.setMargins(4, 0, 4, 0);
            playerButton.setLayoutParams(params);
            playerButton.setText(player.getDisplayName());
            
            PlayerLeg pl = getCurrentPlayerLeg(player, currentLeg);
            if (pl != null && pl.isCurrentPlayer()) {
                playerButton.setIconResource(R.drawable.ic_current_player_dart);
                playerButton.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
            } else {
                playerButton.setIcon(null);
            }
            
            final int index = i;
            playerButton.setOnClickListener(v -> {
                currentPlayerIndex = index;
                updateCurrentPlayerState();
                updateUI();
            });
            
            binding.playersButtonsContainer.addView(playerButton);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
