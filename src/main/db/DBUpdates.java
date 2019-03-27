package main.db;

import main.objects.HistorySegment;
import main.objects.Location;
import main.objects.ReviewListElement;
import main.objects.Route;
import main.unitTests.BETestsVarious;

import utils.Authentication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DBUpdates {

	// Test main for use in testing DB query designs -- Ignore
	public static void main(String[] args) throws Exception {
		//new DBUpdates().uploadImage("C:\\Users\\Liam Pugh\\Desktop\\3.png");
		//System.out.println(new BETestsVarious().testBE());
		/*
		try {
			ArrayList<HistorySegment> historySegments = DBHelper.instance().getPassengerHistory(5);
			for (HistorySegment historySegment: historySegments) System.out.println(historySegment.getName());
		}catch (Exception err) {
			err.printStackTrace();
		}
		*/
		//System.out.println(new HistorySegment(null,new Route(0,new Location("","51.377168 -2.330838"),null,null,0,0)).getOriginLocation());
		//System.out.println(DBHelper.instance().getRouteConfirmedScreen(3));
		///DBHelper.instance().getRouteOfCurrentDrive('1');
		/*
		for (int i = 1; i < 31; i++) {
			ArrayList<ReviewListElement> reviewListElements = DBHelper.instance().showLeaveReviewPrompt(i);
			System.out.println(reviewListElements.size());
		}
		*/
		(new DBUpdates()).createNewCar("BN59 KFU", "BLUE", "VAUXHALL", "CORSA", 37);
	}


	public Boolean createNewCar(String reg, String colour, String make, String model, int userID) {
		try {
			Connection connection = DBHelper.instance().getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO CARS VALUES (?,?,?,?,?);");
			preparedStatement.setString(1, reg);
			preparedStatement.setString(2, colour);
			preparedStatement.setString(3, make);
			preparedStatement.setString(4, model);
			preparedStatement.setInt(5, userID);
			preparedStatement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public Boolean createNewUser(String fname, String lname, String address, String email, String phone, Boolean dorp, String password, String paymentTransactionNumber) {
		// Gen hashed pass and salt
		String[] passArray = Authentication.generateHash(password);
		try {
			Connection connection = DBHelper.instance().getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO PAYMENT (TRANSACTION_NUMBER) VALUES (?);");
			preparedStatement.setString(1, paymentTransactionNumber);
			preparedStatement.execute();
			// Get transact number added
			preparedStatement = connection.prepareStatement("SELECT * FROM PAYMENT;");
			ResultSet resultSet = preparedStatement.executeQuery();
			int num = 0;
			while (resultSet.next()) num++;
			preparedStatement = connection.prepareStatement("INSERT INTO USERS (FIRST_NAME,ADDRESS,EMAIL,PHONE,DORP,PASSWORD,SALT,PAYMENT_DETAILS,LAST_NAME) VALUES (?,?,?,?,?,?,?,?,?);");
			preparedStatement.setString(1, fname);
			preparedStatement.setString(2, address);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, phone);
			preparedStatement.setBoolean(5, dorp);
			preparedStatement.setString(6, passArray[0]);
			preparedStatement.setString(7, passArray[1]);
			preparedStatement.setInt(8, num);
			preparedStatement.setString(9, lname);
			preparedStatement.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	public void uploadImage(String filename) {
		byte[] b = null;
		try {
			Connection conn = DBHelper.instance().getConnection();
			PreparedStatement psImageInsertDatabase = null;

			String sqlImageInsertDatabase = "INSERT INTO IMAGES (IMAGE) values(?)";
			psImageInsertDatabase = conn.prepareStatement(sqlImageInsertDatabase);

			BufferedImage bImage = ImageIO.read(new File(filename));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "png", bos);
			b = bos.toByteArray();
			psImageInsertDatabase.setBytes(1, b);
			psImageInsertDatabase.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
