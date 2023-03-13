package com.cmput301w23t09.qrhunter.profile;

public class QRCodeTest {
    private String hash;
    private int score;

    public QRCodeTest(String hash, int score) {
        this.hash = hash;
        this.score = score;
    }

    public String getHash() {
        return hash;
    }

    public int getScore() {
        return score;
    }
}
