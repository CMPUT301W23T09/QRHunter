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

/**
 * Represents fragment displaying the user's search query results
 */

public class PlayerSearchFragment extends BaseFragment {
    /** The user's search query */
    private String searchQuery;
    /** Button to go back to the leaderboards */
    private ImageView backButton;
    /** Controller for the PlayerSearchFragment */
    private PlayerSearchController controller;
    /** ListView container for all the players relating to the user's search query */
    private ListView searchQueryList;
    /** Array adapter for the search entries */
    private SearchQueryEntryAdapter entryAdapter;
    /** List of players relating to the user's search query */
    private List<SearchQueryEntry> searchQueryEntries;
    /** LinearLayout container for all UI elements of the PlayerSearchFragment */
    private LinearLayout searchLinearLayout;

    /**
     * Constructor for the PlayerSearchFragment
     * @param gameController Gamecontroller used to switch between fragments
     */
    public PlayerSearchFragment(GameController gameController) {
        super(gameController);
    }

    /**
     * Method that runs when the view is created
     *
     * @param inflater Used to inflate any views in the fragment
     * @param container Fragment's UI is attached to this parent view if not null
     * @param savedInstanceState Previously saved state that is the fragment is re-constructed from if not null
     *
     * @return The view that will show the search queries
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) { searchQuery = args.getString("search_query"); }
        View view = inflater.inflate(R.layout.fragment_player_search, container, false);

        controller = new PlayerSearchController(getGameController());
        searchQueryEntries = new ArrayList<>();
        entryAdapter = new SearchQueryEntryAdapter(getContext(), searchQueryEntries);
        ((ListView) view.findViewById(R.id.search_query_list)).setAdapter(entryAdapter);

        setUpSearchFragment(view);

        return view;
    }

    /**
     * Sets up the UI elements of the search fragment
     * @param view View for the search fragment
     */
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