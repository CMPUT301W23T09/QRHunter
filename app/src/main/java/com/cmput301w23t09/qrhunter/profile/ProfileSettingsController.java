package com.cmput301w23t09.qrhunter.profile;

import com.cmput301w23t09.qrhunter.GameController;

public class ProfileSettingsController {

  private final GameController gameController;

  public ProfileSettingsController(GameController gameController) {
    this.gameController = gameController;
  }

  public void returnToProfile() {
    gameController.setBody(new ProfileFragment(gameController));
  }
}
