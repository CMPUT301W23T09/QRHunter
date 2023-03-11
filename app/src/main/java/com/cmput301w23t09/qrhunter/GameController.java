package com.cmput301w23t09.qrhunter;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.ScannerFragment;

/**
 * The GameController handles controlling the content to be shown onscreen when viewing the
 * GameActivity.
 */
public class GameController {
  private final GameActivity activity;
  private Fragment body;
  private DialogFragment popup;
  private Player activePlayer;

  public GameController(GameActivity activity, Player activePlayer) {
    this.activity = activity;
    this.activePlayer = activePlayer;

    setBody(new ScannerFragment(this));
  }

  /**
   * Retrieve the GameActivity this controller controls.
   *
   * @return GameActivity
   */
  public GameActivity getActivity() {
    return activity;
  }

  /**
   * @return The currently active player, the one playing the game.
   */
  public Player getActivePlayer() {
    return activePlayer;
  }

  /**
   * Retrieve the current body fragment.
   *
   * @return body fragment or null if none is set.
   */
  public Fragment getBody() {
    return body;
  }

  /**
   * Set the current body fragment to another fragment or null to show no fragment.
   *
   * @param fragment fragment to display as the body of the screen.
   */
  public void setBody(Fragment fragment) {
    if (fragment != body) {
      body = fragment;
      getActivity().onControllerBodyUpdate(fragment);
    }
  }

  /**
   * Retrieve the current popup dialog.
   *
   * @return current popup dialog.
   */
  public DialogFragment getPopup() {
    if (popup == null || !popup.isVisible()) {
      return null;
    }

    return popup;
  }

  /**
   * Change the current popup fragment.
   *
   * @param dialog popup dialog to display on screen.
   */
  public void setPopup(DialogFragment dialog) {
    if (getPopup() != dialog) {
      popup = dialog;
      getActivity().onControllerPopupUpdate(dialog);
    }
  }
}
