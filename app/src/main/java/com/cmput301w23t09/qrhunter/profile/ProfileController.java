package com.cmput301w23t09.qrhunter.profile;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.cmput301w23t09.qrhunter.DatabaseChangeListener;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/** This is the controller for the profile fragment of the app */
public abstract class ProfileController implements DatabaseChangeListener {
  /** This is the game controller that controls the content on screen. */
  protected final GameController gameController;
  /** This is the profile fragment the controller handles */
  protected final ProfileFragment fragment;
  /** This is the array of QRCode objects that the fragment displays */
  private ArrayList<QRCode> qrCodes;
  /** This is the adapter for displaying the QRCode objects */
  private QRCodeAdapter qrCodeAdapter;
  /** Device UUID of the profile */
  protected final UUID deviceUUID;
  /** This is the gridview showing the qr codes of the player */
  private GridView qrCodeList;
  /** This is the view that shows the qr codes of the player */
  private TextView totalPoints;
  /** This is the view showing the total number of codes the player has */
  private TextView totalCodes;
  /** This is the view showing the top score of the player's qr codes */
  private TextView topScore;
  /** This is the spinner that determines the qr code display order */
  private Spinner orderSpinner;

  /**
   * This initializes the controller with its corresponding fragment
   *
   * @param fragment This is the fragment the controller manages
   * @param gameController The game controller that controls the global view
   * @param deviceUUID Device UUID of the profile.
   */
  public ProfileController(
      ProfileFragment fragment, GameController gameController, UUID deviceUUID) {
    this.fragment = fragment;
    this.gameController = gameController;
    this.deviceUUID = deviceUUID;
  }

