package com.cmput301w23t09.qrhunter.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import java.util.ArrayList;

public class QRSearchResultFragment extends DialogFragment {
  private Context context;
  private ArrayList<QRCode> qrCodes;
  private QRCodeAdapter codeAdapter;

  public QRSearchResultFragment(ArrayList<QRCode> nearbyCodes, Context context) {
    this.context = context;
    qrCodes = nearbyCodes;
    codeAdapter = new QRCodeAdapter(context, qrCodes);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = getLayoutInflater().inflate(R.layout.fragment_qr_search_results, null);
    GridView resultCodeList = view.findViewById(R.id.search_qr_result);
    resultCodeList.setAdapter(codeAdapter);
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    Dialog dialog = builder.setView(view).setPositiveButton("Close", null).create();
    dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
    return dialog;
  }
}
