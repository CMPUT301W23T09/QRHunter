package com.cmput301w23t09.qrhunter.qrcode;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.comment.Comment;
import com.cmput301w23t09.qrhunter.comment.CommentAdapter;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoAdapter;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoController;
import com.cmput301w23t09.qrhunter.locationphoto.LocationPhotoStorage;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.player.PlayerDatabase;
import com.cmput301w23t09.qrhunter.profile.MyProfileFragment;
import com.cmput301w23t09.qrhunter.profile.OtherProfileFragment;
import com.cmput301w23t09.qrhunter.profile.ProfileFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.smarteist.autoimageslider.SliderView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Displays information about a specific QRCode. It also lets the user: */
public class QRCodeFragment extends DialogFragment implements Serializable {
  protected QRCode qrCode;
  protected Button takeLocationPhotoBtn;
  protected CheckBox locationCheckbox;
  protected LocationHandler locationHandler;
  protected Player activePlayer;
  protected FloatingActionButton addButton;
  protected FloatingActionButton deleteButton;
  protected FloatingActionButton loadingButton;
  protected TabLayout tabLayout;
  protected EditText commentBox;
  protected ListView listView;
  protected List<Comment> comments;
  protected CommentAdapter commentsAdapter;
  protected SliderView locationPhotoSlider;
  protected LocationPhotoAdapter locationPhotoAdapter;
  protected LocationPhotoStorage locationPhotoStorage;