  public void setupFollowDetails(
      TextView followingText, TextView followersText, Button followButton) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            results -> {
              if (!results.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }

              followingText.setText(
                  fragment.getString(
                      R.string.profile_following, results.getData().getFollowing().size()));
              followersText.setText(
                  fragment.getString(
                      R.string.profile_followers, results.getData().getFollowers().size()));

              if (gameController.getActivePlayer().getFollowing().contains(deviceUUID)) {
                followButton.setText(R.string.unfollow);
              } else {
                followButton.setText(R.string.follow);
              }
            });
  }
  /**
   * This sets up the username view and profile pic view of the fragment
   *
   * @param usernameView This is the TextView that shows the username
   * @param profilePic This is the ImageView that shows their profile picture
   */
  public void setUpUsernameAndPicture(TextView usernameView, ImageView profilePic) {
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            results -> {
              // check if database query was successful
              if (!results.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }
              // otherwise get username and profile picture
              usernameView.setText(results.getData().getUsername());
              try {
                profilePic.setImageBitmap(results.getData().getProfilePic());
              } catch (ExecutionException | InterruptedException e) {
                showMsg("An error occurred while loading in your profile picture.");
              }
            });
  }

  /**
   * Sets up the list view of qr codes
   *
   * @param qrCodeList This is the view that contains the list view of codes
   * @param totalPoints This is the view that displays the total points
   * @param totalCodes This is the view that displays the total number of codes
   * @param topScore This is the view that displays the top score
   * @param orderSpinner This is the spinner that indicates the sort order
   */
  public void setUpQRList(
      GridView qrCodeList,
      TextView totalPoints,
      TextView totalCodes,
      TextView topScore,
      Spinner orderSpinner) {
    // set controller attributes
    this.qrCodeList = qrCodeList;
    this.totalPoints = totalPoints;
    this.totalCodes = totalCodes;
    this.topScore = topScore;
    this.orderSpinner = orderSpinner;

    // set QR code data and list view adapter
    qrCodes = new ArrayList<>();
    qrCodeAdapter = new QRCodeAdapter(gameController.getActivity(), qrCodes);
    qrCodeList.setAdapter(qrCodeAdapter);

    updateQRList();
  }

  private void updateQRList() {
    // get current player
    PlayerDatabase.getInstance()
        .getPlayerByDeviceId(
            deviceUUID,
            playerCollectionResults -> {
              // check if database query was successful
              if (!playerCollectionResults.isSuccessful()) {
                showMsg("An error occurred while loading in your player data.");
                return;
              }
              // otherwise get the qr code hashes of the current player
              if (playerCollectionResults.getData() == null) {
                return;
              }
              List<String> codeHashes = playerCollectionResults.getData().getQRCodeHashes();
              // get the qr codes from the hashes
              QRCodeDatabase.getInstance()
                  .getQRCodeHashes(
                      codeHashes,
                      QrCodeResults -> {
                        // add qr codes from result to qrCodes
                        qrCodes.clear();
                        qrCodes.addAll(QrCodeResults.getData());

                        // update qr code statistics
                        totalPoints.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.total_points_txt, getTotalScore()));
                        totalCodes.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.total_codes_txt, qrCodes.size()));
                        topScore.setText(
                            gameController
                                .getActivity()
                                .getString(R.string.top_code_txt, getTopScore()));

                        // sort and display qr codes
                        updateQRListSort(orderSpinner);
                      });
            });
  }

  /**
   * This creates a custom OnItemSelectedListener for the given spinner
   *
   * @param orderSpinner This is the spinner for selecting the sorting order of codes
   * @return Return the OnItemSelectedListener for the spinner
   */
  public AdapterView.OnItemSelectedListener handleSpinnerSelect(Spinner orderSpinner) {
    return new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateQRListSort(orderSpinner);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {}
    };
  }

  /**
   * This handles the action to take when the contact info button is clicked. Either displaying the
   * contact information or rendering the edit details fragment.
   */
  public abstract void handleContactButtonClick();

  /**
   * This updates the order of qr codes shown
   *
   * @param orderSpinner This is the spinner indicating the sorting order of codes
   */
  private void updateQRListSort(Spinner orderSpinner) {
    // get selected spinner options
    String selectedOrder = orderSpinner.getSelectedItem().toString();

    // get comparator
    Comparator<QRCode> comparator =
        new ScoreComparator(); // default comparator, sorts by score in ascending order

    if (Objects.equals(selectedOrder, "Descending")) {
      comparator = comparator.reversed();
    }

    // sort and update qr codes
    qrCodes.sort(comparator);
    qrCodeAdapter.notifyDataSetChanged();
  }

  /**
   * This is the onclicklistener for qr codes displayed in the profile
   *
   * @return Return the onclicklistener
   */
  public AdapterView.OnItemClickListener handleQRSelect() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QRCode qrCode = qrCodes.get(position);

        QRCodeDatabase.getInstance()
            .playerHasQRCode(
                gameController.getActivePlayer(),
                qrCode,
                task -> {
                  if (task.isSuccessful()) {
                    boolean playerHasQR = task.getData();

                    if (playerHasQR) {
                      gameController.setPopup(
                          DeleteQRCodeFragment.newInstance(
                              qrCode, gameController.getActivePlayer()));
                    } else {
                      gameController.setPopup(
                          QRCodeFragment.newInstance(qrCode, gameController.getActivePlayer()));
                    }
                  }
                });
      }
    };
  }

  /** This refreshes the profile upon database change */
  public void onChange() {
    if (qrCodeAdapter != null) {
      updateQRList();
    }
  }

  /** This sets up the listener for real-time database changes */
  public void addUpdater() {
    QRCodeDatabase.getInstance().addListener(this);
  }

  /**
   * This computes the sum of code scores
   *
   * @return Return the sum of code scores
   */
  public int getTotalScore() {
    int total = 0;
    for (QRCode qrCode : qrCodes) {
      total += qrCode.getScore();
    }
    return total;
  }

  /**
   * This computes the top score of the qr codes
   *
   * @return The top score
   */
  public long getTopScore() {
    qrCodes.sort(new ScoreComparator().reversed());
    if (qrCodes.size() > 0) {
      QRCode topQR = qrCodes.get(0);
      return topQR.getScore();
    } else {
      return 0;
    }
  }

  /**
   * This displays a Toast message
   *
   * @param msg The message to display
   */
  protected void showMsg(String msg) {
    Toast.makeText(gameController.getActivity(), msg, Toast.LENGTH_SHORT).show();
  }

  /**
   * Finds the position of the user's top QR code relative to all QR codes
   *
   * @param allQRCodes List of all QR codes
   * @param topQR The user's highest scoring QR code
   * @return -1 if the user's top QR code was not found in the collection
   * @return The user's top QR position relative to all the other QR code positions
   */
  private int getTopQRPosition(List<QRCode> allQRCodes, QRCode topQR) {
    int position = 1;

    for (QRCode code : allQRCodes) {
      String qrHash = code.getHash();

      if (topQR != null && qrHash.equals(topQR.getHash())) {
        return position;
      }

      position++;
    }

    return -1;
  }

  /**
   * Calculates and returns the percentile rank of the user's top QR code by score relative to all
   * QR codes to the callback
   */
  public void retrievePercentile(BiConsumer<Exception, Float> callback) {
    if (qrCodes.size() <= 0) {
      callback.accept(null, 0f);
      return;
    }

    List<QRCode> qrCodesSortedByScore = new ArrayList<>(qrCodes);
    qrCodesSortedByScore.sort(new ScoreComparator().reversed());
    QRCode topQR = qrCodesSortedByScore.get(0);

    QRCodeDatabase.getInstance()
        .getAllQRCodes(
            allQRCodes -> {
              if (!allQRCodes.isSuccessful()) {
                callback.accept(allQRCodes.getException(), null);
                return;
              }

              // Sort all the QR codes in ascending order
              allQRCodes.getData().sort(new ScoreComparator());
              int topQRPosition = getTopQRPosition(allQRCodes.getData(), topQR);
              int totalNumQRCodes = allQRCodes.getData().size();
              callback.accept(null, 100 - ((topQRPosition - 1) / (float) totalNumQRCodes) * 100);
            });
  }
}
