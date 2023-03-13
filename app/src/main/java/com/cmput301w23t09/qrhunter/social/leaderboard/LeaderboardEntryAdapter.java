package com.cmput301w23t09.qrhunter.social.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import java.util.List;

public class LeaderboardEntryAdapter extends ArrayAdapter<LeaderboardEntry> {
  private final List<LeaderboardEntry> entries;
  private final Context context;

  public LeaderboardEntryAdapter(Context context, List<LeaderboardEntry> entries) {
    super(context, 0, entries);
    this.entries = entries;
    this.context = context;
  }

  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    // Get leaderboard entry view
    View view = convertView;
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.leaderboard_entry_view, parent, false);
    }

    // get leaderboard data
    LeaderboardEntry entry = entries.get(position);

    // set fields of view
    TextView name = view.findViewById(R.id.leaderboard_entry_text);
    name.setText(entry.getName());

    TextView score = view.findViewById(R.id.leaderboard_entry_score);
    score.setText(entry.getScore() + " " + entry.getScoreSuffix());

    return view;
  }
}
