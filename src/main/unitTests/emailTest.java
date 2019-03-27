package main.unitTests;

import utils.MailHandler;

import java.io.IOException;

public class emailTest {

	public static void main(String[] args) {
		try {
			MailHandler mailHandler = new MailHandler();
			if (mailHandler.sendEmail("liampugh@limelight.tech", "Test Email", "Message body") == 0) {
				System.out.println("Pass");
			} else {
				System.out.println("Fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
