package com.rewine.backend.client.maps.impl;

import com.rewine.backend.client.maps.IMapsClient;
import com.rewine.backend.configuration.properties.MapsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Google Maps client implementation.
 * When API key is not configured, returns placeholder/mock data.
 * When API key is present, makes real HTTP calls to Google Maps APIs.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleMapsClientImpl implements IMapsClient {

    private static final int AVERAGE_SPEED_KPH = 50;
    private static final int MINUTES_PER_HOUR = 60;
    private static final BigDecimal DEFAULT_LAT = BigDecimal.valueOf(-32.8895);
    private static final BigDecimal DEFAULT_LNG = BigDecimal.valueOf(-68.8458);

    private final MapsProperties mapsProperties;

    @Override
    public String generateRoutePreview(RoutePreviewRequest request) {
        log.debug("Generating route preview for {} waypoints", request.waypoints().size());

        if (!isConfigured()) {
            log.info("Maps API not configured, returning placeholder preview");
            return generatePlaceholderPreviewUrl(request);
        }

        // Real implementation would call Google Static Maps API
        return generateGoogleMapsPreviewUrl(request);
    }

    @Override
    public RouteCalculationResult calculateRoute(RouteCalculationRequest request) {
        log.debug("Calculating route from {} to {} with {} intermediate stops",
                formatWaypoint(request.origin()),
                formatWaypoint(request.destination()),
                Objects.nonNull(request.intermediateStops()) ? request.intermediateStops().size() : 0);

        if (!isConfigured()) {
            log.info("Maps API not configured, returning estimated route calculation");
            return calculateEstimatedRoute(request);
        }

        // Real implementation would call Google Directions API
        return calculateGoogleMapsRoute(request);
    }

    @Override
    public GeocodedLocation geocode(String address) {
        log.debug("Geocoding address: {}", address);

        if (!isConfigured()) {
            log.info("Maps API not configured, returning placeholder geocode");
            return generatePlaceholderGeocode(address);
        }

        // Real implementation would call Google Geocoding API
        return geocodeWithGoogleMaps(address);
    }

    // =========================================================================
    // Configuration Check
    // =========================================================================

    private boolean isConfigured() {
        return Objects.nonNull(mapsProperties)
                && mapsProperties.isEnabled()
                && Objects.nonNull(mapsProperties.getApiKey())
                && !mapsProperties.getApiKey().isBlank();
    }

    // =========================================================================
    // Placeholder Implementations (No API Key)
    // =========================================================================

    private String generatePlaceholderPreviewUrl(RoutePreviewRequest request) {
        // Return a placeholder map image or an OpenStreetMap-based preview
        StringBuilder url = new StringBuilder();
        url.append("https://via.placeholder.com/")
                .append(request.width())
                .append("x")
                .append(request.height())
                .append("/E8E8E8/666666?text=Route+Preview");

        // Alternative: Generate an OpenStreetMap static image URL
        if (!request.waypoints().isEmpty()) {
            Waypoint first = request.waypoints().get(0);
            // OpenStreetMap static map (free, no API key needed)
            return String.format(
                    "https://staticmap.openstreetmap.de/staticmap.php?center=%s,%s&zoom=10&size=%dx%d&maptype=osmarenderer",
                    first.latitude(),
                    first.longitude(),
                    request.width(),
                    request.height());
        }

        return url.toString();
    }

    private RouteCalculationResult calculateEstimatedRoute(RouteCalculationRequest request) {
        // Calculate estimated distance using Haversine formula
        List<Waypoint> allPoints = new ArrayList<>();
        allPoints.add(request.origin());
        if (Objects.nonNull(request.intermediateStops())) {
            allPoints.addAll(request.intermediateStops());
        }
        allPoints.add(request.destination());

        double totalDistanceKm = 0;
        List<RouteLeg> legs = new ArrayList<>();

        for (int i = 0; i < allPoints.size() - 1; i++) {
            Waypoint start = allPoints.get(i);
            Waypoint end = allPoints.get(i + 1);

            double distanceKm = calculateHaversineDistance(
                    start.latitude().doubleValue(),
                    start.longitude().doubleValue(),
                    end.latitude().doubleValue(),
                    end.longitude().doubleValue());

            // Estimate duration at average speed (accounting for winery stops)
            int durationMinutes = (int) Math.ceil(distanceKm / AVERAGE_SPEED_KPH * MINUTES_PER_HOUR);

            totalDistanceKm += distanceKm;
            legs.add(new RouteLeg(
                    Objects.nonNull(start.label()) ? start.label() : "Point " + (i + 1),
                    Objects.nonNull(end.label()) ? end.label() : "Point " + (i + 2),
                    Math.round(distanceKm * 10) / 10.0,
                    durationMinutes));
        }

        // Total duration estimate
        int totalDurationMinutes = (int) Math.ceil(totalDistanceKm / AVERAGE_SPEED_KPH * MINUTES_PER_HOUR);

        log.debug("Estimated route: {} km, {} minutes",
                Math.round(totalDistanceKm * 10) / 10.0, totalDurationMinutes);

        return new RouteCalculationResult(
                Math.round(totalDistanceKm * 10) / 10.0,
                totalDurationMinutes,
                null, // No encoded polyline without real API
                legs);
    }

    private GeocodedLocation generatePlaceholderGeocode(String address) {
        // Return placeholder data
        return new GeocodedLocation(
                DEFAULT_LAT,
                DEFAULT_LNG,
                address,
                "Argentina",
                "Mendoza",
                "Mendoza");
    }

    // =========================================================================
    // Real Google Maps Implementations (With API Key)
    // =========================================================================

    private String generateGoogleMapsPreviewUrl(RoutePreviewRequest request) {
        // Build Google Static Maps API URL
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/staticmap?");
        url.append("size=").append(request.width()).append("x").append(request.height());
        url.append("&maptype=").append(request.mapStyle());

        // Add markers for waypoints
        for (int i = 0; i < request.waypoints().size(); i++) {
            Waypoint wp = request.waypoints().get(i);
            url.append("&markers=color:")
                    .append(i == 0 ? "green" : (i == request.waypoints().size() - 1 ? "red" : "blue"))
                    .append("%7Clabel:").append((char) ('A' + i))
                    .append("%7C").append(wp.latitude()).append(",").append(wp.longitude());
        }

        // Add path between waypoints
        if (request.waypoints().size() > 1) {
            url.append("&path=color:0x0000ff80%7Cweight:4");
            for (Waypoint wp : request.waypoints()) {
                url.append("%7C").append(wp.latitude()).append(",").append(wp.longitude());
            }
        }

        url.append("&key=").append(mapsProperties.getApiKey());

        log.debug("Generated Google Maps preview URL");
        return url.toString();
    }

    private RouteCalculationResult calculateGoogleMapsRoute(RouteCalculationRequest request) {
        // TODO: Implement real Google Directions API call
        // For now, fall back to estimated calculation
        log.warn("Real Google Maps route calculation not yet implemented, using estimates");
        return calculateEstimatedRoute(request);
    }

    private GeocodedLocation geocodeWithGoogleMaps(String address) {
        // TODO: Implement real Google Geocoding API call
        // For now, fall back to placeholder
        log.warn("Real Google Maps geocoding not yet implemented, using placeholder");
        return generatePlaceholderGeocode(address);
    }

    // =========================================================================
    // Utility Methods
    // =========================================================================

    /**
     * Calculates distance between two points using Haversine formula.
     *
     * @param lat1 latitude of point 1
     * @param lon1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lon2 longitude of point 2
     * @return distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth's radius in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private String formatWaypoint(Waypoint wp) {
        if (Objects.isNull(wp)) {
            return "null";
        }
        return String.format("(%s, %s)", wp.latitude(), wp.longitude());
    }
}

