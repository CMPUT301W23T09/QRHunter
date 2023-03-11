package com.cmput301w23t09.qrhunter.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.util.DeviceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.UUID;

/** This is the fragment displaying the user's profile */
public class ProfileFragment extends BaseFragment {
  /** This is the UUID corresponding to the profile of this player */
  private UUID deviceUUID;
  /** This is the controller that manages the fragment */
  private ProfileController controller;
  /** This is the view displaying the user's username */
  private TextView username;
  /** This is the view displaying the sum of points of the user's qr codes */
  private TextView totalPoints;
  /** This is the view displaying the total number of codes the user has */
  private TextView totalCodes;
  /** This is the view displaying the top score of the user's codes */
  private TextView topCodeScore;
  /** This is the spinner that allows the user to select the order their codes are displayed */
  private Spinner sortOrderSpinner;
  /** This is the view displaying the list of codes the user has */
  private GridView qrCodeList;
  /** This is the view displaying the settings button */
  private FloatingActionButton contactButton;

  /**
   * Initializes the fragment with the app controller
   *
   * @param gameController This is the app controller
   */
  public ProfileFragment(GameController gameController, UUID playerDeviceId) {
    super(gameController);
    deviceUUID = playerDeviceId;
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.profile_activity, container, false);

    controller = new ProfileController(this, getMainController(), deviceUUID);
    createProfile(view);
    return view;
  }

  /**
   * Creates the fragment elements
   *
   * @param view The view of the fragment's layout
   */
  private void createProfile(View view) {
    // get profile elements
    username = view.findViewById(R.id.username);
    qrCodeList = view.findViewById(R.id.code_list);
    totalPoints = view.findViewById(R.id.total_points);
    totalCodes = view.findViewById(R.id.total_codes);
    topCodeScore = view.findViewById(R.id.top_code_score);
    sortOrderSpinner = view.findViewById(R.id.order_spinner);
    contactButton = view.findViewById(R.id.contact_info_button);

    // create a default empty profile (shown while waiting for database queries)
    createDefaultProfile();

    // setup profile elements
    controller.setUpUsername(username);
    controller.setUpQRList(qrCodeList, totalPoints, totalCodes, topCodeScore, sortOrderSpinner);
    qrCodeList.setOnItemClickListener(controller.handleQRSelect());
  }

  /** Sets the profile elements to a blank/default state */
  private void createDefaultProfile() {
    username.setText("");
    totalPoints.setText(getString(R.string.total_points_txt, 0));
    totalCodes.setText(getString(R.string.total_codes_txt, 0));
    topCodeScore.setText(getString(R.string.top_code_txt, 0));
    createSpinner(sortOrderSpinner, R.array.order_options);

    setupContactButton();
  }

  /** Sets the image of the profile settings button and handler. */
  private void setupContactButton() {
    if (deviceUUID.equals(DeviceUtils.getDeviceUUID(getMainController().getActivity()))) {
      contactButton.setImageResource(R.drawable.baseline_settings_24);
    } else {
      contactButton.setImageResource(R.drawable.info_button);
    }

    contactButton.setOnClickListener(v -> controller.handleContactButtonClick());
  }

  /**
   * Display a prompt showcasing the contact information for this profile.
   *
   * @param email the email to display
   * @param phoneNo the phone number to display
   */
  public void displayContactInfo(String email, String phoneNo) {
    View view =
        getLayoutInflater()
            .inflate(R.layout.fragment_contact_info_prompt, (ViewGroup) getView(), false);

    TextView phoneNoDisplay = view.findViewById(R.id.contact_phoneNo);
    phoneNoDisplay.setText(getString(R.string.contact_info_phone_no, phoneNo));

    TextView emailDisplay = view.findViewById(R.id.contact_email);
    emailDisplay.setText(getString(R.string.contact_info_email, email));

    new AlertDialog.Builder(getContext())
        .setView(view)
        .setPositiveButton(R.string.close, (v, i) -> {})
        .show();
  }

  /**
   * Creates the spinner element of the fragment
   *
   * @param spinner This is the spinner to create
   * @param spinnerOptionsResource This is the resource containing the spinner's display options
   */
  private void createSpinner(Spinner spinner, @ArrayRes int spinnerOptionsResource) {
    // set adapter for spinner
    ArrayAdapter<CharSequence> spinnerAdapter =
        ArrayAdapter.createFromResource(
            getContext(), spinnerOptionsResource, android.R.layout.simple_spinner_item);
    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerAdapter);
    // add listeners for item selection
    spinner.setOnItemSelectedListener(controller.handleSpinnerSelect(sortOrderSpinner));
  }
}
