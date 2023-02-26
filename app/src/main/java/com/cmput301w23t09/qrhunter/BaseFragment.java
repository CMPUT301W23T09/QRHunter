package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.Fragment;

/**
 * Base Fragment class with utility fragment methods.
 */
public abstract class BaseFragment extends Fragment {
    private final GameController gameController;

    public BaseFragment(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Retrieve the MainController.
     * @return MainController
     */
    public GameController getMainController() {
        return gameController;
    }

}
