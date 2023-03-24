package com.cmput301w23t09.qrhunter.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class QRSearchResultFragment extends DialogFragment {
  private Fragment fragment;
  private ArrayList<QRCode> qrCodes;
  private QRCodeAdapter codeAdapter;

  public QRSearchResultFragment(ArrayList<QRCode> nearbyCodes, Fragment fragment) {
    this.fragment = fragment;
    qrCodes = nearbyCodes;
    codeAdapter = new QRCodeAdapter(fragment.getContext(), qrCodes);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    // setup fragment
    View view = getLayoutInflater().inflate(R.layout.fragment_qr_search_results, null);
    GridView resultCodeList = view.findViewById(R.id.search_qr_result);
    resultCodeList.setAdapter(codeAdapter);
    resultCodeList.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            QRCode qrCode = qrCodes.get(position);
            QRCodeFragment.newInstance(qrCode, null)
                .show(fragment.getParentFragmentManager(), "Show QR code information");
          }
        });
    FloatingActionButton closeBtn = view.findViewById(R.id.qr_search_close_btn);
    // build the dialog
    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
    Dialog dialog = builder.setView(view).create();
    dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
    // set close button on click listener
    closeBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        });
    return dialog;
  }
}
