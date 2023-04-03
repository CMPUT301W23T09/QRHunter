package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.util.Log;
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

    // What kind of entry do we need to render?
    if (entry instanceof LeaderboardEntryTitle) {
      // Title entry
      return ((LeaderboardEntryTitle) entry)
          .getView(context, convertView, parent, (LeaderboardEntryTitle) entry);
    } else if (entry instanceof LeaderboardEntry) {
      // Render the leaderboard entry and then apply the score, text, and photo
      View view =
          ((LeaderboardEntry) entry)
              .getView(context, convertView, parent, (LeaderboardEntry) entry);

      // set fields of view
      TextView name = view.findViewById(R.id.leaderboard_entry_text);
      name.setText(((LeaderboardEntry) entry).getName());

      TextView score = view.findViewById(R.id.leaderboard_entry_score);
      score.setText(
          ((LeaderboardEntry) entry).getScore()
              + " "
              + ((LeaderboardEntry) entry).getScoreSuffix());

      // Apply photo
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
    } else {
      throw new UnsupportedOperationException("Unknown leaderboard entry UI element");
    }
  }
}
