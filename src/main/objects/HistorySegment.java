package main.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HistorySegment {

	private Route route;
	private User user;


	public HistorySegment(User user, Route route) {
		this.user = user;
		this.route = route;
	}


	public String getDate() {
		String dateString = this.route.getStartTime().toString(); // Returns: dow mon dd hh:mm:ss zzz yyyy
		return dateString.substring(8, 10) + " " + dateString.substring(4, 7);
	}


	public String getDestinationLocation() {
		String lat = this.route.getEndLoc().getLatitude().toString();
		String lng = this.route.getEndLoc().getLongitude().toString();
		String a = reverseLookUp(lat, lng);
		return a == null ? this.route.getEndLoc().getAddress() : a;
	}


	public String getName() {
		return this.user.getFirstName() + " " + this.user.getLastName();
	}


	public String getOriginLocation() {
		String lat = this.route.getStartLoc().getLatitude().toString();
		String lng = this.route.getStartLoc().getLongitude().toString();
		String a = reverseLookUp(lat, lng);
		return a == null ? this.route.getStartLoc().getAddress() : a;
	}


	public int getProfilePicNum() {
		return this.user.getProfilePicture();
	}


	public int getStarRating() {
		return (int) this.user.getRating();
	}


	public String getTime() {
		String dateString = this.route.getStartTime().toString(); // Returns: dow mon dd hh:mm:ss zzz yyyy
		return dateString.substring(11, 16);
	}


	public String reverseLookUp(String lat, String lng) {
			String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=AIzaSyDHEu6l_OvI7aCHQ0jyydKvBw6qD7OM2Ko";
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			//add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			JSONObject myResponse = new JSONObject(response.toString());
			return myResponse.getJSONArray("results").getJSONObject(0).getString("formatted_address");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
