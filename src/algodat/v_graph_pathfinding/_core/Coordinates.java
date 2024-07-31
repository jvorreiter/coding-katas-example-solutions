package algodat.v_graph_pathfinding._core;

public record Coordinates(double latitude, double longitude) {
    public double getDistanceInMeters(Coordinates other) {
        var phi1 = degreesToRadians(this.latitude);
        var phi2 = degreesToRadians(other.latitude);
        var phiDelta = degreesToRadians(other.latitude - this.latitude);
        var lambdaDelta = degreesToRadians(other.longitude - this.longitude);

        var a = Math.pow(Math.sin(phiDelta / 2), 2)
                + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(lambdaDelta / 2), 2);

        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        var distanceMeters = earthRadiusMeters * c;

        return distanceMeters;
    }

    private static final double earthRadiusMeters = 6371 * 1000;
    private static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
}