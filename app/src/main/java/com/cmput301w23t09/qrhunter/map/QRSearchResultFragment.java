package com.cmput301w23t09.qrhunter.map;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.QRCodeAdapter;
import java.util.ArrayList;

public class QRSearchResultFragment extends DialogFragment {
  private ArrayList<QRCode> qrCodes;
  private QRCodeAdapter codeAdapter;

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view =
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_qr_search_results, null);
    getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
    return super.onCreateDialog(savedInstanceState);
  }
}