  protected ListView listElement;
  protected QRCodePlayerScansAdapter scansAdapter;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param player The player that scanned the given QR code
   * @return QRCodeFragment
   */
  public static QRCodeFragment newInstance(QRCode qrCode, Player player) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("player", player);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    activePlayer = (Player) getArguments().getSerializable("player");
    locationPhotoStorage = LocationPhotoStorage.getInstance();

    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return createAlertDialog(view);
  }

  @Override
  public void onResume() {
    super.onResume();

    // Adjust QRFragment window size to match content.
    Window window = getDialog().getWindow();
    window.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) throws ExecutionException, InterruptedException {
    // get widgets in QRCodeFragment
    locationCheckbox = view.findViewById(R.id.location_request_box);
    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    TextView qrName = view.findViewById(R.id.qr_name);
    TextView qrScore = view.findViewById(R.id.qr_points);
    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    addButton = view.findViewById(R.id.addButton);
    deleteButton = view.findViewById(R.id.deleteButton);
    loadingButton = view.findViewById(R.id.loadingButton);
    tabLayout = view.findViewById(R.id.qr_nav);
    commentBox = view.findViewById(R.id.comment_box);
    listView = view.findViewById(R.id.qr_nav_items);

    // setup location photos
    locationPhotoSlider = view.findViewById(R.id.location_photos);
    locationPhotoAdapter = new LocationPhotoAdapter(this.getContext(), qrCode);
    locationPhotoSlider.setSliderAdapter(locationPhotoAdapter, false);

    // fill views with qr code information
    qrName.setText(qrCode.getName());
    qrScore.setText(qrCode.getScore().toString() + " PTS");
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    // set up buttons
    setUpButtons(view);

    // set up tab menu
    setupTab(view);

    // set up location photos
    updateLocationPhoto();
  }

  /**
   * Enable and disable buttons of QRCodeFragment
   *
   * @param view the view
   */
  protected void setUpButtons(View view) {
    takeLocationPhotoBtn.setVisibility(View.GONE);
    locationCheckbox.setVisibility(View.GONE);
    addButton.setVisibility(View.GONE);
    deleteButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.GONE);
    commentBox.setVisibility(View.GONE);
  }

  /**
   * Sets up the tab related items and listeners for the qr fragment.
   *
   * @param view dialog
   */
  private void setupTab(View view) {
    tabLayout = view.findViewById(R.id.qr_nav);
    tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.players_who_scanned_tab_title)));
    tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.comments_tab_title)));

    listElement = view.findViewById(R.id.qr_nav_items);
    setupListListener();

    scansAdapter = new QRCodePlayerScansAdapter(getContext());
    setupPlayerScans();

    comments = new ArrayList<>();
    commentsAdapter = new CommentAdapter(getContext(), comments);
    setupPlayerComments();

    listElement.setAdapter(
        scansAdapter); // by default, the adapter should display the scanned players.

    commentBox.setVisibility(View.GONE);
    tabLayout.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {

          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getText().equals(getText(R.string.players_who_scanned_tab_title))) {
              // Who scanned the QR
              listElement.setAdapter(scansAdapter);
              toggleCommentBox(false);
            } else {
              listElement.setAdapter(commentsAdapter);
              toggleCommentBox(true);
            }
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {}

          @Override
          public void onTabReselected(TabLayout.Tab tab) {}
        });

    // allows user to enter a comment
    commentBox.setOnClickListener(this::onAddCommentInput);
    // allows user to click send and store comment in the database
    commentBox.setOnTouchListener((v, event) -> onSendComment(v, event, null));
    // handles Enter key press to store comment in the database
    commentBox.setOnKeyListener(
        (v, keyCode, event) -> {
          if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return onSendComment(v, null, event);
          }
          return false;
        });
  }

  /**
   * Toggles whether or not the comment box should be shown or not.
   *
   * <p>Behaves like a 'hook', where the comment box is only available to AddQRCodeFragment and
   * DeleteQRCodeFragment (essentially whenever the player has/had scanned the code themselved)
   *
   * @param isShown Whether or not the comment box should be shown.
   */
  protected void toggleCommentBox(boolean isShown) {}

  /** Fetch all players who scanned this QR and add it to the adapter. */
  private void setupPlayerScans() {
    // For each player who scanned the QR, fetch them and the score they have.
    // Upon fetching them, add them to our adapter.
    for (String documentId : qrCode.getPlayers()) {
      PlayerDatabase.getInstance()
          .getPlayerByDocumentId(
              documentId,
              task -> {
                if (!task.isSuccessful()) {
                  Toast.makeText(
                          getContext(),
                          "An exception occurred while fetching the players who scanned this QR...",
                          Toast.LENGTH_LONG)
                      .show();
                  return;
                }
                Player player = task.getData();

                // Retrieve all of the QRs the player has to get their total score.
                QRCodeDatabase.getInstance()
                    .getQRCodeHashes(
                        player.getQRCodeHashes(),
                        qrsTask -> {
                          if (!qrsTask.isSuccessful()) {
                            Toast.makeText(
                                    getContext(),
                                    "An exception occurred while fetching the QRs of a player who scanned this QR...",
                                    Toast.LENGTH_LONG)
                                .show();
                            return;
                          }

                          // Calculate the player's total score
                          int score =
                              qrsTask.getData().stream()
                                  .mapToInt(QRCode::getScore)
                                  .reduce(0, Integer::sum);

                          // Add an entry into our adapter with their score and player.
                          scansAdapter.add(new QRPlayerScanEntry(player, score));
                        });
              });
    }
  }

  private void setupListListener() {
    listElement.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean isPlayerScansSelected = tabLayout.getSelectedTabPosition() == 0;

            if (isPlayerScansSelected) {
              QRPlayerScanEntry scanEntry = scansAdapter.getItem(position);

              // Navigate to their profile.
              GameController gameController = ((GameActivity) getActivity()).getController();
              ProfileFragment profileFragment;
              boolean isOurProfile =
                  scanEntry.getPlayer().getDeviceId().equals(activePlayer.getDeviceId());
              if (isOurProfile) {
                profileFragment = new MyProfileFragment(gameController);
              } else {
                profileFragment =
                    new OtherProfileFragment(gameController, scanEntry.getPlayer().getDeviceId());
              }
              gameController.setBody(profileFragment);
              QRCodeFragment.this.dismiss();
            }
          }
        });
  }

  /**
   * Updates the locationPhoto image view to show the newly-captured location photo
   *
   * @see CameraLocationPhotoController
   * @see LocationPhotoController
   */
  public void updateLocationPhoto() {
    locationPhotoStorage.playerHasLocationPhoto(
        qrCode,
        activePlayer,
        (hasPhoto) -> {
          if (hasPhoto) {
            takeLocationPhotoBtn.setText(R.string.remove_location_photo);
            locationPhotoSlider.setCurrentPagePosition(
                locationPhotoAdapter.getPlayerLocationPhoto(activePlayer));
          } else takeLocationPhotoBtn.setText(R.string.take_location_photo);
        });
    locationPhotoAdapter.renewLocationPhotos(
        photos -> {
          if (photos.size() == 0) locationPhotoSlider.setVisibility(View.GONE);
          else locationPhotoSlider.setVisibility(View.VISIBLE);
        });
  }

  /**
   * Sets up the player comments for the current QR code. Adds all the comments for the current QR
   * code to the list Notifies Adapter of data change
   */
  private void setupPlayerComments() {
    comments.clear();
    comments.addAll(qrCode.getComments());
    commentsAdapter.notifyDataSetChanged();
  }

  /**
   * Removes hint text when clicked*
   *
   * @param view view
   */
  private void onAddCommentInput(View view) {
    commentBox.setHint("");
  }

  /**
   * Handles sending a comment when the send icon is clicked. Changes the color of the send icon
   * depending on the state.
   *
   * @param view View
   * @param event The motion event
   * @param keyEvent The keyboard event
   * @return True if the event is handled. otherwise, false.
   */
  private boolean onSendComment(View view, MotionEvent event, KeyEvent keyEvent) {
    final int DRAWABLE_RIGHT = 2;

    // changes color of send icon depending on the state
    int default_color = ContextCompat.getColor(requireContext(), R.color.purple_500);
    int on_send_color = ContextCompat.getColor(requireContext(), R.color.purple_200);

    if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
      if (event.getRawX()
          >= (commentBox.getRight()
              - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
        commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].setTint(on_send_color);
        return true;
      }
    } else if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
      if (event.getRawX()
          >= (commentBox.getRight()
              - commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
        commentBox.getCompoundDrawables()[DRAWABLE_RIGHT].setTint(default_color);

        // stores comment message in a variable and calls the addComment method
        String commentText = commentBox.getText().toString().trim();
        if (!commentText.isEmpty()) {
          addComment(commentText);
          commentBox.setText("");
          commentBox.setHint(R.string.comment_box_hint_text);
        } else {
          // Comment text is empty, show an error message
          Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    } else if (keyEvent != null
        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      // handles the enter key press
      String commentText = commentBox.getText().toString().trim();
      if (!commentText.isEmpty()) {
        addComment(commentText);
        commentBox.setText("");
        commentBox.setHint(R.string.comment_box_hint_text);
      } else {
        // Comment text is empty, show an error message
        Toast.makeText(getContext(), "Comment text is empty", Toast.LENGTH_SHORT).show();
      }
      return true;
    }
    return false;
  }

  /**
   * Adds a comment to the comments list updates the comments adapter saves the comment to the
   * database.
   *
   * @param commentText The comment text to be added to database
   */
  private void addComment(String commentText) {
    Comment comment =
        new Comment(activePlayer.getDocumentId(), activePlayer.getUsername(), commentText);
    comments.add(comment);
    commentsAdapter.notifyDataSetChanged();
    listElement.setSelection(commentsAdapter.getCount() - 1);

    QRCodeDatabase.getInstance()
        .getQRCodeByHash(
            qrCode.getHash(),
            qrCodeQueryResults -> {
              if (qrCodeQueryResults.isSuccessful()) {
                // Update the QRCode with the new comment
                QRCode qrCodeToUpdate = qrCodeQueryResults.getData();
                qrCodeToUpdate.addComment(comment);
                QRCodeDatabase.getInstance()
                    .updateQRCode(
                        qrCodeToUpdate,
                        updateResults -> {
                          if (updateResults.isSuccessful()) {
                            Log.d(
                                TAG,
                                "Comment added to QRCode with hash: " + qrCodeToUpdate.getHash());
                          } else {
                            Log.w(
                                TAG,
                                "Error updating QRCode with new comment",
                                updateResults.getException());
                          }
                        });
              } else {
                Log.w(TAG, "Error getting QRCode from database", qrCodeQueryResults.getException());
              }
            });
    Log.d("AddQRCodeFragment", "Comment saved to database");
  }

  /**
   * Creates a dialog box to display QRCode information in
   *
   * @param view The view that displays fragment_qrcode.xml
   * @return An AlertDialog that displays QRCode information
   */
  private AlertDialog createAlertDialog(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).setPositiveButton("Close", null).create();
  }
}
