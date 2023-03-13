package com.cmput301w23t09.qrhunter.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cmput301w23t09.qrhunter.player.Player;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TestCommentModel {
  // create a mock player
  private Player mockPlayer() {
    UUID mockUUID = UUID.randomUUID();
    return new Player(
        mockUUID, "Username", "587-998-1206", "mock-email@gmail.com", new ArrayList<String>());
  }
  // create a mock comment
  private Comment mockComment() {
    return new Comment("This is a comment", mockPlayer());
  }

  // test getting the comment from a comment
  @Test
  public void testGetComment() {
    assertEquals(mockComment().getComment(), "This is a comment");
  }

  // test getting the player from a comment
  @Test
  public void testGetPlayer() {
    Player player =
        new Player(
            UUID.randomUUID(),
            "Username",
            "587-998-1206",
            "mock-email@gmail.com",
            new ArrayList<>());
    Comment comment = new Comment("Test", player);
    assertEquals(comment.getPlayer(), player);
  }

  // test setting the comment of comment
  @Test
  public void testSetComment() {
    Comment comment = mockComment();
    comment.setComment("New Comment");
    assertEquals(comment.getComment(), "New Comment");
  }
}
