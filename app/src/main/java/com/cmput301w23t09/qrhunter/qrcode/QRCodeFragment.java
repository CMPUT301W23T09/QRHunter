package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/** Displays information about a specific QRCode. */
public class QRCodeFragment extends DialogFragment implements Serializable {
  private QRCode qrCode;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * @param qrCode The QR code to view
   * @param player A player that has scanned the QR code
   * @return QRCodeFragment
   */
  public static QRCodeFragment newInstance(QRCode qrCode, @Nullable Player player) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    args.putSerializable("player", player);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = getLayoutInflater().inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    try {
      setupViews(view);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return createAlertDialog(view);
  }

  /**
   * Setups the UI components of the dialog
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  protected void setupViews(View view) throws ExecutionException, InterruptedException {
    // disable unused views
    view.findViewById(R.id.location_request_box).setVisibility(View.GONE);
    view.findViewById(R.id.addButton).setVisibility(View.GONE);
    view.findViewById(R.id.deleteButton).setVisibility(View.GONE);
    view.findViewById(R.id.take_location_photo_btn).setVisibility(View.GONE);

    // display qr code info
    setUpQRCodeView(view);
  }

  /**
   * Binds the UI components with the name, score, and visual attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  protected void setUpQRCodeView(View view) throws ExecutionException, InterruptedException {
    TextView qrName = view.findViewById(R.id.qr_name);
    qrName.setText(qrCode.getName());

    TextView qrScore = view.findViewById(R.id.qr_points);
    qrScore.setText(
        String.format(Locale.getDefault(Locale.Category.FORMAT), "%d PTS", qrCode.getScore()));

    ImageView qrCodeVisual = view.findViewById(R.id.qr_code_visual);
    qrCodeVisual.setImageBitmap(qrCode.getVisualRepresentation());

    ImageView locationPhoto = view.findViewById(R.id.location_photo);
    locationPhoto.setImageBitmap(qrCode.getPhotos().get(0).getPhoto());
  }

  /**
   * Creates a dialog box to display QRCode information in
   *
   * @param view The view that displays fragment_qrcode.xml
   * @return An AlertDialog that displays QRCode information
   */
  protected AlertDialog createAlertDialog(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).setPositiveButton("Close", null).create();
  }
}
