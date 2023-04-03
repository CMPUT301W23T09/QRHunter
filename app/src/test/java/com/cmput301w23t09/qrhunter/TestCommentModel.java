package com.cmput301w23t09.qrhunter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cmput301w23t09.qrhunter.comment.Comment;
import org.junit.jupiter.api.Test;

/** This tests the methods of the Comment class */
public class TestCommentModel {

  /**
   * This creates a mock Comment object
   *
   * @return Return the created mock object
   */
  private Comment mockComment() {
    return new Comment("player_id", "User", "This is a comment");
  }

  /** This tests the getter for the comment string of a Comment */
  @Test
  public void testGetComment() {
    assertEquals(mockComment().getComment(), "This is a comment");
  }

  /** This tests the getter for the player ID of a Comment */
  @Test
  public void testGetPlayerId() {
    Comment comment = mockComment();
    assertEquals(comment.getPlayerId(), "player_id");
  }

  /** This tests the getter for the player username of a Comment */
  @Test
  public void testGetPlayerUsername() {
    Comment comment = mockComment();
    assertEquals(comment.getUsername(), "User");
  }
}
