package com.cmput301w23t09.qrhunter.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.cmput301w23t09.qrhunter.R;
import java.util.List;

/**
 * ArrayAdapter for search query entries
 *
 * @author Andy Nguyen
 * @version 1.0
 */
public class SearchQueryEntryAdapter extends ArrayAdapter<SearchQueryEntry> {
  /** Search query entries to use for this adapter */
  private final List<SearchQueryEntry> entries;
  /** Context for the search fragment */
  private final Context context;

  /**
   * Constructor for the SearchQueryEntryAdapter
   *
   * @param context Context for the saerch fragment
   * @param entries Search query entries to use for this adapter
   */
  public SearchQueryEntryAdapter(Context context, List<SearchQueryEntry> entries) {
    super(context, 0, entries);
    this.entries = entries;
    this.context = context;
  }

  /**
   * Gets the view that represents the search query entry at a certain position in the list
   *
   * @param position Position of the search entry
   * @param convertView Potential view the adapter can reuse
   * @param parent Parent of convertView
   * @return View that represents the search query entry
   */
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.search_query_entry_view, parent, false);
    }

    SearchQueryEntry entry = entries.get(position);

    // set fields of view
    TextView name = view.findViewById(R.id.search_query_entry_text);
    name.setText(entry.getName());
    ImageView picture = view.findViewById(R.id.search_query_image);
    Glide.with(view).load(entry.getPlayer().getProfilePicUrl()).into(picture);

    return view;
  }
}
