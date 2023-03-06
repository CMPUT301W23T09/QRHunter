package com.cmput301w23t09.qrhunter.qrcode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;

public class QRCodeFragment extends DialogFragment {

  // TODO: Figure out how to know when to display Add (+) button or Delete (Trash) button
  private TextView qrName;

  /**
   * Creates a new QRCodeFragment to display a specific QR Code
   *
   * <p>TODO: Replase hash with QRCode object
   *
   * @param hash Hash of the QR code to view
   * @return
   */
  public static QRCodeFragment newInstance(String hash) {
    Bundle args = new Bundle();
    args.putString("hash", hash);
    QRCodeFragment fragment = new QRCodeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
    setupViews(view);
    return createAlertDialog(view);
  }

  /**
   * Binds the UI components with the attributes of the QRCode
   *
   * @param view The view that displays fragment_qrcode.xml
   */
  private void setupViews(View view) {
    qrName = view.findViewById(R.id.qrName);
    qrName.setText((String) getArguments().get("hash"));
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
