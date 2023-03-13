package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardFragment extends BaseFragment {

  private final LeaderboardController controller;
  private LeaderboardEntryAdapter entryAdapter;
  private List<LeaderboardEntry> leaderboardEntries;

  private String currentActiveTab;
  private Map<String, Leaderboard> cachedLeaderboards;

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

    cachedLeaderboards = new HashMap<>();
    leaderboardEntries = new ArrayList<>();
    entryAdapter = new LeaderboardEntryAdapter(getContext(), leaderboardEntries);
    ((ListView) view.findViewById(R.id.leaderboard_list)).setAdapter(entryAdapter);

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
            String tabText = String.valueOf(tab.getText());
            currentActiveTab = tabText;

            // Render cached leaderboard data if any exists.
            Leaderboard cachedLeaderboard = cachedLeaderboards.getOrDefault(tabText, null);
            if (cachedLeaderboard != null) {
              renderLeaderboard(cachedLeaderboard);
            } else {
              clearLeaderboard();
            }

            switch (String.valueOf(tab.getText())) {
              case "Total Points":
                controller.getTotalPointsLeaderboard(
                    (exception, leaderboard) ->
                        onLeaderboardCallback(tabText, exception, leaderboard));
                break;
              case "Scanned":
                controller.getTopScansLeaderboard(
                    (exception, leaderboard) ->
                        onLeaderboardCallback(tabText, exception, leaderboard));
                break;
              case "Top Codes":
                controller.getTopQRCodesLeaderboard(
                    (exception, leaderboard) ->
                        onLeaderboardCallback(tabText, exception, leaderboard));
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

  private void onLeaderboardCallback(String tabName, Exception exception, Leaderboard leaderboard) {
    if (exception != null) {
      Log.e(getClass().getName(), exception.getLocalizedMessage());
      Toast.makeText(
              getContext(),
              "An exception occurred while fetching leaderboard data!",
              Toast.LENGTH_SHORT)
          .show();
      return;
    }

    // Store leaderboard in cache to "reduce" empty leaderboard page time when flipping between
    cachedLeaderboards.put(tabName, leaderboard);

    if (currentActiveTab.equals(tabName)) {
      renderLeaderboard(leaderboard);
    }
  }

  private void clearLeaderboard() {
    leaderboardEntries.clear();
    entryAdapter.notifyDataSetChanged();
  }

  private void renderLeaderboard(Leaderboard leaderboard) {
    clearLeaderboard();

    leaderboardEntries.addAll(leaderboard.getEntries());
    entryAdapter.notifyDataSetChanged();
  }

  private void renderLeaderboard(Map<String, Leaderboard> leaderboardWithHeaders) {
    // TODO: when locations are implemented
  }
}
