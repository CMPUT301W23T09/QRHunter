package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import java.io.Serializable;

public class QRCodeFragment extends DialogFragment implements Serializable {

  // TODO: Figure out how to know when to display Add (+) button or Delete (Trash) button
  private TextView qrName;
  private ImageView locationPhoto;
  private QRCode qrCode;
  private Button takeLocationPhotoBtn;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * <p>TODO: Replase hash with QRCode object
   *
   * @param qrCode The QR code to view
   * @return
   */
  public static QRCodeFragment newInstance(QRCode qrCode) {
    Bundle args = new Bundle();
    args.putSerializable("qrcode", qrCode);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    qrCode = (QRCode) getArguments().getSerializable("qrcode");
    setupViews(view);
    updateLocationPhoto();
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) {
    qrName = view.findViewById(R.id.qr_name);
    locationPhoto = view.findViewById(R.id.location_photo);
    qrName.setText(qrCode.getHash());
    takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
    takeLocationPhotoBtn.setOnClickListener(
        v -> {
          if (qrCode.getPhotos().size() > 0) {
            qrCode.deletePhoto(qrCode.getPhotos().get(0));
            updateLocationPhoto();
          } else {
            LocationPhotoFragment frag = LocationPhotoFragment.newInstance(qrCode, this);
            frag.show(getParentFragmentManager(), "Take Location Photo");
          }
        });
  }

  public void updateLocationPhoto() {
    if (qrCode.getPhotos().size() > 0) {
      takeLocationPhotoBtn.setText(R.string.remove_location_photo);
      locationPhoto.setImageBitmap(qrCode.getPhotos().get(0));
    } else {
      takeLocationPhotoBtn.setText(R.string.take_location_photo);
      locationPhoto.setImageResource(android.R.color.transparent);
    }
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
