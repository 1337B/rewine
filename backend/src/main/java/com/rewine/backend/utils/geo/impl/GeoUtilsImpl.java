package com.rewine.backend.utils.geo.impl;

import com.rewine.backend.utils.geo.IGeoUtils;
import org.springframework.stereotype.Component;

/**
 * Implementation of geolocation utilities using the Haversine formula.
 */
@Component
public class GeoUtilsImpl implements IGeoUtils {

    /**
     * Earth's radius in kilometers.
     */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Degrees to radians conversion factor.
     */
    private static final double DEG_TO_RAD = Math.PI / 180.0;

    /**
     * Latitude boundary constants.
     */
    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;

    /**
     * Longitude boundary constants.
     */
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    /**
     * Full circle in degrees for longitude normalization.
     */
    private static final double FULL_CIRCLE_DEGREES = 360.0;

    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = lat1 * DEG_TO_RAD;
        double lat2Rad = lat2 * DEG_TO_RAD;
        double deltaLat = (lat2 - lat1) * DEG_TO_RAD;
        double deltaLon = (lon2 - lon1) * DEG_TO_RAD;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public BoundingBox calculateBoundingBox(double latitude, double longitude, double radiusKm) {
        // Angular distance in radians on a great circle
        double angularDistance = radiusKm / EARTH_RADIUS_KM;

        double latRad = latitude * DEG_TO_RAD;
        double lonRad = longitude * DEG_TO_RAD;

        // Latitude bounds are simple
        double minLatRad = latRad - angularDistance;
        double maxLatRad = latRad + angularDistance;

        // Longitude bounds need to account for latitude
        double deltaLon = Math.asin(Math.sin(angularDistance) / Math.cos(latRad));
        double minLonRad = lonRad - deltaLon;
        double maxLonRad = lonRad + deltaLon;

        // Convert back to degrees and normalize in one step
        double minLat = normalizeLatitude(minLatRad / DEG_TO_RAD);
        double maxLat = normalizeLatitude(maxLatRad / DEG_TO_RAD);
        double minLon = normalizeLongitude(minLonRad / DEG_TO_RAD);
        double maxLon = normalizeLongitude(maxLonRad / DEG_TO_RAD);

        return new BoundingBox(minLat, maxLat, minLon, maxLon);
    }

    /**
     * Normalizes latitude to be within valid bounds.
     *
     * @param latitude the latitude to normalize
     * @return normalized latitude between -90 and 90
     */
    private double normalizeLatitude(double latitude) {
        if (latitude < MIN_LATITUDE) {
            return MIN_LATITUDE;
        }
        if (latitude > MAX_LATITUDE) {
            return MAX_LATITUDE;
        }
        return latitude;
    }

    /**
     * Normalizes longitude to be within valid bounds.
     *
     * @param longitude the longitude to normalize
     * @return normalized longitude between -180 and 180
     */
    private double normalizeLongitude(double longitude) {
        if (longitude < MIN_LONGITUDE) {
            return longitude + FULL_CIRCLE_DEGREES;
        }
        if (longitude > MAX_LONGITUDE) {
            return longitude - FULL_CIRCLE_DEGREES;
        }
        return longitude;
    }
}

