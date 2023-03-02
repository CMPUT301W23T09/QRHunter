package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
  private ProfileController controller;
  private TextView username;
  private TextView totalPoints;
  private TextView totalCodes;
  private TextView topCodeScore;
  private Spinner sortTypeSpinner;
  private Spinner sortOrderSpinner;
  private GridView qrCodeList;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.profile_activity, container, false);
    controller = new ProfileController(this);
    createProfileElements(view);
    return view;
  }

  private void createProfileElements(View view) {
    username = view.findViewById(R.id.username);
    qrCodeList = view.findViewById(R.id.code_list);
    totalPoints = view.findViewById(R.id.total_points);
    totalCodes = view.findViewById(R.id.total_codes);
    topCodeScore = view.findViewById(R.id.top_code_score);
    sortTypeSpinner = view.findViewById(R.id.sort_spinner);
    sortOrderSpinner = view.findViewById(R.id.order_spinner);

    controller.setUpUsername(username);
    controller.setUpSortSpinners(sortTypeSpinner, sortOrderSpinner);
    controller.setUpQRList(
        qrCodeList, totalPoints, totalCodes, topCodeScore, sortTypeSpinner, sortOrderSpinner);
  }
}
