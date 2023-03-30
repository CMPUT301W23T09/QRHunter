package com.cmput301w23t09.qrhunter.leaderboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cmput301w23t09.qrhunter.R;

/** Represents a leaderboard entry */
public class LeaderboardEntry
    implements Comparable<LeaderboardEntry>, LeaderboardAdapterItem<LeaderboardEntry> {

  /** Name to display for this leaderboard entry */
  private final String name;
  /** Score to associate with this entry */
  private final long score;
  /** Score suffix of this entry */
  private final String scoreSuffix;

  /**
   * Constructor for a leaderboard entry
   *
   * @param name name of the leaderboard entry
   * @param score score of the leaderboard entry
   * @param scoreSuffix suffix to use for the leaderboard entry
   */
  public LeaderboardEntry(String name, long score, String scoreSuffix) {
    this.name = name;
    this.score = score;
    this.scoreSuffix = scoreSuffix;
  }

  /**
   * Retrieve the name of the leaderboard entry
   *
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieve the score of the leaderboard entry
   *
   * @return score
   */
  public long getScore() {
    return score;
  }

  /**
   * Retrieve the suffix to use for this leaderboard entry
   *
   * @return score suffix
   */
  public String getScoreSuffix() {
    return scoreSuffix;
  }

  @Override
  public int compareTo(LeaderboardEntry otherEntry) {
    int entryComparisonScore = Long.compare(otherEntry.getScore(), getScore());

    // Compare name if score is equal.
    if (entryComparisonScore == 0) {
      return otherEntry.getName().compareTo(getName());
    }

    return entryComparisonScore;
  }

  @SuppressLint("ResourceType")
  @Override
  public View getView(Context context, View convertView, ViewGroup parent, LeaderboardEntry item) {
    // Get leaderboard entry view
    View view = convertView;
    if (view == null || R.layout.leaderboard_entry_view != view.getId()) {
      view = LayoutInflater.from(context).inflate(R.layout.leaderboard_entry_view, parent, false);
    }

    // set fields of view
    TextView name = view.findViewById(R.id.leaderboard_entry_text);
    name.setText(item.getName());

    TextView score = view.findViewById(R.id.leaderboard_entry_score);
    score.setText(item.getScore() + " " + item.getScoreSuffix());

    return view;
  }
}
