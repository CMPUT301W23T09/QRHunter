package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.Fragment;

/**
 * Base Fragment class with utility fragment methods.
 */
public abstract class BaseFragment extends Fragment {
    private final MainController mainController;

    public BaseFragment(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Retrieve the MainController.
     * @return MainController
     */
    public MainController getMainController() {
        return mainController;
    }

}
