package com.cmput301w23t09.qrhunter.social.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.tabs.TabLayout;
import java.util.Map;

public class LeaderboardFragment extends BaseFragment {

  private final LeaderboardController controller;

  public LeaderboardFragment(GameController gameController) {
    super(gameController);

    controller = new LeaderboardController(this);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
    setupTabList(view);

    return view;
  }

  /**
   * Populate and setup the tab list with the various leaderboard filter options
   *
   * @param view the view of the fragment
   */
  private void setupTabList(View view) {
    TabLayout tabNavigation = view.findViewById(R.id.leaderboard_navigation);
    tabNavigation.addOnTabSelectedListener(
        new TabLayout.OnTabSelectedListener() {
          @Override
          public void onTabSelected(TabLayout.Tab tab) {
            // Clear leaderboard and wait for new data
            clearLeaderboard();

            switch (String.valueOf(tab.getText())) {
              case "Total Points":
                controller.getTotalPointsLeaderboard(
                    LeaderboardFragment.this::onLeaderboardCallback);
                break;
              case "Scanned":
                controller.getTopScansLeaderboard(LeaderboardFragment.this::onLeaderboardCallback);
                break;
              case "Top Codes":
                controller.getTopQRCodesLeaderboard(
                    LeaderboardFragment.this::onLeaderboardCallback);
                break;
              case "Top Codes (By Region)":
                // TODO: Implement after location uploading is implemented.
                break;
              default:
                throw new UnsupportedOperationException(
                    "This tab item is not supported: " + tab.getText());
            }
          }

          @Override
          public void onTabUnselected(TabLayout.Tab tab) {
            // ignored
          }

          @Override
          public void onTabReselected(TabLayout.Tab tab) {
            // ignored
          }
        });

    // Create tab list items
    String[] tabOptions = getResources().getStringArray(R.array.leaderboard_options);
    for (String tabName : tabOptions) {
      tabNavigation.addTab(tabNavigation.newTab().setText(tabName));
    }
  }

  private void onLeaderboardCallback(Exception exception, Leaderboard<?> leaderboard) {
    if (exception != null) {
      Log.e(getClass().getName(), exception.getLocalizedMessage());
      Toast.makeText(
              getContext(),
              "An exception occurred while fetching leaderboard data!",
              Toast.LENGTH_SHORT)
          .show();
      return;
    }

    renderLeaderboard(leaderboard);
  }

  private void clearLeaderboard() {}

  private void renderLeaderboard(Leaderboard<?> leaderboard) {}

  private void renderLeaderboard(Map<String, QRLeaderboard> leaderboardWithHeaders) {}
}
