package com.example.dartsscoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.dartsscoreboard.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

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

        binding.buttonPlayer1.setOnClickListener(v -> showEditPlayerDialog(binding.buttonPlayer1));
        binding.buttonPlayer2.setOnClickListener(v -> showEditPlayerDialog(binding.buttonPlayer2));
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
            targetButton.setText(""); // Or some default
            dialog.dismiss();
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
    }

}
