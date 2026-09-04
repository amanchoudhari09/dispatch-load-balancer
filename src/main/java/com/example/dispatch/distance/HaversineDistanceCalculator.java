package com.example.dispatch.distance;

import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator implements DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0088;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MAX_LONGITUDE = 180.0;

    @Override
    public double calculateDistance(double latitude1, double longitude1,
                                    double latitude2, double longitude2) {
        validateCoordinates(latitude1, longitude1);
        validateCoordinates(latitude2, longitude2);

        double latitudeDelta = Math.toRadians(latitude2 - latitude1);
        double longitudeDelta = Math.toRadians(longitude2 - longitude1);
        double firstTerm = Math.pow(Math.sin(latitudeDelta / 2), 2);
        double secondTerm = Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(longitudeDelta / 2), 2);
        double haversine = Math.min(1.0, firstTerm + secondTerm);
        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -MAX_LATITUDE || latitude > MAX_LATITUDE
                || longitude < -MAX_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException("Coordinates must be finite and within valid latitude/longitude ranges");
        }
    }
}
