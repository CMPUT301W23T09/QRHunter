package com.cmput301w23t09.qrhunter.qrcode;

import com.cmput301w23t09.qrhunter.player.Player;

/**
 * This is a class that stores a comment that a player made
 */
public class Comment {
    /**
     * This is the comment made by the player
     */
    private String comment;
    /**
     * This is the player that made the comment
     */
    private Player player;

    /**
     * This initializes a Comment with a comment and the player who made it
     * @param comment
     * This is the comment made by the player
     * @param player
     * This is the player that made the comment
     */
    public Comment(String comment, Player player) {
        this.comment = comment;
        this.player = player;
    }

    /**
     * This returns the comment attribute
     * @return
     * Return the comment made by the player
     */
    public String getComment() {
        return comment;
    }

    /**
     * This returns the player attribute
     * @return
     * Return the player that made the comment
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * This sets the comment attribute
     * @param comment
     * This is the string comment to set the comment attribute to
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
