package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.cmput301w23t09.qrhunter.R;
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

      // Apply photo
      ImageView picture = view.findViewById(R.id.leaderboard_entry_pic);
      if (entry instanceof QRCodeLeaderboardEntry) {
        Glide.with(view)
            .load(((QRCodeLeaderboardEntry) entry).getQRCode().getVisualRepresentationUrl())
            .into(picture);
      } else if (entry instanceof PlayerLeaderboardEntry) {
        Glide.with(view)
            .load(((PlayerLeaderboardEntry) entry).getPlayer().getProfilePicUrl())
            .into(picture);
      } else {
        throw new IllegalArgumentException("Invalid Leaderboard entry type!");
      }
      return view;
    } else {
      throw new UnsupportedOperationException("Unknown leaderboard entry UI element");
    }
  }
}
