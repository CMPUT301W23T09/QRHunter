package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Represents an item that can be rendered in the LeaderboardAdapter
 *
 * @param <T> type of the item
 */
public interface LeaderboardAdapterItem<T> {

  /**
   * Retrieves the view to render in the leaderboard
   *
   * @param context application context
   * @param convertView view
   * @param parent parent
   * @param item leaderboard item
   * @return view to display
   */
  View getView(Context context, View convertView, ViewGroup parent, T item);
}
