package utils;

import main.objects.Location;

public class CoordinateHandling {

	public static int calcDistance(Location one, Location two) {
		Double lat1 = one.getLatitude();
		Double lat2 = two.getLatitude();
		Double lon1 = one.getLongitude();
		Double lon2 = two.getLongitude();
		Double dist = distance(lat1, lat2, lon1, lon2);
		return dist.intValue();
	}


	/**
	 * Method from : https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
	 */
	public static double distance(double lat1, double lat2, double lon1, double lon2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		// Height is ignored
		double height = 0;
		distance = Math.pow(distance, 2) + Math.pow(height, 2);
		return Math.sqrt(distance);
	}

}
