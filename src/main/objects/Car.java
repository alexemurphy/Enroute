package main.objects;

public class Car {

	private String color;
	private String make;
	private String model;
	private String reg_plate;
	private int user_ID;


	public Car(String make, String model, String color, String reg_plate, int user_ID) {
		this.make = make;
		this.model = model;
		this.color = color;
		this.reg_plate = reg_plate;
		this.user_ID = user_ID;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public String getMake() {
		return make;
	}


	public void setMake(String make) {
		this.make = make;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public String getReg_plate() {
		return reg_plate;
	}


	public void setReg_plate(String reg_plate) {
		this.reg_plate = reg_plate;
	}


	public int getUser_ID() {
		return user_ID;
	}


	public void setUser_ID(int user_ID) {
		this.user_ID = user_ID;
	}
}
