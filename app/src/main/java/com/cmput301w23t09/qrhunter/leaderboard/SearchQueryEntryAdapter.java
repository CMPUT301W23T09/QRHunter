package com.cmput301w23t09.qrhunter.leaderboard;

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

public class SearchQueryEntryAdapter extends ArrayAdapter<SearchQueryEntry> {
    /** Leaderboard entries to use for this adapter */
    private final List<SearchQueryEntry> entries;

    private final Context context;

    public SearchQueryEntryAdapter(Context context, List<SearchQueryEntry> entries) {
        super(context, 0, entries);
        this.entries = entries;
        this.context = context;
    }

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

        return view;
    }
}
