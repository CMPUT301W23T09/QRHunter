package com.cmput301w23t09.qrhunter.leaderboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.R;


public class PlayerSearchFragment extends BaseFragment {
    private String searchQuery;
    private TextView playerSearchQuery;
    private ImageView backButton;
    private PlayerSearchFragmentController controller;
    private ListView searchQueryList;

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
        controller = new PlayerSearchFragmentController(getGameController());

        setUpSearchFragment(view);
        return view;
    }

    private void setUpSearchFragment(View view) {
        playerSearchQuery = view.findViewById(R.id.search_query);
        backButton = view.findViewById(R.id.back_button);
        searchQueryList = view.findViewById(R.id.search_query_list);
        backButton.setOnClickListener(v -> controller.handleBackButton());
        playerSearchQuery.setText(searchQuery);
    }
}