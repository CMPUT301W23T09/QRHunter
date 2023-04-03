package com.cmput301w23t09.qrhunter.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.cmput301w23t09.qrhunter.R;

/** Fragment displayed to show the percentile data of a player. */
public class ProfilePercentileFragment extends DialogFragment {

  private TextView totalPointsElement;
  private TextView codesScannedElement;
  private TextView topCodeElement;

  private double totalPoints = -1;
  private double codesScanned = -1;
  private double topCode = -1;

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = getLayoutInflater().inflate(R.layout.fragment_rankings, null);

    totalPointsElement = view.findViewById(R.id.rankings_total_points_percentage);
    codesScannedElement = view.findViewById(R.id.rankings_codes_scanned_percentage);
    topCodeElement = view.findViewById(R.id.rankings_top_code_percentage);

    if (totalPoints == -1 || codesScanned == -1 || topCode == -1) {
      totalPointsElement.setText(R.string.ellipses);
      codesScannedElement.setText(R.string.ellipses);
      topCodeElement.setText(R.string.ellipses);
    } else {
      totalPointsElement.setText(getString(R.string.percentage, totalPoints));
      codesScannedElement.setText(getString(R.string.percentage, codesScanned));
      topCodeElement.setText(getString(R.string.percentage, topCode));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getLayoutInflater().getContext());
    Dialog dialog =
        builder
            .setView(view)
            .setPositiveButton(R.string.close, (ignored, ignored2) -> this.dismiss())
            .create();
    dialog.getWindow().setGravity(Gravity.CENTER);
    dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    return dialog;
  }

  public void displayPercentiles(double totalPoints, double codesScanned, double topCode) {
    this.totalPoints = totalPoints;
    this.codesScanned = codesScanned;
    this.topCode = topCode;

    if (this.getDialog() != null && this.getDialog().isShowing()) {
      totalPointsElement.setText(getString(R.string.percentage, totalPoints));
      codesScannedElement.setText(getString(R.string.percentage, codesScanned));
      topCodeElement.setText(getString(R.string.percentage, topCode));
    }
  }
}
