package com.example.dispatch.distance;

import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator implements DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0088;
    @Override public double calculateDistance(double lat1,double lon1,double lat2,double lon2) {
        validate(lat1, lon1); validate(lat2, lon2);
        double dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1);
        double a=Math.pow(Math.sin(dLat/2),2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.pow(Math.sin(dLon/2),2);
        return EARTH_RADIUS_KM*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
    }
    private void validate(double lat,double lon) { if(lat < -90 || lat > 90 || lon < -180 || lon > 180) throw new IllegalArgumentException("Coordinates are outside valid ranges"); }
}
