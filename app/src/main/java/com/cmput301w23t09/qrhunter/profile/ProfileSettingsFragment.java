package com.cmput301w23t09.qrhunter.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.util.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import java.util.UUID;

/** Fragment to display and edit contact details */
public class ProfileSettingsFragment extends BaseFragment {
  private ProfileSettingsController controller;

  private TextInputEditText phoneField;
  private TextInputEditText emailField;

  private String currentSavedEmail;
  private String currentSavedPhoneNo;
  private final UUID deviceUUID;

  public ProfileSettingsFragment(GameController gameController, UUID deviceUUID) {
    super(gameController);
    this.deviceUUID = deviceUUID;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    controller = new ProfileSettingsController(getGameController(), deviceUUID);
    View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

    phoneField = view.findViewById(R.id.settings_screen_phoneTextField);
    emailField = view.findViewById(R.id.settings_screen_emailTextField);

    view.findViewById(R.id.settings_back_button).setOnClickListener(v -> this.onBackClick());
    view.findViewById(R.id.settings_reset_details_button)
        .setOnClickListener(v -> this.onResetClick());
    view.findViewById(R.id.settings_save_button).setOnClickListener(v -> this.onSaveClick());

    loadPlayerContactDetails();

    return view;
  }

  /** Handles logic when the back button is clicked. */
  private void onBackClick() {
    boolean dataLoaded = currentSavedEmail != null && currentSavedPhoneNo != null;
    boolean changesMade =
        dataLoaded
            && (!phoneField.getText().toString().equals(currentSavedPhoneNo)
                || !emailField.getText().toString().equals(currentSavedEmail));

    if (!changesMade) {
      controller.returnToProfile();
      return;
    }

    // Show confirm dialog
    View view =
        getLayoutInflater()
            .inflate(R.layout.fragment_unsaved_changes_prompt, (ViewGroup) getView(), false);
    new AlertDialog.Builder(getContext())
        .setView(view)
        .setPositiveButton("Leave", (dia, l) -> controller.returnToProfile())
        .setNegativeButton("Go Back", (dia, l) -> {})
        .show();
  }

  /** Handles logic when the reset button is clicked. */
  private void onResetClick() {
    phoneField.setText("");
    emailField.setText("");
  }

  /** Handles logic when the save button is clicked. */
  private void onSaveClick() {
    String phoneNo = phoneField.getText().toString();
    String email = emailField.getText().toString();

    if (!ValidationUtils.isValidPhoneNo(phoneNo)) {
      Toast.makeText(getContext(), "That is not a valid phone number", Toast.LENGTH_SHORT).show();
      return;
    }
    if (!ValidationUtils.isValidEmail(email)) {
      Toast.makeText(getContext(), "That is not a valid email", Toast.LENGTH_SHORT).show();
      return;
    }

    controller.saveChanges(
        email,
        phoneNo,
        success -> {
          if (success) {
            currentSavedEmail = email;
            currentSavedPhoneNo = phoneNo;

            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(
                    getContext(),
                    "An exception occurred while trying to save your changes.",
                    Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  /** Load the player contact details. */
  private void loadPlayerContactDetails() {
    controller.requestPlayerData(
        player -> {
          if (player == null) {
            Toast.makeText(
                    getContext(),
                    "An exception occurred while fetching your data.",
                    Toast.LENGTH_SHORT)
                .show();
            return;
          }

          currentSavedPhoneNo = player.getPhoneNo();
          phoneField.setText(player.getPhoneNo());

          currentSavedEmail = player.getEmail();
          emailField.setText(player.getEmail());
        });
  }
}
