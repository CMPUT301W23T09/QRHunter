package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;

import java.util.ArrayList;
import java.util.List;


public class PlayerSearchFragment extends BaseFragment {
    private String searchQuery;
    private ImageView backButton;
    private PlayerSearchController controller;
    private ListView searchQueryList;
    private SearchQueryEntryAdapter entryAdapter;
    private List<SearchQueryEntry> searchQueryEntries;
    private LinearLayout searchLinearLayout;

    public PlayerSearchFragment(GameController gameController) {
        super(gameController);
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) { searchQuery = args.getString("search_query"); }
        View view = inflater.inflate(R.layout.fragment_player_search, container, false);

        controller = new PlayerSearchController(getGameController());
        searchQueryEntries = new ArrayList<>();
        entryAdapter = new SearchQueryEntryAdapter(getContext(), searchQueryEntries);
        ((ListView) view.findViewById(R.id.search_query_list)).setAdapter(entryAdapter);

//        searchQueryEntries.add(new SearchQueryEntry("Joe"));
//        searchQueryEntries.add(new SearchQueryEntry("Hank"));
//        searchQueryEntries.add(new SearchQueryEntry("Bob"));
//        entryAdapter.notifyDataSetChanged();

        setUpSearchFragment(view);

        return view;
    }

    private void setUpSearchFragment(View view) {
        backButton = view.findViewById(R.id.back_button);
        searchQueryList = view.findViewById(R.id.search_query_list);
        searchLinearLayout = view.findViewById(R.id.search_linear_layout);

        backButton.setOnClickListener(v -> controller.handleBackButton());
        searchQueryList.setOnItemClickListener((parent, v, position, id) -> {
            SearchQueryEntry entry = searchQueryEntries.get(position);
            controller.handleSearchQueryListClick(entry);
        });

        controller.displaySearchQueryData(searchQuery, searchQueryEntries, entryAdapter, searchLinearLayout, getContext());
    }


}