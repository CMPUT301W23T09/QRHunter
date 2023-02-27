package com.cmput301w23t09.qrhunter.social.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.GameController;
import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.R;

public class LeaderboardFragment extends BaseFragment {

    private final LeaderboardController controller;

    public LeaderboardFragment(GameController gameController) {
        super(gameController);

        controller = new LeaderboardController();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

}
