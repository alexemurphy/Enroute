package main.objects;

public class ReviewListElement {

	private Boolean driverOrPassenger;
	private int userID;


	public ReviewListElement(Boolean driverOrPassenger, int userID) {
		this.driverOrPassenger = driverOrPassenger;
		this.userID = userID;
	}


	public Boolean getDriverOrPassenger() {
		return driverOrPassenger;
	}


	public void setDriverOrPassenger(Boolean driverOrPassenger) {
		this.driverOrPassenger = driverOrPassenger;
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
	}
}
