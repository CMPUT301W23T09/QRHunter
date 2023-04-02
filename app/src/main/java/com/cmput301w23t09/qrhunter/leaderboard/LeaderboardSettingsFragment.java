package com.cmput301w23t09.qrhunter.leaderboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;

/** The leaderboard filter settings fragment */
public class LeaderboardSettingsFragment extends DialogFragment {

  private final LeaderboardController controller;
  private final LeaderboardFragment fragment;

  public LeaderboardSettingsFragment(
      LeaderboardFragment fragment, LeaderboardController controller) {
    this.fragment = fragment;
    this.controller = controller;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = getLayoutInflater().inflate(R.layout.fragment_leaderboard_settings, null);

    // Load the saved followed filter status
    CheckBox checkBox = view.findViewById(R.id.leaderboard_settings_filter_by_followed);
    checkBox.setChecked(controller.isFilteredByFollowedPlayers());
    checkBox.setOnCheckedChangeListener(
        (buttonView, isChecked) -> controller.setIsFilteredByFollowedPlayers(isChecked));

    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
    Dialog dialog =
        builder
            .setView(view)
            .setPositiveButton("Close", (ignored, ignored2) -> this.dismiss())
            .create();
    dialog.getWindow().setGravity(Gravity.CENTER);
    return dialog;
  }
}
