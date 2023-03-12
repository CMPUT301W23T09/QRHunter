package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.player.Player;

/** Base Fragment class with utility fragment methods. */
public abstract class BaseFragment extends Fragment {
  private final GameController gameController;

  public BaseFragment(GameController gameController) {
    this.gameController = gameController;
  }

  /**
   * Retrieve the GameController.
   *
   * @return GameController
   */
  public GameController getGameController() {
    return gameController;
  }

  /**
   * @return The currently active player, the one playing the game.
   */
  public Player getActivePlayer() {
    return gameController.getActivePlayer();
  }
}
