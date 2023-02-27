package com.cmput301w23t09.qrhunter.map;

/**
 * This is a class that stores the location coordinates of a location
 */
public class Location {
    /**
     * This is the latitude coordinate of the location
     */
    private float latitude;
    /**
     * This is the longitude coordinate of the location
     */
    private float longitude;

    /**
     * This initializes a Location with its location coordinates
     * @param latitude
     * This is the latitude coordinate of the location
     * @param longitude
     * This is the longitude coordinate of the location
     */
    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * This returns the latitude coordinate of the location
     * @return
     * Return the latitude coordinate of the location
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * This returns the longitude coordinate of the location
     * @return
     * Return the longitude coordinate of the location
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * This sets the latitude coordinate of the location
     * @param latitude
     * This is the latitude coordinate to set the location to
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * This sets the longitude coordinate of the location
     * @param longitude
     * This is the longitude coordinate to set the location to
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
