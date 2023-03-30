package com.cmput301w23t09.qrhunter.leaderboard;

import java.util.UUID;

public class SearchQueryEntry {
    private String name;
    private UUID deviceUUID;

    public SearchQueryEntry(String name, UUID deviceUUID) {
        this.name = name;
        this.deviceUUID = deviceUUID;
    }

    public String getName() { return name; }

    public UUID getDeviceId() { return deviceUUID; }
}
