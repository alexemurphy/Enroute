package main.servlet.servletClasses;


import main.db.DBHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/SendMessage/*")
public class MessageReceiver extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		String requestName = request.getPathInfo().substring(1); // Returns num in call
		String[] array = requestName.split("/");
		int userID = Integer.parseInt(array[0]);
		int routeID = Integer.parseInt(array[1]);
		try {
			DBHelper.instance().addMessageToDB(userID, routeID, array[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
