package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardFragment extends BaseFragment {

  private final LeaderboardController controller;

  private String currentActiveTab;
  private Map<String, List<Leaderboard<?>>> cachedLeaderboardsByTab;
  private List<LeaderboardAdapterItem<?>> leaderboardAdapterItems;
  private LeaderboardEntryAdapter leaderboardAdapter;

  public LeaderboardFragment(GameController gameController) {
    super(gameController);

    controller = new LeaderboardController(this, gameController);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

    cachedLeaderboardsByTab = new HashMap<>();
    leaderboardAdapterItems = new ArrayList<>();
    leaderboardAdapter = new LeaderboardEntryAdapter(getContext(), leaderboardAdapterItems);

    ListView leaderboardListView = view.findViewById(R.id.leaderboard_list);
    leaderboardListView.setAdapter(leaderboardAdapter);

    view.findViewById(R.id.leaderboard_filter_button)
        .setOnClickListener(l -> controller.onFilterButtonClick());

    setupTabList(view);
    setupListView(view);
    return view;
  }

  /**
   * Populate and setup the tab list with the various leaderboard filter options
   *
   * @param view the view of the fragment
   */
  @SuppressWarnings("unchecked")
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
            List<Leaderboard<?>> cachedLeaderboards =
                cachedLeaderboardsByTab.getOrDefault(tabText, null);
            if (cachedLeaderboards != null) {
              renderLeaderboards(cachedLeaderboards);
            } else {
              clearLeaderboards();
            }

            LeaderboardFragment.this.onTabSelected(tabText);
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

  /** Clears the leaderboard cache and reloads the current shown leaderboard. */
  public void clearCachesAndReloadLeaderboard() {
    cachedLeaderboardsByTab.clear();
    onTabSelected(currentActiveTab);
  }

  /**
   * Called when a tab is selected and a leaderboard should be rendered.
   *
   * @param tabName tab selected
   */
  private void onTabSelected(String tabName) {
    switch (tabName) {
      case "Total Points":
        controller.getTotalPointsLeaderboard(
            (exception, leaderboard) ->
                onLeaderboardCallback(tabName, exception, Collections.singletonList(leaderboard)));
        break;
      case "Scanned":
        controller.getTopScansLeaderboard(
            (exception, leaderboard) ->
                onLeaderboardCallback(tabName, exception, Collections.singletonList(leaderboard)));
        break;
      case "Top Codes":
        controller.getTopQRCodesLeaderboard(
            (exception, leaderboard) ->
                onLeaderboardCallback(tabName, exception, Collections.singletonList(leaderboard)));
        break;
      case "Top Codes (By Region)":
        controller.getTopQRCodesByRegionLeaderboard(
            (exception, leaderboardsByRegion) ->
                onLeaderboardCallback(
                    tabName, exception, (List<Leaderboard<?>>) (Object) leaderboardsByRegion));
        break;
      default:
        throw new UnsupportedOperationException("This tab item is not supported: " + tabName);
    }
  }

  /**
   * Sets up the leaderboard list listener
   *
   * @param view
   */
  private void setupListView(View view) {
    ListView leaderboardListView = view.findViewById(R.id.leaderboard_list);
    leaderboardListView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LeaderboardAdapterItem<?> entry = leaderboardAdapterItems.get(position);

            // What kind of entry is this?
            if (entry instanceof PlayerLeaderboardEntry) {
              // Player leaderboard item, display their profile.
              controller.handleEntryClick((PlayerLeaderboardEntry) entry);
            } else if (entry instanceof QRCodeLeaderboardEntry) {
              // QR leaderboard item, display the QR fragment.
              controller.handleEntryClick((QRCodeLeaderboardEntry) entry);
            }
          }
        });
  }

  /**
   * Called when the controller returns leaderboard data
   *
   * @param tabName leaderboard tab name
   * @param exception if any exception occurred while fetching the leaderboards
   * @param leaderboards the leaderboards fetched
   */
  private void onLeaderboardCallback(
      String tabName, Exception exception, List<Leaderboard<?>> leaderboards) {
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
    cachedLeaderboardsByTab.put(tabName, leaderboards);

    if (currentActiveTab.equals(tabName)) {
      renderLeaderboards(leaderboards);
    }
  }

  /** Clear the current shown leaderboards */
  private void clearLeaderboards() {
    leaderboardAdapterItems.clear();
    leaderboardAdapter.notifyDataSetChanged();
  }

  /**
   * Render a list of leaderboards onto the list
   *
   * @param leaderboards leaderboards to render
   */
  private void renderLeaderboards(List<Leaderboard<?>> leaderboards) {
    clearLeaderboards();

    for (Leaderboard<?> leaderboard : leaderboards) {
      if (leaderboard.getName() != null) {
        // Add title element
        leaderboardAdapterItems.add(new LeaderboardEntryTitle(leaderboard.getName()));
      }
      leaderboardAdapterItems.addAll(leaderboard.getEntries());
      leaderboardAdapter.notifyDataSetChanged();
    }
  }
}
