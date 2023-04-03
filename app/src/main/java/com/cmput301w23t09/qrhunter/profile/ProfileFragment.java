package com.cmput301w23t09.qrhunter.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** This is the fragment displaying the user's profile */
public abstract class ProfileFragment extends BaseFragment {
  /** This is the controller that manages the fragment */
  protected ProfileController controller;
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
  /** This is the view displaying the user's profile picture */
  private ImageView profilePic;
  /** This is the view displaying the settings button */
  protected FloatingActionButton contactButton;
  /** This is the button that allows the user to view their rankings */
  protected FloatingActionButton rankingsButton;

  protected TextView followingText;
  protected TextView followersText;
  protected FloatingActionButton followButton;
  protected FloatingActionButton unfollowButton;
  protected FloatingActionButton loadingFollowButton;

  /**
   * Initializes the fragment with the app controller
   *
   * @param gameController This is the app controller
   */
  public ProfileFragment(GameController gameController) {
    super(gameController);
  }

  protected abstract ProfileController getProfileController();

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);

    controller = getProfileController();
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
    rankingsButton = view.findViewById(R.id.rankings_button);
    followersText = view.findViewById(R.id.followers_count);
    followingText = view.findViewById(R.id.following_count);
    followButton = view.findViewById(R.id.follow_button);
    unfollowButton = view.findViewById(R.id.unfollow_button);
    loadingFollowButton = view.findViewById(R.id.follow_loading_button);
    profilePic = view.findViewById(R.id.profile_pic);

    // create a default empty profile (shown while waiting for database queries)
    createDefaultProfile();

    // setup profile elements
    controller.setUpUsernameAndPicture(username, profilePic);
    controller.setupFollowDetails(
        followingText, followersText, followButton, unfollowButton, loadingFollowButton);
    controller.setUpQRList(qrCodeList, totalPoints, totalCodes, topCodeScore, sortOrderSpinner);
    qrCodeList.setOnItemClickListener(controller.handleQRSelect());

    // calculate qr code rankings
    handleProfileHeaderEstimates(view);
    controller.addUpdater();
  }

  /** Sets the profile elements to a blank/default state */
  private void createDefaultProfile() {
    username.setText("");
    totalPoints.setText("");
    totalCodes.setText("");
    topCodeScore.setText("");
    followingText.setText("");
    followersText.setText("");
    followButton.setVisibility(View.GONE);
    unfollowButton.setVisibility(View.GONE);
    loadingFollowButton.setVisibility(View.GONE);
    profilePic.setImageBitmap(null);
    createSpinner(sortOrderSpinner, R.array.order_options);

    setupSocialMethods();
    contactButton.setOnClickListener(v -> controller.handleContactButtonClick());
  }

  /** Sets the social related buttons. */
  protected abstract void setupSocialMethods();

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

  /**
   * Creates click listener for highest QR code score estimates
   *
   * @param view The view of the fragment's layout
   */
  private void handleProfileHeaderEstimates(View view) {
    rankingsButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            AlertDialog loadingDialog =
                new AlertDialog.Builder(getContext())
                    .setTitle("Rankings")
                    .setMessage("Calculating rankings...")
                    .setPositiveButton("OK", null)
                    .create();
            loadingDialog.show();

            controller.retrievePercentile(
                (exception, percentile) -> {
                  loadingDialog.dismiss();
                  if (exception != null) {
                    Toast.makeText(
                            getContext(),
                            "An exception occurred while fetching the ranking..",
                            Toast.LENGTH_SHORT)
                        .show();
                    return;
                  }

                  String formattedMessage = getString(R.string.ranking_message, percentile);
                  new AlertDialog.Builder(getContext())
                      .setTitle("Rankings")
                      .setMessage(formattedMessage)
                      .setPositiveButton("OK", null)
                      .create()
                      .show();
                });
          }
        });
  }

  /**
   * Gets the controller of the profile fragment
   *
   * @return Return the controller of the fragment
   */
  public ProfileController getController() {
    return controller;
  }
}
