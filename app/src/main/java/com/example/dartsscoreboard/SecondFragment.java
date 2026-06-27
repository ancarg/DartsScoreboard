package com.example.dartsscoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dartsscoreboard.databinding.FragmentSecondBinding;
import com.example.dartsscoreboard.model.Leg;
import com.example.dartsscoreboard.model.Match;
import com.example.dartsscoreboard.model.Player;
import com.example.dartsscoreboard.model.PlayerLeg;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private Match crtMatch;
    private int currentPlayerIndex = 0;
    private Leg currentLeg;

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

        crtMatch = initAppModel();
        if (crtMatch != null && !crtMatch.getLegsList().isEmpty()) {
            currentLeg = crtMatch.getLegsList().get(0);
            updateUI();
            createPlayerButtons();
        }

        binding.buttonSubmit.setOnClickListener(v -> handleSubmit());
    }

    private Match initAppModel() {
        if (getArguments() != null) {
            List<String> playerNames = getArguments().getStringArrayList("playerNames");
            int firstTo = getArguments().getInt("firstTo");
            int legSize = getArguments().getInt("legLength");

            Match match = new Match(firstTo * 2 - 1, legSize);
            ArrayList<Player> players = new ArrayList<>();

            Leg firstLeg = new Leg(match.getId(), 1);
            match.getLegsList().add(firstLeg);

            if (playerNames != null) {
                for (String name : playerNames) {
                    Player player = new Player(match.getId(), name);
                    player.setPlayerLegsList(new ArrayList<>());
                    
                    PlayerLeg pl = new PlayerLeg(match.getId(), player.getId(), firstLeg.getId(), legSize);
                    player.getPlayerLegsList().add(pl);
                    
                    players.add(player);
                }
            }
            match.setPlayersList(players);

            return match;
        }
        return null;
    }

    private void updateUI() {
        if (crtMatch == null || currentLeg == null) return;

        Player currentPlayer = crtMatch.getPlayersList().get(currentPlayerIndex);
        
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
        }
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

    private void handleSubmit() {
        String throwValueStr = binding.edittextThrow.getText().toString();
        if (throwValueStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a throw value", Toast.LENGTH_SHORT).show();
            return;
        }

        int throwValue = Integer.parseInt(throwValueStr);
        int multiplier = 1;
        
        int checkedId = binding.toggleGroupThrowType.getCheckedButtonId();
        if (checkedId == R.id.button_double) {
            multiplier = 2;
        } else if (checkedId == R.id.button_triple) {
            multiplier = 3;
        }

        int totalScore = throwValue * multiplier;
        
        Player currentPlayer = crtMatch.getPlayersList().get(currentPlayerIndex);
        PlayerLeg pl = getCurrentPlayerLeg(currentPlayer, currentLeg);
        
        if (pl != null) {
            int newScore = pl.getCurrentScore() - totalScore;
            if (newScore >= 0) {
                pl.setCurrentScore(newScore);
                if (newScore == 0) {
                    Toast.makeText(getContext(), currentPlayer.getDisplayName() + " won the leg!", Toast.LENGTH_LONG).show();
                    currentPlayer.setWonLegsNo(currentPlayer.getWonLegsNo() + 1);
                }
            } else {
                Toast.makeText(getContext(), "Bust!", Toast.LENGTH_SHORT).show();
            }
        }

        binding.edittextThrow.setText("");
        binding.toggleGroupThrowType.clearChecked();
        
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % crtMatch.getPlayersList().size();
        updateUI();
    }

    private void createPlayerButtons() {
        binding.playersButtonsContainer.removeAllViews();
        for (int i = 0; i < crtMatch.getPlayersList().size(); i++) {
            Player player = crtMatch.getPlayersList().get(i);
            
            // Create button with Material Components style
            Button playerButton = new com.google.android.material.button.MaterialButton(
                    getContext(),
                    null,
                    com.google.android.material.R.attr.materialButtonStyle
            );
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            playerButton.setLayoutParams(params);
            playerButton.setText(player.getDisplayName());
            
            final int index = i;
            playerButton.setOnClickListener(v -> {
                currentPlayerIndex = index;
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
