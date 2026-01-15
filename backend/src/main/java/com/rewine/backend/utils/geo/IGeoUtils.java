package com.rewine.backend.utils.geo;

/**
 * Interface for geolocation utilities.
 */
public interface IGeoUtils {

    /**
     * Calculates the distance between two geographic coordinates using the Haversine formula.
     *
     * @param lat1 latitude of the first point in degrees
     * @param lon1 longitude of the first point in degrees
     * @param lat2 latitude of the second point in degrees
     * @param lon2 longitude of the second point in degrees
     * @return distance in kilometers
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    /**
     * Calculates the bounding box coordinates for a given center point and radius.
     *
     * @param latitude  center latitude in degrees
     * @param longitude center longitude in degrees
     * @param radiusKm  radius in kilometers
     * @return BoundingBox with min/max lat/lon
     */
    BoundingBox calculateBoundingBox(double latitude, double longitude, double radiusKm);

    /**
     * Represents a geographic bounding box.
     */
    record BoundingBox(
            double minLatitude,
            double maxLatitude,
            double minLongitude,
            double maxLongitude
    ) {
    }
}

