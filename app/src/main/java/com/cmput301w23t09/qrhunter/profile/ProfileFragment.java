package com.cmput301w23t09.qrhunter.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;

public class ProfileFragment extends Fragment {
  private final GameController gameController;
  private ProfileController controller;
  private TextView username;
  private TextView totalPoints;
  private TextView totalCodes;
  private TextView topCodeScore;
  private Spinner sortTypeSpinner;
  private Spinner sortOrderSpinner;
  private Button settingsButton;
  private GridView qrCodeList;

  public ProfileFragment(GameController gameController) {
    this.gameController = gameController;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.profile_activity, container, false);
    controller = new ProfileController(this);
    createProfile(view);
    return view;
  }

  private void createProfile(View view) {
    // get profile elements
    username = view.findViewById(R.id.username);
    qrCodeList = view.findViewById(R.id.code_list);
    totalPoints = view.findViewById(R.id.total_points);
    totalCodes = view.findViewById(R.id.total_codes);
    topCodeScore = view.findViewById(R.id.top_code_score);
    sortTypeSpinner = view.findViewById(R.id.sort_spinner);
    sortOrderSpinner = view.findViewById(R.id.order_spinner);
    settingsButton = view.findViewById(R.id.settings_btn);

    // create a default empty profile (shown while waiting for database queries)
    createDefaultProfile();

    // setup profile elements
    controller.setUpUsername(username);
    controller.setUpQRList(
        qrCodeList, totalPoints, totalCodes, topCodeScore, sortTypeSpinner, sortOrderSpinner);
  }

  private void createDefaultProfile() {
    username.setText("");
    totalPoints.setText(getString(R.string.total_points_txt, 0));
    totalCodes.setText(getString(R.string.total_codes_txt, 0));
    topCodeScore.setText(getString(R.string.top_code_txt, 0));
    createSpinner(sortTypeSpinner, R.array.sort_options);
    createSpinner(sortOrderSpinner, R.array.order_options);

    settingsButton.setOnClickListener(v -> controller.handeSettingsClick());
  }

  private void createSpinner(Spinner spinner, @ArrayRes int spinnerOptionsResource) {
    // set adapter for spinner
    ArrayAdapter<CharSequence> spinnerAdapter =
        ArrayAdapter.createFromResource(
            getContext(), spinnerOptionsResource, android.R.layout.simple_spinner_item);
    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerAdapter);
    // add listeners for item selection
    spinner.setOnItemSelectedListener(
        controller.handleSpinnerSelect(sortTypeSpinner, sortOrderSpinner));
  }

  private void setupSettingsButton(View view) {
    view.findViewById(R.id.settings_btn)
        .setOnClickListener(
            ignored -> {
              gameController.setBody(new ProfileSettingsFragment(gameController));
            });
  }
}
