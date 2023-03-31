package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** ArrayAdapter for leaderboard entries */
public class LeaderboardEntryAdapter extends ArrayAdapter<LeaderboardEntry> {
  /** Leaderboard entries to use for this adapter */
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

    ImageView picture = view.findViewById(R.id.leaderboard_entry_pic);
    try {
      if (entry instanceof QRCodeLeaderboardEntry) {
        picture.setImageBitmap(
            ((QRCodeLeaderboardEntry) entry).getQRCode().getVisualRepresentation());
      } else if (entry instanceof PlayerLeaderboardEntry) {
        picture.setImageBitmap(((PlayerLeaderboardEntry) entry).getPlayer().getProfilePic());
      } else {
        throw new IllegalArgumentException("Invalid Leaderboard entry type!");
      }
    } catch (ExecutionException | InterruptedException e) {
      Log.e("LeaderboardEntryAdapter", "An exception occurred while fetching image", e);
      Toast.makeText(context, "An exception occurred while fetching image...", Toast.LENGTH_LONG)
          .show();
    }

    return view;
  }
}
