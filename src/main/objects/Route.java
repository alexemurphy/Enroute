package main.objects;

import java.util.Date;

//Route object. with startLoc, endLoc, startTime and driverID
public class Route {

	private int driverID;
	private Location endLoc;
	private Date endTime;
	private int passengerListID;
	private int routeID;
	private Location startLoc;
	private Date startTime;


	public Route(int routeID, Location startLoc, Location endLoc, Date startTime, int passengerListID, int driverID) {
		this.routeID = routeID;
		this.startLoc = startLoc;
		this.endLoc = endLoc;
		this.startTime = startTime;
		this.passengerListID = passengerListID;
		this.driverID = driverID;
	}


	public Route(int routeID, Location startLoc, Location endLoc, Date startTime, Date endTime, int passengerListID, int driverID) {
		this.routeID = routeID;
		this.startLoc = startLoc;
		this.endLoc = endLoc;
		this.startTime = startTime;
		this.endTime = endTime;
		this.passengerListID = passengerListID;
		this.driverID = driverID;
	}


	public Route(Location startLoc, Location endLoc, int driverID, int timeTillDepart) {

		this.startLoc = startLoc;
		this.endLoc = endLoc;
		long currentTime = new Date().getTime();
		this.startTime = new Date(currentTime + timeTillDepart * 60000);
		System.out.println(this.startTime.toString());
		this.endTime = new Date();
		this.driverID = driverID;
	}


	public int getDriverID() {
		return driverID;
	}


	public void setDriverID(int driverID) {
		this.driverID = driverID;
	}


	public Location getEndLoc() {
		return endLoc;
	}


	public void setEndLoc(Location endLoc) {
		this.endLoc = endLoc;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public int getPassengerListID() {
		return passengerListID;
	}


	public void setPassengerListID(int passengerListID) {
		this.passengerListID = passengerListID;
	}


	public int getRouteID() {
		return routeID;
	}


	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}


	public Location getStartLoc() {
		return startLoc;
	}


	public void setStartLoc(Location startLoc) {
		this.startLoc = startLoc;
	}


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
}
