package com.rewine.backend.client.maps;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for maps client operations.
 * Provides route visualization and distance calculations.
 */
public interface IMapsClient {

    int DEFAULT_MAP_WIDTH = 640;
    int DEFAULT_MAP_HEIGHT = 400;

    /**
     * Generates a static map preview image URL for a route.
     *
     * @param request the map preview request
     * @return URL to the static map image
     */
    String generateRoutePreview(RoutePreviewRequest request);

    /**
     * Calculates the total distance and estimated duration for a route.
     *
     * @param request the route calculation request
     * @return the route calculation result
     */
    RouteCalculationResult calculateRoute(RouteCalculationRequest request);

    /**
     * Geocodes an address to coordinates.
     *
     * @param address the address to geocode
     * @return the geocoded location
     */
    GeocodedLocation geocode(String address);

    /**
     * Request for generating a route preview.
     */
    record RoutePreviewRequest(
            List<Waypoint> waypoints,
            int width,
            int height,
            String mapStyle
    ) {
        public RoutePreviewRequest(List<Waypoint> waypoints) {
            this(waypoints, DEFAULT_MAP_WIDTH, DEFAULT_MAP_HEIGHT, "roadmap");
        }
    }

    /**
     * A waypoint on a route.
     */
    record Waypoint(
            BigDecimal latitude,
            BigDecimal longitude,
            String label
    ) { }

    /**
     * Request for route calculation.
     */
    record RouteCalculationRequest(
            Waypoint origin,
            Waypoint destination,
            List<Waypoint> intermediateStops
    ) { }

    /**
     * Result of a route calculation.
     */
    record RouteCalculationResult(
            double totalDistanceKm,
            int totalDurationMinutes,
            String encodedPolyline,
            List<RouteLeg> legs
    ) { }

    /**
     * A leg of the route between two waypoints.
     */
    record RouteLeg(
            String startAddress,
            String endAddress,
            double distanceKm,
            int durationMinutes
    ) { }

    /**
     * A geocoded location.
     */
    record GeocodedLocation(
            BigDecimal latitude,
            BigDecimal longitude,
            String formattedAddress,
            String country,
            String region,
            String city
    ) { }
}

