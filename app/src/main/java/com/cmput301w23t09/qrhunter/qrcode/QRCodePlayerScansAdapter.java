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

/** Adapter that handles displaying QRCode player scans */
public class QRCodePlayerScansAdapter extends ArrayAdapter<QRPlayerScanEntry> {

  private final Context context;

  public QRCodePlayerScansAdapter(Context context) {
    super(context, 0);

    this.context = context;
  }

  @NonNull @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    // Get leaderboard entry view
    View view = convertView;
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.qrcode_player_view, parent, false);
    }

    // get player data
    QRPlayerScanEntry entry = getItem(position);

    // set fields of view
    TextView name = view.findViewById(R.id.qrcode_player_scan_name);
    name.setText(entry.getPlayer().getUsername());

    TextView score = view.findViewById(R.id.player_comment_input);
    score.setText(entry.getScore() + " PTS");

    return view;
  }
}
