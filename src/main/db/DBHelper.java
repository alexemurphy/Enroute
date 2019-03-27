package main.db;

import main.objects.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.CoordinateHandling;
import utils.MailHandler;
import utils.MessageDataStore;
import utils.ResourceUtils;

import javax.xml.transform.Result;
import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBHelper implements Closeable {

	private static DBHelper instance;
	private Connection connection;


	private DBHelper() throws Exception {
		String[] details = getLoginInfo();
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + details[0] + "/" + details[1], details[2], details[3]);
	}


	public static DBHelper instance() throws Exception {
		if (instance == null) instance = new DBHelper();
		return instance;
	}


	private static String[] getLoginInfo() throws IOException {
		String[] data = new String[4];
		List<String> lines = ResourceUtils.getLines("mysqlconnection.txt");
		if (lines.size() < 4) throw new IOException("Invalid login Info");
		for (int i = 0; i < 4; i++) data[i] = lines.get(i);
		return data;
	}


	private static java.sql.Date getSQLDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}


	public Boolean addFavLocation(int userID, Location location) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO FAV_LOCS VALUES(?,?,?);");
			preparedStatement.setInt(1, userID);
			preparedStatement.setString(2, location.getName());
			preparedStatement.setString(3, location.getAddress());
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public void addMessageToDB(int userId, int routeID, String message) {
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO MESSENGER (MESSAGE, USER_ID, ROUTE_ID, TIMESTAMP) VALUES(?,?,?,CURRENT_TIMESTAMP);");
			preparedStatement.setString(1, message);
			preparedStatement.setInt(2, userId);
			preparedStatement.setInt(3, routeID);
			preparedStatement.execute();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}


	public int addRoute(Route route) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT COUNT('PASS_ID') FROM PASSENGERS;");
			results = preparedStatement.executeQuery();
			results.next();
			int num = results.getInt(1);
			preparedStatement = connection.prepareStatement("INSERT INTO PASSENGERS VALUES (?,?);");
			preparedStatement.setInt(1, num);
			preparedStatement.setInt(2, route.getDriverID());
			preparedStatement.execute();
			preparedStatement = connection.prepareStatement("INSERT INTO ROUTES(DRIVER_ID, START_LOC, START_TIME, END_LOC, END_TIME, PASS_ID) VALUES (?,?,?,?,?,?);");
			preparedStatement.setInt(1, route.getDriverID());
			preparedStatement.setString(2, route.getStartLoc().getAddress());
			preparedStatement.setTimestamp(3, new java.sql.Timestamp(route.getStartTime().getTime()));
			preparedStatement.setString(4, route.getEndLoc().getAddress());
			preparedStatement.setTimestamp(5, new java.sql.Timestamp(route.getStartTime().getTime()));
			preparedStatement.setInt(6, num);
			preparedStatement.execute();
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES ORDER BY ROUTE_ID DESC;");
			results = preparedStatement.executeQuery();
			results.next();
			return results.getInt("ROUTE_ID");
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return -1;
	}


	public Boolean addUserToRoute(int userID, int routeID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT PASS_ID FROM ROUTES WHERE ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			results.next();
			int passID = results.getInt("PASS_ID");
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES R, PASSENGERS P WHERE P.PASS_ID = R.PASS_ID AND R.ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			int num = 0;
			while (results.next()) num++;
			if (num >= 4) return false;
			preparedStatement = connection.prepareStatement("INSERT INTO PASSENGERS VALUES(?,?);");
			preparedStatement.setInt(1, passID);
			preparedStatement.setInt(2, userID);
			preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public void cancelRoute(int routeID) {
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			// Email Driver that they have cancelled a route
			preparedStatement = connection.prepareStatement("SELECT DRIVER_ID, PASS_ID FROM ROUTES WHERE ROUTE_ID=?;");
			preparedStatement.setInt(1, routeID);
			ResultSet results = preparedStatement.executeQuery();
			results.next();
			sendDriverCancellationEmail(getEmailFromID(results.getInt("DRIVER_ID")));
			int passID = results.getInt("PASS_ID");
			// Email passengers that their route it cancelled
			preparedStatement = connection.prepareStatement("SELECT USER_ID FROM ROUTES, PASSENGERS WHERE " +
					"ROUTES.ROUTE_ID = ? AND ROUTES.PASS_ID = PASSENGERS.PASS_ID AND " +
					"PASSENGERS.USER_ID <> ROUTES.DRIVER_ID;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			while (results.next()) sendPassCancellationEmail(getEmailFromID(results.getInt("USER_ID")));
			// Delete route from DB
			preparedStatement = connection.prepareStatement("DELETE FROM ROUTES WHERE ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			preparedStatement.execute();
			preparedStatement = connection.prepareStatement("DELETE FROM PASSENGERS WHERE PASS_ID = ?;");
			preparedStatement.setInt(1, passID);
			preparedStatement.execute();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}


	public Boolean isDriver(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT DORP FROM USERS WHERE USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			if (results.next()) if (results.getInt("DORP") == 1) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public Boolean checkForRouteCancellation(int routeID) {
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			// Email Driver that they have cancelled a route
			preparedStatement = connection.prepareStatement("SELECT ROUTE_ID FROM ROUTES WHERE ROUTE_ID=?;");
			preparedStatement.setInt(1, routeID);
			ResultSet results = preparedStatement.executeQuery();
			return results.next();
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return false;
	}


	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void depart(int routeID) {
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO TRIP_HISTORY SELECT * FROM ROUTES WHERE " +
					"ROUTES.ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			preparedStatement.execute();
			preparedStatement = connection.prepareStatement("DELETE FROM ROUTES WHERE ROUTES.ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			preparedStatement.execute();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}


	public void emailDriverPassCancelled(int userID) {
		String email = getEmailFromID(userID);
		try {
			MailHandler mailHandler = new MailHandler();
			mailHandler.sendEmail(email, "Passenger Cancelled", "A passenger on your route has " +
					"cancelled, apologies for the inconvenience.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Car getCarInfo(int driverID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM CARS WHERE USER_ID = ?;");
			preparedStatement.setInt(1, driverID);
			results = preparedStatement.executeQuery();
			if (results.next())
				return new Car(results.getString("MAKE"), results.getString("MODEL"), results.getString("COLOUR"), results.getString("REG_PLATE"), driverID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getChosenDriverMarker(int routeID) {
		JSONArray jsonArray = new JSONArray();
		Route route = getRouteInfo(routeID);
		String picture = "/images/" + Integer.toString(getProfilePicID(route.getDriverID()));
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("picture", picture);
			jsonObject.put("latitude", route.getStartLoc().getLatitude());
			jsonObject.put("longitude", route.getStartLoc().getLongitude());
			jsonArray.put(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray.toString();
	}


	public Connection getConnection() {
		return connection;
	}


	/**
	 * Get a list of drivers who have driven the user
	 *
	 * @param userID User ID
	 * @return List of historySegments containing user and route data for the past routes
	 */
	public ArrayList<HistorySegment> getDriverHistory(int userID) {
		ArrayList<HistorySegment> historySegments = new ArrayList<>();
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM (SELECT T.*,U.*,P.USER_ID AS 'ID' " +
					"FROM TRIP_HISTORY T, PASSENGERS P, USERS U WHERE P.PASS_ID = T.PASS_ID AND P.USER_ID = ? " +
					"AND U.USER_ID = T.DRIVER_ID) AS A LEFT JOIN REVIEWS ON REVIEWS.BY_USER_ID = A.ID AND " +
					"REVIEWS.ROUTE_ID = A.ROUTE_ID AND A.USER_ID = REVIEWS.OF_USER_ID;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) {
				User user = new User(results.getString("FIRST_NAME"),
						results.getString("LAST_NAME"), results.getInt("STARS"),
						results.getInt("IMAGE_LINK"), results.getInt("USER_ID"));
				Route route = new Route(results.getInt("ROUTE_ID"), new Location("",
						results.getString("START_LOC")), new Location("",
						results.getString("END_LOC")), results.getTimestamp("START_TIME"),
						null, results.getInt("PASS_ID"),
						results.getInt("DRIVER_ID"));
				historySegments.add(new HistorySegment(user, route));
			}
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return historySegments;
	}


	public int getDriverID(int routeID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT DRIVER_ID FROM ROUTES WHERE ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			if (results.next()) return results.getInt("DRIVER_ID");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}


	public String getEmailFromID(int ID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE USER_ID = ?;");
			preparedStatement.setInt(1, ID);
			results = preparedStatement.executeQuery();
			results.next();
			return results.getString("EMAIL");
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return "failed@limelight.tech";
	}


	public Location[] getFavLocations(int userID) {
		ArrayList<Location> array = new ArrayList();
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM FAV_LOCS WHERE USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) {
				array.add(new Location(results.getString("LOC_NAME"), results.getString("LOC_ADD")));
			}
			Location[] toReturn = new Location[array.size()];
			for (int i = 0; i < array.size(); i++) toReturn[i] = array.get(i);
			return toReturn;
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return null;
	}


	public int getIDFromEmail(String email) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE EMAIL = ?;");
			preparedStatement.setString(1, email);
			results = preparedStatement.executeQuery();
			results.next();
			return results.getInt("USER_ID");
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return -1;
	}


	public String getMarkers(Location startLocation, Location endLocation) {

		Route[] routes = getRoutesClose(startLocation, endLocation);
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;
		for (int i = 0; i < routes.length; i++) {
			Route route = routes[i];
			jsonObject = new JSONObject();
			String picture = "/images/" + Integer.toString(getProfilePicID(route.getDriverID()));
			try {
				jsonObject.put("picture", picture);
				jsonObject.put("latitude", route.getStartLoc().getLatitude());
				jsonObject.put("longitude", route.getStartLoc().getLongitude());
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return jsonArray.toString();
	}


	public MessageDataStore getMessages(int userID, int routeID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		ArrayList<String> messages = new ArrayList<>();
		ArrayList<Integer> names = new ArrayList<>();
		ArrayList<Boolean> sent = new ArrayList<>();
		ArrayList<Integer> id = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM MESSENGER WHERE ROUTE_ID = ?;");
			//preparedStatement.setInt(1, userID);
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			while (results.next()) {
				sent.add(results.getInt("USER_ID") == userID);
				messages.add(results.getString("MESSAGE"));
				id.add(results.getInt("USER_ID"));
			}
			for (Integer i : id) {
				names.add(userID);
			}
			String[] m = new String[messages.size()];
			Boolean[] s = new Boolean[messages.size()];
			int[] n = new int[messages.size()];
			for (int i = 0; i < messages.size(); i++) {
				m[i] = messages.get(i);
				s[i] = sent.get(i);
				n[i] = names.get(i);
			}
			return new MessageDataStore(m, s, n);
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return null;
	}


	/**
	 * Get a list of passengers the user has driven.
	 *
	 * @param userID User ID
	 * @return List of historySegments containing user and route data for the past routes
	 */
	public ArrayList<HistorySegment> getPassengerHistory(int userID) {
		ArrayList<HistorySegment> historySegments = new ArrayList<>();
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM (SELECT T.*,U.* FROM TRIP_HISTORY T, " +
					"PASSENGERS P, USERS U WHERE U.USER_ID = P.USER_ID AND T.PASS_ID = P.PASS_ID AND " +
					"DRIVER_ID = ?) AS A LEFT JOIN REVIEWS ON REVIEWS.BY_USER_ID = A.DRIVER_ID AND " +
					"REVIEWS.ROUTE_ID = A.ROUTE_ID AND A.USER_ID = REVIEWS.OF_USER_ID;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) {
				User user = new User(results.getString("FIRST_NAME"),
						results.getString("LAST_NAME"), results.getInt("STARS"),
						results.getInt("IMAGE_LINK"), results.getInt("USER_ID"));
				Route route = new Route(results.getInt("ROUTE_ID"), new Location("",
						results.getString("START_LOC")), new Location("",
						results.getString("END_LOC")), results.getTimestamp("START_TIME"),
						null, results.getInt("PASS_ID"),
						results.getInt("DRIVER_ID"));
				historySegments.add(new HistorySegment(user, route));
			}
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return historySegments;
	}


	public ArrayList<User> getPassengersOnRoute(int routeID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		ArrayList<User> passengers = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement("SELECT USER_ID FROM PASSENGERS, ROUTES WHERE ROUTES.PASS_ID = PASSENGERS.PASS_ID AND ROUTES.ROUTE_ID = ? AND ROUTES.DRIVER_ID <> PASSENGERS.USER_ID;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			while (results.next()) passengers.add(getUserInfo(results.getInt("USER_ID")));
			return passengers;
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return passengers;
	}


	public int getProfilePicID(String username) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT IMAGE_LINK FROM USERS WHERE EMAIL = ?;");
			preparedStatement.setString(1, username);
			results = preparedStatement.executeQuery();
			if (results.next()) return results.getInt("IMAGE_LINK");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 2;
	}


	public int getProfilePicID(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT IMAGE_LINK FROM USERS WHERE USER_ID = ?;");
			preparedStatement.setString(1, Integer.toString(userID));
			results = preparedStatement.executeQuery();
			if (results.next()) return results.getInt("IMAGE_LINK");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 2;
	}


	public float getRatingForUser(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT SUM(STARS) AS SUM, COUNT(STARS) AS TOTAL_RATINGS FROM REVIEWS WHERE OF_USER_ID = ? GROUP BY OF_USER_ID;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			if(results.next()) if (results.getInt("TOTAL_RATINGS") != 0) return (results.getInt("SUM") / results.getInt("TOTAL_RATINGS"));
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return 3.5f;
	}


	public String getReportString(int routeID, int userID) {
		// Was user driver or passenger?
		String passOrDriver = "Error: Unknown role -- This is fine, please continue with details below";
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			// Email Driver that they have cancelled a route
			preparedStatement = connection.prepareStatement("SELECT DRIVER_ID FROM ROUTES WHERE ROUTE_ID=?;");
			preparedStatement.setInt(1, routeID);
			ResultSet results = preparedStatement.executeQuery();
			results.next();
			if (userID == results.getInt("DRIVER_ID")) passOrDriver = "driving";
			else passOrDriver = "a%20passenger";
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return "mailto:enroutesystem@gmail.com?cc=enroute@limelight.tech&subject=REPORT:%20route%20id%20number:" + routeID + "&body=User ID Number:%20" + userID + "%20%0d%0aThis user was%20" + passOrDriver + "%0d%0a%0d%0aPlease%20provide%20details%20below:%0d%0a";
	}


	public String getRouteConfirmedScreen(int routeID) {
		String driverName = "";
		String addressName = "";
		String numMins = "";
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT DRIVER_ID,START_LOC,START_TIME " +
					"FROM ROUTES WHERE ROUTE_ID=?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			results.next();
			driverName = getUserInfo(results.getInt("DRIVER_ID")).getFirstName();
			String[] a = results.getString("START_LOC").split(" ");

			addressName = new HistorySegment(null, null).reverseLookUp(a[0], a[1]);
			numMins = results.getTimestamp("START_TIME").toString().substring(11, 16);
		} catch (SQLException err) {
			err.printStackTrace();
			;
		}
		return "Meet " + driverName + " at " + addressName + " at " + numMins;
	}


	public Route getRouteInfo(int routeID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES WHERE ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			if (results.next())
				return new Route(results.getInt("ROUTE_ID"), new Location("", results.getString("START_LOC")), new Location("", results.getString("END_LOC")), results.getTimestamp("START_TIME"), results.getInt("PASS_ID"), results.getInt("DRIVER_ID"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public int getRouteOfCurrentDrive(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("(SELECT ROUTE_ID, START_TIME FROM TRIP_HISTORY " +
					"WHERE START_TIME > ? AND DRIVER_ID = ?) UNION (SELECT ROUTE_ID, START_TIME FROM" +
					" TRIP_HISTORY, PASSENGERS WHERE START_TIME > ? AND PASSENGERS.PASS_ID = TRIP_HISTORY.PASS_ID" +
					" AND PASSENGERS.USER_ID = ?) ORDER BY START_TIME DESC;");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -20);
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
			preparedStatement.setTimestamp(1, timestamp);
			preparedStatement.setInt(2, userID);
			preparedStatement.setTimestamp(3, timestamp);
			preparedStatement.setInt(4, userID);
			results = preparedStatement.executeQuery();
			if (results.next()) return results.getInt("ROUTE_ID");
			else return -1;
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return 0;
	}


	public Route[] getRoutesClose(Location startLocation, Location endLocation) {
		// Get all locations, then run dist check to shrink
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		ArrayList<Route> routes = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES;");
			results = preparedStatement.executeQuery();
			while (results.next()) {
				routes.add(new Route(results.getInt("ROUTE_ID"), new Location("",
						results.getString("START_LOC")), new Location("",
						results.getString("END_LOC")), results.getTimestamp("START_TIME"),
						results.getInt("PASS_ID"), results.getInt("DRIVER_ID")));
			}
			// routes now contains all routes in system, parse each and remove those far, those that are full, and those
			// that have already begun and those that start too far in advance.

			Calendar cal = Calendar.getInstance();
			ArrayList<Route> toRemove = new ArrayList<>();
			for (Route route : routes) {
				if (CoordinateHandling.calcDistance(route.getStartLoc(), startLocation) > 850 ||
						CoordinateHandling.calcDistance(route.getEndLoc(), endLocation) > 850)
					toRemove.add(route);
				else {
					preparedStatement = connection.prepareStatement("SELECT COUNT(USER_ID) FROM " +
							"PASSENGERS WHERE PASS_ID = ?;");
					preparedStatement.setInt(1, route.getPassengerListID());
					results = preparedStatement.executeQuery();
					results.next();
					int numPass = results.getInt(1);
					if (numPass >= 5) toRemove.add(route);
					else {
						long diff = route.getStartTime().getTime() - cal.getTimeInMillis();
						int diffMinutes = (int) (diff / (60000));
						if (diffMinutes > 30 || diff < 0) toRemove.add(route);
					}
				}
			}
			for (Route route : toRemove) {
				routes.remove(route);
			}

			//return results
			Route[] routesToReturn = new Route[routes.size()];
			for (int i = 0; i < routes.size(); i++) {
				routesToReturn[i] = routes.get(i);

			}
			return routesToReturn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public User getUserInfo(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			results.next();
			int num = 2;
			try {
				num = results.getInt("IMAGE_LINK");
			} catch (Exception e) {
			}
			User user = new User(results.getString("FIRST_NAME"), results.getString("LAST_NAME"),
					0, num, userID);
			user.setRating(getRatingForUser(userID));
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Returns 1 if driver, 2 if pass, -1 if neither
	 */
	public Integer[] isUserCurrentlyOnRoute(String username) {
		int userID = getIDFromEmail(username);
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			// Test if driver
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES WHERE DRIVER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			if (results.next()) {
				return new Integer[]{1, results.getInt("ROUTE_ID")};
			}
			// Test if passenger
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES, PASSENGERS WHERE " +
					"ROUTES.PASS_ID = PASSENGERS.PASS_ID AND PASSENGERS.USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			if (results.next()) {
				return new Integer[]{2, results.getInt("ROUTE_ID")};
			}
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return new Integer[]{-1, -1};
	}


	public void leaveReview(int reviewBy, int reviewOf, int stars) {
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			// Establish if user is driver or passenger
			preparedStatement = connection.prepareStatement("INSERT INTO REVIEWS(OF_USER_ID, BY_USER_ID, STARS) VALUES (?,?,?);");
			preparedStatement.setInt(1, reviewOf);
			preparedStatement.setInt(2, reviewBy);
			preparedStatement.setInt(3, stars);
			preparedStatement.execute();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}


	public Boolean removePassenger(int routeID, int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM ROUTES WHERE ROUTE_ID = ?;");
			preparedStatement.setInt(1, routeID);
			results = preparedStatement.executeQuery();
			int pass_id, driver_id;
			if (results.next()) {
				pass_id = results.getInt("PASS_ID");
				driver_id = results.getInt("DRIVER_ID");
			} else return false;
			preparedStatement = connection.prepareStatement("DELETE FROM PASSENGERS WHERE PASS_ID = ? AND USER_ID = ?;");
			preparedStatement.setInt(1, pass_id);
			preparedStatement.setInt(1, userID);
			preparedStatement.execute();
			emailDriverPassCancelled(driver_id);
			return true;
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return false;
	}


	public void sendDriverCancellationEmail(String email) {
		try {
			MailHandler mailHandler = new MailHandler();
			mailHandler.sendEmail(email, "Route Cancelled", "We have successfully cancelled your planned " +
					"route; as this is a nuisance to our ride sharers, we ask that you refrain from cancelling routes " +
					"in the future");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendPassCancellationEmail(String email) {
		try {
			MailHandler mailHandler = new MailHandler();
			mailHandler.sendEmail(email, "Planned Route Cancelled", "Unfortunately, your driver has just " +
					"cancelled the planned route you were due to share. We're sorry for the inconvenience");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public ArrayList<ReviewListElement> showLeaveReviewPrompt(int userID) {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		ArrayList<ReviewListElement> usersReviewsShouldBeLeftFor = new ArrayList<>();
		ArrayList<ReviewListElement> reviewsLeftFor = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement("SELECT DISTINCT TH.DRIVER_ID FROM TRIP_HISTORY TH, PASSENGERS P WHERE TH.PASS_ID = P.PASS_ID and P.USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) usersReviewsShouldBeLeftFor.add(new ReviewListElement(true, results.getInt(1)));
			preparedStatement = connection.prepareStatement("SELECT DISTINCT P.USER_ID FROM TRIP_HISTORY TH, PASSENGERS P WHERE TH.PASS_ID = P.PASS_ID and TH.DRIVER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) usersReviewsShouldBeLeftFor.add(new ReviewListElement(false, results.getInt(1)));
			// Remove those reviews have been left for:
			preparedStatement = connection.prepareStatement("SELECT DISTINCT OF_USER_ID FROM REVIEWS WHERE BY_USER_ID = ?;");
			preparedStatement.setInt(1, userID);
			results = preparedStatement.executeQuery();
			while (results.next()) {
				for (ReviewListElement reviewListElement : usersReviewsShouldBeLeftFor) {
					if (reviewListElement.getUserID() == results.getInt(1)) reviewsLeftFor.add(reviewListElement);
				}
			}
			for (ReviewListElement reviewListElement : reviewsLeftFor)
				usersReviewsShouldBeLeftFor.remove(reviewListElement);
			return usersReviewsShouldBeLeftFor;
		} catch (SQLException err) {
			err.printStackTrace();
		}
		return null;
	}


	public Boolean testConn() {
		ResultSet results;
		PreparedStatement preparedStatement;
		Connection connection = instance.getConnection();
		try {
			preparedStatement = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES;");
			results = preparedStatement.executeQuery();
			if (results.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
