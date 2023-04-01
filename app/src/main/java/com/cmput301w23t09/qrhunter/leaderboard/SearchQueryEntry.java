package com.cmput301w23t09.qrhunter.leaderboard;

import java.util.UUID;

/** Represents a leaderboard entry
 *
 * @author Andy Nguyen
 * @version 1.0
 */

public class SearchQueryEntry {
    /** Name to display for this search entry */
    private String name;
    /** Device ID of a player's device */
    private UUID deviceUUID;

    /**
     * Constructor for SearchQueryEntry
     * @param name Name of the player in the search query
     * @param deviceUUID Device ID of the player in the search query
     */
    public SearchQueryEntry(String name, UUID deviceUUID) {
        this.name = name;
        this.deviceUUID = deviceUUID;
    }

    /**
     * Retrieves the name of the search query
     * @return Name of the search query
     */
    public String getName() { return name; }

    /**
     * Retrieves the device ID of the search query
     * @return Device ID of the search query
     */
    public UUID getDeviceId() { return deviceUUID; }
}
