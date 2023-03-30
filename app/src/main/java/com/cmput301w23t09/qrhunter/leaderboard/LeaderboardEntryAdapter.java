package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/** ArrayAdapter for leaderboard entries */
public class LeaderboardEntryAdapter extends ArrayAdapter<LeaderboardAdapterItem<?>> {
  private final Context context;
  private final List<LeaderboardAdapterItem<?>> items;

  public LeaderboardEntryAdapter(Context context, List<LeaderboardAdapterItem<?>> items) {
    super(context, 0, items);
    this.context = context;
    this.items = items;
  }

  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    // get leaderboard data
    LeaderboardAdapterItem<?> entry = items.get(position);

    if (entry instanceof LeaderboardEntryTitle) {
      return ((LeaderboardEntryTitle) entry)
          .getView(context, convertView, parent, (LeaderboardEntryTitle) entry);
    } else if (entry instanceof LeaderboardEntry) {
      return ((LeaderboardEntry) entry)
          .getView(context, convertView, parent, (LeaderboardEntry) entry);
    } else {
      throw new UnsupportedOperationException("Unknown leaderboard entry UI element");
    }
  }
}
