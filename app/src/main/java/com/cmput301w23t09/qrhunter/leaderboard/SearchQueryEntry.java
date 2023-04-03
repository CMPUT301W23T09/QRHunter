package com.cmput301w23t09.qrhunter.leaderboard;

import com.cmput301w23t09.qrhunter.player.Player;
import java.util.UUID;

/**
 * Represents a search query entry
 *
 * @author Andy Nguyen
 * @version 1.0
 */
public class SearchQueryEntry {
  /** Name to display for this search entry */
  private String name;
  /** Device ID of a player's device */
  private UUID deviceUUID;
  /** Player associated with a search query */
  private Player player;

  /**
   * Constructor for SearchQueryEntry
   *
   * @param name Name of the player in the search query
   * @param deviceUUID Device ID of the player in the search query
   * @param player Player of the search query
   */
  public SearchQueryEntry(String name, UUID deviceUUID, Player player) {
    this.name = name;
    this.deviceUUID = deviceUUID;
    this.player = player;
  }

  /**
   * Retrieves the name of the search query
   *
   * @return Name of the search query
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the device ID of the search query
   *
   * @return Device ID of the search query
   */
  public UUID getDeviceId() {
    return deviceUUID;
  }

  /**
   * Retireve the player of the search query
   *
   * @return Player of the search query
   */
  public Player getPlayer() {
    return player;
  }
}
