package com.cmput301w23t09.qrhunter.qrcode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import java.util.List;

public class QRCodePlayerScansAdapter extends ArrayAdapter<QRCodePlayerScansAdapter.Entry> {

  private final Context context;
  private final List<Entry> entries;

  public QRCodePlayerScansAdapter(Context context, List<Entry> entries) {
    super(context, 0);

    this.context = context;
    this.entries = entries;
  }

  @NonNull @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    // Get leaderboard entry view
    View view = convertView;
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.qrcode_player_view, parent, false);
    }

    // get player data
    Entry entry = entries.get(position);

    // set fields of view
    TextView name = view.findViewById(R.id.qrcode_player_scan_name);
    name.setText(entry.getPlayer().getUsername());

    TextView score = view.findViewById(R.id.qrcode_player_scan_points);
    score.setText(entry.getScore() + " PTS");

    System.out.println(entry.getPlayer().getUsername());
    return view;
  }

  public static class Entry {

    private final Player player;
    private final int score;

    public Entry(Player player, int score) {
      this.player = player;
      this.score = score;
    }

    public Player getPlayer() {
      return player;
    }

    public int getScore() {
      return score;
    }
  }
}
