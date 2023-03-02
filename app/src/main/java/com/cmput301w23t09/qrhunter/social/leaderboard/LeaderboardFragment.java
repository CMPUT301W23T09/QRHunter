package com.cmput301w23t09.qrhunter.social.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.tabs.TabLayout;

public class LeaderboardFragment extends BaseFragment {

  private final LeaderboardController controller;

  public LeaderboardFragment(GameController gameController) {
    super(gameController);

    controller = new LeaderboardController();
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
    createTab(view);

    return view;
  }

  private void createTab(View view) {
    TabLayout layout = view.findViewById(R.id.leaderboard_navigation);
    String[] tabOptions = getResources().getStringArray(R.array.leaderboard_options);

    for (String tabName : tabOptions) {
      layout.addTab(layout.newTab().setText(tabName));
    }
  }
}
