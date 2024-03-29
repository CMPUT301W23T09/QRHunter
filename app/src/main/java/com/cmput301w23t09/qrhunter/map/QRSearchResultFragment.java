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
import com.cmput301w23t09.qrhunter.GameActivity;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.DeleteQRCodeFragment;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeDatabase;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

/** The fragment the QRSearchResultFragment should open up from */
public class QRSearchResultFragment extends DialogFragment {
  private MapFragment fragment;
  /** The list of qr codes to display */
  private ArrayList<QRCode> qrCodes;
  /** The adapter for displaying the codes */
  private QRCodeAdapter codeAdapter;

  /**
   * This initializes a dialog fragment with a list of codes to display, and the fragment to open it
   * up from
   *
   * @param nearbyCodes The list of codes to display
   * @param fragment The fragment to open the dialog from
   */
  public QRSearchResultFragment(ArrayList<QRCode> nearbyCodes, MapFragment fragment) {
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
            Player activePlayer =
                ((GameActivity) fragment.getActivity()).getController().getActivePlayer();

            QRCodeDatabase.getInstance()
                .playerHasQRCode(
                    activePlayer,
                    qrCode,
                    task -> {
                      if (task.isSuccessful()) {
                        boolean playerHasQR = task.getData();

                        if (playerHasQR) {
                          fragment
                              .getGameController()
                              .setPopup(DeleteQRCodeFragment.newInstance(qrCode, activePlayer));
                        } else {
                          fragment
                              .getGameController()
                              .setPopup(QRCodeFragment.newInstance(qrCode, activePlayer));
                        }
                      }
                    });
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
