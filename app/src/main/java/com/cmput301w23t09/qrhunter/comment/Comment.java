package com.cmput301w23t09.qrhunter.comment;

/** This is a class that stores a comment that a player made */
public class Comment {
  /** This is the comment made by the player */
  private final String comment;
  /** This is the username of the player that made the comment. */
  private final String username;
  /** This is the player that made the comment */
  private final String playerId;

  /**
   * This initializes a Comment with a comment and the player who made it
   *
   * @param comment This is the comment made by the player
   */
  public Comment(String playerId, String username, String comment) {
    this.comment = comment;
    this.playerId = playerId;
    this.username = username;
  }

  /**
   * This returns the comment attribute
   *
   * @return Return the comment made by the player
   */
  public String getComment() {
    return comment;
  }

  /**
   * This returns the player ID attribute
   *
   * @return Return the ID of the player that made the comment
   */
  public String getPlayerId() {
    return playerId;
  }

  /**
   * This returns the player username attribute
   *
   * @return Return the username of player that made the comment
   */
  public String getUsername() {
    return username;
  }
}
