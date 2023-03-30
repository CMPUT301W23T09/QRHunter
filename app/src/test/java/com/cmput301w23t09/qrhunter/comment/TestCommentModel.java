package com.cmput301w23t09.qrhunter.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestCommentModel {

  // create a mock comment
  private Comment mockComment() {
    return new Comment("player_id", "User", "This is a comment");
  }

  // test getting the comment from a comment
  @Test
  public void testGetComment() {
    assertEquals(mockComment().getComment(), "This is a comment");
  }

  // test getting the player from a comment
  @Test
  public void testGetPlayerId() {
    Comment comment = mockComment();
    assertEquals(comment.getPlayerId(), "player_id");
  }

  @Test
  public void testGetPlayerUsername() {
    Comment comment = mockComment();
    assertEquals(comment.getUsername(), "User");
  }
}
