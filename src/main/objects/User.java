package main.objects;

public class User {

	private String firstName;
	private String lastName;
	private int profilePicture;
	private float rating;
	private int userID;


	public User(String firstName, String lastName, float rating, int profilePicture, int userID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.rating = rating;
		this.profilePicture = profilePicture;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public int getProfilePicture() {
		return profilePicture;
	}


	public void setProfilePicture(int profilePicture) {
		this.profilePicture = profilePicture;
	}


	public float getRating() {
		return rating;
	}


	public void setRating(float rating) {
		this.rating = rating;
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
	}


}
