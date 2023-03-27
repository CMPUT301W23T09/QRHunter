package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/** Displays information about a specific QRCode. It also lets the user: */
public class QRCodeFragment extends DialogFragment implements Serializable {
  protected QRCode qrCode;
  protected ImageView locationPhoto;
  protected Button takeLocationPhotoBtn;
  protected CheckBox locationCheckbox;
  protected LocationHandler locationHandler;
  protected LocationPhotoFragment locationPhotoFragment;
  protected Player activePlayer;
  protected FloatingActionButton addButton;
  protected FloatingActionButton deleteButton;
  protected FloatingActionButton loadingButton;
  protected QRCodeDatabase qrCodeDatabase;
  protected TabLayout tabLayout;
  protected EditText commentBox;
  protected ListView listView;


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
    qrCodeDatabase = QRCodeDatabase.getInstance();
    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return createAlertDialog(view);



  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) throws ExecutionException, InterruptedException {
    // get widgets in QRCodeFragment
    locationPhoto = view.findViewById(R.id.location_photo);
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

    // fill views with qr code information
    qrName.setText(qrCode.getName());
    qrScore.setText(qrCode.getScore().toString() + " PTS");
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    // set up buttons
    setUpButtons(view);

  }

  /** Enable and disable buttons of QRCodeFragment */
  protected void setUpButtons(View view) {
    locationPhoto.setVisibility(View.GONE);
    takeLocationPhotoBtn.setVisibility(View.GONE);
    locationCheckbox.setVisibility(View.GONE);
    addButton.setVisibility(View.GONE);
    deleteButton.setVisibility(View.GONE);
    loadingButton.setVisibility(View.GONE);
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
