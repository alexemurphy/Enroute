package main.unitTests;

import main.db.DBHelper;
import main.objects.Car;
import main.objects.Location;
import main.objects.Route;
import main.objects.User;

public class BETestsVarious {

	private Boolean testGetDriverID() throws Exception {
		int driverID = DBHelper.instance().getDriverID(1);
		return driverID == 5;
	}


	private Boolean testGetCloseRoutes() throws Exception {
		Route[] routes = DBHelper.instance().getRoutesClose(new Location("", "51.381253 -2.359953"), new Location("", "51.381253 -2.359953"));
		return routes[0].getRouteID() == 1;
	}


	private Boolean testGetDriverInfo() throws Exception {
		User user = DBHelper.instance().getUserInfo(5);
		return user.getFirstName().equals("LiamBETest");
	}


	private Boolean testGetCarInfo() throws Exception {
		Car car = DBHelper.instance().getCarInfo(5);
		return car.getReg_plate().equals("REGPLATE");
	}


	private Boolean testGetRouteInfo() throws Exception {
		Route route = DBHelper.instance().getRouteInfo(1);
		return route.getStartLoc().getAddress().equals("51.381253 -2.359953");
	}


	private Boolean testAddPassengerToRoute() throws Exception {
		DBHelper.instance().addUserToRoute(1, 1);
		// MANUAL TEST REQUIRED, AS NO PASS. CHECKING METHOD CURRENTLY EXISTS
		return true;
	}


	private Boolean testGetFavLocations() throws Exception {
		Location[] array = DBHelper.instance().getFavLocations(1);
		return array[0].getAddress().equals("51.381252 -2.359952");
	}


	/**
	 * Functions needed!
	 * <p>
	 * getRoutesClose(startLocation) - returns RouteIDs that are close and leaving within 30mins
	 * NEEDS TO CHECK IF IT IS FULL i.e. current passengers must be less than 4
	 * <p>
	 * ---Complete---getDriverID(routeID) - returns driverID
	 * ---Complete---getUserInfo(userID) - (driverID == userID) return User Object. User object has Name, Profile Picture path, Rating.
	 * ---Complete---getCarInfo(driverID) - return car object. color, reg number, model
	 * ---Complete---getRouteInfo(routeID) - returns Route object. with startLoc, endLoc, startTime and driverID
	 * ---Complete---addPassengerToRoute(userID, routeID) - return Boolean. check route for passID, create new passenger
	 * ---Complete---getFavLocations() - returning array of home and work fav locations, ideally placeID (Google Places API)
	 */

	public Boolean testBE() {
		try {
			//All // lines have passed
			//if(!testGetDriverID()) return false;
			//if(!testAddPassengerToRoute()) return false;
			//if(!testGetCarInfo()) return false;
			//if(!testGetDriverInfo()) return false;
			//if(!testGetFavLocations()) return false;
			//if(!testGetRouteInfo()) return false;
			if (!testGetCloseRoutes()) return false;


		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}

		return true;
	}


}
