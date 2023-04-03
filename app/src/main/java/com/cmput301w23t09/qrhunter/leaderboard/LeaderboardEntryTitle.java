package com.cmput301w23t09.qrhunter.leaderboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cmput301w23t09.qrhunter.R;

/** Leaderboard entry describing a title view */
public class LeaderboardEntryTitle implements LeaderboardAdapterItem<LeaderboardEntryTitle> {

  private final String title;

  public LeaderboardEntryTitle(String title) {
    this.title = title;
  }

  /**
   * Retrieve the title this entry represents
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  @SuppressLint("ResourceType")
  @Override
  public View getView(
      Context context, View convertView, ViewGroup parent, LeaderboardEntryTitle item) {
    // Get leaderboard entry view
    View view = convertView;
    if (view == null || R.layout.leaderboard_title_view != view.getId()) {
      view = LayoutInflater.from(context).inflate(R.layout.leaderboard_title_view, parent, false);
    }

    // set fields of view
    TextView name = view.findViewById(R.id.leaderboard_title);
    name.setText(item.getTitle());

    return view;
  }
}
