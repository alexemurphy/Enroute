package main.objects;

public class Location {

	private String address;
	private Double latitude;
	private Double longitude;
	private String name;


	public Location(String name, String address) {
		this.name = name;
		this.address = address;
	}


	public Location(String name, Double latitude, Double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}


	private void getCoorFromString() {
		String[] coor = this.address.split(" ");
		this.latitude = Double.parseDouble(coor[0]);
		this.longitude = Double.parseDouble(coor[1]);
	}


	public String getAddress() {
		if (this.address == null) this.address = Double.toString(latitude) + " " + Double.toString(longitude);
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Double getLatitude() {
		if (this.latitude == null) {
			getCoorFromString();
		}
		return this.latitude;
	}


	public Double getLongitude() {
		if (this.longitude == null) {
			getCoorFromString();
		}
		return this.longitude;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
