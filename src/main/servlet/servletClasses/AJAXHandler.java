package main.servlet.servletClasses;


import main.db.DBHelper;
import main.objects.*;
import utils.CoordinateHandling;
import utils.MessageDataStore;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@WebServlet("/AJAX/*")
public class AJAXHandler extends HttpServlet {


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestName = request.getPathInfo().substring(1); // Returns num in call
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		HttpSession session = request.getSession();
		switch (requestName) {


			case "2":
				try {
					int userId = DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"));
					int routeID = (Integer) session.getAttribute("routeid");
					response.getWriter().write(createMessageDIV(DBHelper.instance().getMessages(userId, routeID)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				response.getWriter().write("HELLO WORLD! It is currently " + sdf.format(cal.getTime()));
				break;
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestName = request.getPathInfo().substring(1); // Returns num in call
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		Location start, end;
		int routeID, userID, userIDreviewee;
		String out = "";

		switch (requestName) {


			case "15"://submit rating
				try {
					userIDreviewee = Integer.parseInt(request.getParameter("userIDreviewee"));
					userID = Integer.parseInt(request.getParameter("userID"));
					double rating = Double.parseDouble(request.getParameter("rating"));

					submitRating(userIDreviewee, userID, rating);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case "14": //cancelRoute

				routeID = Integer.parseInt(request.getParameter("routeID"));
				System.out.print("cancel route id:" + routeID);
				try {
					DBHelper.instance().cancelRoute(routeID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case "13": //show rating screen
				userID = Integer.parseInt(request.getParameter("userIDreviewee"));
				response.getWriter().write(createRatingsPage(userID));

				break;

			case "12": //reviews

				userID = Integer.parseInt(request.getParameter("userID"));
				response.getWriter().write(createReviewsPage(userID));
				break;

			case "11": //driverHistory

				userID = Integer.parseInt(request.getParameter("userID"));
				response.getWriter().write(createDriverHistoryPage(userID));
				break;

			case "10": //passengerHistory

				userID = Integer.parseInt(request.getParameter("userID"));
				response.getWriter().write(createPassengerHistoryPage(userID));
				break;

			case "9": //depart

				routeID = Integer.parseInt(request.getParameter("routeID"));
				try {
					DBHelper.instance().depart(routeID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "8": //get route (find ridesharers)

				routeID = Integer.parseInt(request.getParameter("routeID"));
				response.getWriter().write(createRidesharersTable(routeID));
				break;

			case "7": //get markers - one
				routeID = Integer.parseInt(request.getParameter("routeID"));

				try {
					out = DBHelper.instance().getChosenDriverMarker(routeID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(out);

				break;

			case "6": //get markers - all
				start = new Location("start", Double.parseDouble(request.getParameter("startLat")),
						Double.parseDouble(request.getParameter("startLng")));

				end = new Location("end", Double.parseDouble(request.getParameter("endLat")),
						Double.parseDouble(request.getParameter("endLng")));

				try {
					out = DBHelper.instance().getMarkers(start, end);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(out);

				break;
			case "5": //remove passenger
				routeID = Integer.parseInt(request.getParameter("routeID"));
				userID = Integer.parseInt(request.getParameter("userID"));
				try {
					if (DBHelper.instance().removePassenger(routeID, userID)) {
						response.getWriter().write("Successfully removed from route.");
					} else {
						response.getWriter().write("Error code: 203");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case "4": //Drivers info page
				routeID = Integer.parseInt(request.getParameter("routeID"));

				try {
					out = createDriversInfoPage(routeID, request);
				} catch (Exception e) {
					e.printStackTrace();
				}
				response.getWriter().write(out);
				break;

			case "3": //request and pay screen
				routeID = Integer.parseInt(request.getParameter("routeID"));
				response.getWriter().write(requestAndPayScreen(routeID));
				break;

			case "2": //new route (find ridesharers)
				start = new Location("start", Double.parseDouble(request.getParameter("startLat")),
						Double.parseDouble(request.getParameter("startLng")));

				end = new Location("end", Double.parseDouble(request.getParameter("endLat")),
						Double.parseDouble(request.getParameter("endLng")));
				routeID = 0;
				userID = Integer.parseInt(request.getParameter("userID"));
				int timeTillDepart = Integer.parseInt(request.getParameter("timeTillDepart"));
				try {
					routeID = DBHelper.instance().addRoute(new Route(start, end, userID, timeTillDepart));

				} catch (Exception e) {
					e.printStackTrace();
				}

				response.getWriter().write(Integer.toString(routeID));
				break;


			case "1":
				start = new Location("start", Double.parseDouble(request.getParameter("startLat")),
						Double.parseDouble(request.getParameter("startLng")));

				end = new Location("end", Double.parseDouble(request.getParameter("endLat")),
						Double.parseDouble(request.getParameter("endLng")));


				response.getWriter().write(createDriversFoundTable(start, end));
				break;

			default:
				response.getWriter().write("Not found");
				break;
		}
	}


	private void submitRating(int userIDreviewee, int userID, double rating) {

		try {
			DBHelper.instance().leaveReview(userID, userIDreviewee, (int) Math.round(rating));
		} catch (Exception e) {
		}

	}


	private String requestAndPayScreen(int routeID) {

		User user = null;
		try {
			Route route = DBHelper.instance().getRouteInfo(routeID);
			user = DBHelper.instance().getUserInfo(route.getDriverID());
		} catch (Exception e) {
			e.printStackTrace();
		}
        /*String fileName = this.getServletContext().getRealPath("WEB-INF/requestAndPay.html");

        System.out.print("file" + fileName);
        File file = new File(fileName);
        BufferedReader br;
        String line;
        StringBuilder builder = new StringBuilder();

        try (FileReader fr = new FileReader(file)) {
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.print(builder.toString());*/

		String html = "<div class=\"requestAndPay\">\n" +
				"    <div style=\"margin-top: 49px;\"><img src=\"%s\" style=\"object-fit: cover;border-radius: 50%%;width: 120px;height: 120px;margin-right: auto;margin-left: auto;display: block;\"></div>\n" +
				"    <div style=\"width: 100%%;\">\n" +
				"        <p class=\"text-center\" style=\"margin-top: 29px;font-family: Roboto, sans-serif;color: #eddc36;font-size: 26px;\">%s</p>\n" +
				"    </div>\n" +
				"    <div style=\"text-align: center;width: 100%%;\"><span style=\"margin-right: auto;display: block;margin-left: auto;\"><span style=\"font-size: 35px;color: #eddc36;font-family: Roboto, sans-serif;font-weight: 500;\">%.2f&nbsp;</span><span class=\"glyphicon glyphicon-star\" style=\"font-size: 35px;color: #eddc36;\"></span></span>\n" +
				"    </div>\n" +
				"    <div>\n" +
				"        <hr style=\"width: 177px;\">\n" +
				"    </div>\n" +
				"    <div>\n" +
				"        <p class=\"text-center\" style=\"font-family: Roboto, sans-serif;font-style: italic;font-weight: 500;color: #eddc36;font-size: 60px;margin-bottom: -5px;\">£%.2f</p>\n" +
				"    </div>\n" +
				"    <div>\n" +
				"        <p class=\"text-center\" style=\"font-size: 15px;color: rgb(95,95,95);\">30p per Mile</p>\n" +
				"        <div style=\"left: 0;position: absolute;bottom: 0;width: 100%%;\"><button id=\"requestAndPayButton\" class=\"btn btn-primary\" type=\"button\" style=\"display: block;margin-left: auto;margin-right: auto;width: 77%%;margin-bottom: 34px;height: 51px;font-size: 21px;font-family: Roboto, sans-serif;font-weight: 600;\" >&nbsp;<img class=\"img-rounded\" src=\"https://www.paypalobjects.com/webstatic/icon/pp258.png\" style=\"width: 35px;height: 35px;\">&nbsp;Request and Pay</button></div>\n" +
				"    </div>\n" +
				"</div>";
		String name = user.getFirstName() + " " + user.getLastName();
		String profilePicturePath = "/images/" + Integer.toString(user.getProfilePicture());


		String returnValue = null;
		try {
			Route route = DBHelper.instance().getRouteInfo(routeID);
			int distance = CoordinateHandling.calcDistance(route.getStartLoc(), route.getEndLoc());
			Double cost = distance * 0.0003;
			returnValue = String.format(html, profilePicturePath, name, user.getRating(), cost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;


	}


	private String[] readFromFile(String filename) { //Not used

		String fileName = "driversTable.txt";
		File file = new File(fileName);
		BufferedReader br;
		String line;
		StringBuilder builder = new StringBuilder();
		String[] returnString = new String[3];

		try (FileReader fr = new FileReader(file)) {
			br = new BufferedReader(fr);

			while (!(line = br.readLine()).equals("***")) {
				builder.append(line);
			}
			returnString[0] = builder.toString();
			builder = new StringBuilder();
			while (!(line = br.readLine()).equals("***")) {
				builder.append(line);
			}
			returnString[1] = builder.toString();
			builder = new StringBuilder();
			while (!(line = br.readLine()).equals("***")) {
				builder.append(line);
			}
			returnString[2] = builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;

	}


	private String createDriversFoundTable(Location start, Location end) {


		String head = "<table id=\"driversTable\" class=\"table\">\n" +
				"                    <thead>\n" +
				"                        <tr></tr>\n" +
				"                    </thead>\n" +
				"                    <tbody>";

		StringBuilder mid = new StringBuilder();
		try {
			Route[] closeRoutes = DBHelper.instance().getRoutesClose(start, end);
			for (int i = 0; i < closeRoutes.length; i++) {
				int driverID = closeRoutes[i].getDriverID();
				User driver = DBHelper.instance().getUserInfo(driverID);
				mid.append(createUserFoundCell(driver, closeRoutes[i].getRouteID()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		String endOfTable = "</tbody>\n" +
				"                </table>";

		return head + mid.toString() + endOfTable;
	}


	private String createRidesharersTable(int routeID) {

		String head = "        <div class=\"driversFound\" style=\"height: 100%;width: 100%;background-color: #000000;z-index: 99;\">\n" +
				"\n" +
				"                <table class=\"table\">\n" +
				"                    <thead>\n" +
				"                    <tr></tr>\n" +
				"                    </thead>\n" +
				"                    <tbody>";

		StringBuilder mid = new StringBuilder();
		try {
			ArrayList<User> users = DBHelper.instance().getPassengersOnRoute(routeID);
			for (int i = 0; i < users.size(); i++) {
				mid.append(createUserFoundCell(users.get(i), routeID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String end = "</tbody>\n" +
				"                </table>\n" +
				"\n" +
				"        </div>" +
				"<div class=\"d-flex justify-content-center align-items-center driversFound\" style=\"z-index: 101;width: 100%;height: 80px;position: absolute;bottom: 0;\">\n" +
				"            <div style=\"height: 100%;width: 100%;padding-top: 12px;padding-bottom: 12px;padding-right: 12px;padding-left: 12px;\"><button class=\"btn btn-primary\" type=\"button\" style=\"height: 100%;border: none;background-color: #eddc36;border-radius: 0;font-family: Roboto, sans-serif;font-weight: bold;color: #000000;font-size: 26px;width: 100%;\">PREPARE TO LEAVE</button></div>\n" +
				"        </div>";

		return head + mid.toString() + end;
	}


	private String createUserFoundCell(User driver, int routeID) {

		String profilePicturePath = "/images/" + Integer.toString(driver.getProfilePicture());

		String driverName = driver.getFirstName() + " " + driver.getLastName();

		return String.format("<tr class=\"clickable-row\" id='%d' style=\"border: solid black; border-bottom: solid rgb(41,41,41) 0.255px;width: 100%%;height: 75px;border-collapse: collapse;\"><td style=\"width: 74px;\"><img src=\"%s\" " +
				"style=\"object-fit: cover;border-radius: 50%%;width: 60px;height: 60px;\"></td>\n" +
				"<td style=\"color: rgb(218,218,218);padding-top: 25px;font-size: 17px;font-family: Roboto, sans-serif;font-weight: 300;\">%s</td>\n" +
				"<td style=\"font-family: Roboto, sans-serif;font-weight: 500;color: yellow;font-size: 18px;text-align: center;" +
				"padding-top: 23px;\"><strong>%.2f&nbsp;</strong>&nbsp;<span class=\"glyphicon glyphicon-star\" style=\"color: yellow;font-size: 18px;\"></span></td></tr>", routeID, profilePicturePath, driverName, driver.getRating());

	}


	private String createMessageDIV(MessageDataStore messageDataStore) {


		String incomingMessage = "<div class=\"incoming_msg\">\n" +
				"              <div class=\"incoming_msg_img\"> <img src=\"%s\"> </div>\n" +
				"              <div class=\"received_msg\">\n" +
				"                <div class=\"received_withd_msg\">\n" +
				"                  <p>%s</p>\n" +
				"                  <span class=\"time_date\">%s %s</span></div>\n" +
				"              </div>\n" +
				"            </div>";

		String outGoingMessage = "<div class=\"outgoing_msg\">\n" +
				"              <div class=\"sent_msg\">\n" +
				"                <p>%s</p>\n" +
				"                <span class=\"time_date\"></span> </div>\n" +
				"            </div>";

		String[] messages = messageDataStore.getMessages();
		Boolean[] sent = messageDataStore.getSent();
		int[] userID = messageDataStore.getUserID();

		StringBuilder returnString = new StringBuilder();

		for (int i = 0; i < messages.length; i++) {
			if (!sent[i]) {
				User user = null;
				try {
					user = DBHelper.instance().getUserInfo(userID[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String profilePicturePath = "/images/" + Integer.toString(user.getProfilePicture());
				returnString.append(String.format(incomingMessage, profilePicturePath, messages[i], user.getFirstName(), user.getLastName()));
			} else {
				returnString.append(String.format(outGoingMessage, messages[i]));
			}
		}

		return returnString.toString();
	}


	private String createMessagesTable(MessageDataStore messageDataStore) {
		String head =
				"<table width=\"80%\">\n" +
						"<tr>\n" +
						"<td width=\"10%\">Name</td>\n" +
						"<td width=\"35%\">Message</td>\n" +
						"<td width=\"35%\">Your Messages</td>\n" +
						"</tr>\n";
		String[] messages = messageDataStore.getMessages();
		Boolean[] sent = messageDataStore.getSent();
		String[] name = null;
		//String[] name = messageDataStore.getName();
		StringBuilder mid = new StringBuilder();
		for (int i = 0; i < messages.length; i++) {
			if (!sent[i]) {
				mid.append("<tr><td>");
				mid.append(name[i]);
				mid.append("</td><td>");
				mid.append(messages[i]);
				mid.append("</td><td></td></tr>");
			} else {
				mid.append("<tr><td></td><td></td><td>");
				mid.append(messages[i]);
				mid.append("</td></tr>");
			}
		}
		String end = "</table>\n";
		return head + mid.toString() + end;
	}


	private String createDriversInfoPage(int routeID, HttpServletRequest request) {

		String body = "    <div class=\"driverInfo\" style=\"bottom: 0;height: 100%%;position: absolute;width: 100%%;background-color: #000000;z-index: 99;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.75);\">\n" +
				"        <div style=\"height: 10%%;\">\n" +
				"            <p style=\"font-family: Roboto, sans-serif;font-weight: 300;font-size: 17px;color: rgb(180,180,180);margin-top: 10px;margin-left: 10px;max-width: 500px;\">%s</p>\n" +
				"        </div>\n" +
				"        <div style=\"height: 50%%;margin-left: 10px;margin-right: 10px;/*max-width: 500px;*/\">\n" +
				"            <div style=\"width: 50%%;height: 100%%;float: left;\">\n" +
				"                <div style=\"height: 50%%;\">\n" +
				"                    <div class=\"d-flex justify-content-center align-items-center\" style=\"width: 30%%;height: 100%%;float: left;\"><img class=\"d-flex justify-content-center align-items-center\" src=\"%s\" width=\"50px\" height=\"50px\" style=\"object-fit: cover;border-radius: 50%%;\"></div>\n" +
				"                    <div class=\"d-flex justify-content-start align-items-center\" style=\"width: 70%%;height: 100%%;overflow: hidden;\">\n" +
				"                        <p class=\"d-flex justify-content-center align-items-center\" style=\"color: rgb(180,180,180);font-family: Roboto, sans-serif;font-size: 19px;margin-left: 8px;font-weight: 300;\">%s</p>\n" +
				"                    </div>\n" +
				"                </div>\n" +
				"                <div><span class=\"d-flex justify-content-center\" style=\"margin-right: auto;display: block;margin-left: auto;\"><span style=\"font-size: 25px;color: #eddc36;font-family: Roboto, sans-serif;font-weight: 500;\">%.2f</span><span class=\"glyphicon glyphicon-star\"\n" +
				"                        style=\"font-size: 23px;color: #eddc36;margin-top: 3px;\"></span></span>\n" +
				"                </div>\n" +
				"            </div>\n" +
				"            <div style=\"width: 50%%;height: 100%%;overflow: hidden;\">\n" +
				"                <div style=\"height: 50%%; color: rgb(180,180,180);font-family: Roboto, sans-serif;font-size: 17px;font-weight: 300;\">\n" +
				"                    <p>%s</p>\n" +
				"                    <p style=\"margin-top: -11px;\">%s %s</p>\n" +
				"                </div>\n" +
				"                <div class=\"d-flex justify-content-center\" style=\"height: 50%%;\">\n" +
				"                    <div style=\"font-family: 'Charles Wright';  font-style: normal; background-color: #eddc36;width: 80%%;display: block;margin: auto;border-radius: 10px;height: 50px;max-width: 200px;\">\n" +
				"\n" +
				"                        <p class=\"text-center d-flex justify-content-center align-items-center\" style=\"color: #000000; height: 100%%;width: 100%%;font-size: 23px;\">%s</p>\n" +
				"                    </div>\n" +
				"                </div>\n" +
				"            </div>\n" +
				"        </div>\n" +
				"        <div style=\"bottom: 0;position: absolute;width: 100%%;height: 30%%;\">\n" +
				"            <div style=\"width: 50%%;height: 100%%;float: left;\"><button onclick=\"window.location.href='/chatScreen.jsp?routeid=%s'\" class=\"btn btn-primary\" type=\"button\" style=\"color: white; width: 90%%;height: 80%%;display: block;margin-left: auto;margin-right: auto;font-size: 20px;font-family: Roboto, sans-serif;background-color: #39B54A;font-weight: 300;border: 0;border-radius: 2px;overflow: hidden;\"><img src=\"assets/img/chat.png\" width=\"30px\"><br>\n" +
				"Message</button></div>\n" +
				"            <div\n" +
				"                style=\"width: 50%%;height: 100%%;overflow: hidden;\"><a href=\"%s\" class=\"d-flex justify-content-center align-items-center btn btn-primary\" style=\"width: 90%%;height: 80%%;margin-right: auto;margin-left: auto;display: block;font-family: Roboto, sans-serif;font-size: 20px;font-weight: 300;background-color: #e5053a;border: 0;border-radius: 2px;overflow: hidden;\">\n" +
				"    <div><img src=\"assets/img/problem.png\" width=\"30px\" style=\"/*padding-bottom: 5px;*/padding-top: 7px;\" /><br />\n" +
				"        <p>Report</p>\n" +
				"    </div>\n" +
				"</a></div>\n" +
				"    </div>\n" +
				"    </div>";

		Route route = null;
		User driver = null;
		Car car = null;
		String report = null;
		String curTime = null;
		try {
			route = DBHelper.instance().getRouteInfo(routeID);
			driver = DBHelper.instance().getUserInfo(route.getDriverID());
			car = DBHelper.instance().getCarInfo(route.getDriverID());
			HttpSession session = request.getSession();
			report = DBHelper.instance().getReportString(route.getRouteID(), DBHelper.instance().getIDFromEmail((String) session.getAttribute("username")));
			curTime = DBHelper.instance().getRouteConfirmedScreen(route.getRouteID());

		} catch (Exception e) {
			e.printStackTrace();
		}
		String profilePicturePath = "/images/" + Integer.toString(driver.getProfilePicture());

		String name = driver.getFirstName() + " " + driver.getLastName();


		return String.format(body, curTime, profilePicturePath, name, driver.getRating(), car.getColor(), car.getMake(), car.getModel(), car.getReg_plate(), route.getRouteID(), report);


	}


	private String createPassengerHistoryPage(int userID) {
		String div = "<div style=\"width: 100%%;height: 100px;font-family: Roboto, sans-serif;font-weight: 300;\">\n" +
				"            <div class=\"d-flex justify-content-center align-items-center\" style=\"width: 20%%;height: 100%%;float: left;\"><img src=\"images/%d\" width=\"50px\" height=\"50px\" style=\"object-fit: cover;border-radius: 50%%;\"></div>\n" +
				"            <div class=\"d-flex flex-column justify-content-center\" style=\"width: 60%%;float: left;height: 100%%;\">\n" +
				"                <p class=\"driverInfoMiddleText\" style=\"font-weight: 500;\">%s</p>\n" +
				"                <p class=\"driverInfoMiddleText\">%s to</p>\n" +
				"                <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"            </div>\n" +
				"            <div class=\"d-flex flex-column justify-content-center align-items-center\" style=\"height: 100%%;width: 20%%;margin-right: 0;margin-left: auto;\">\n" +
				"                <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"                <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"            </div>\n" +
				"            <hr style=\"margin-top: 0px;opacity: 0.67;\">\n" +
				"        </div>";
		StringBuilder returnString = new StringBuilder();
		ArrayList<HistorySegment> historySegments = new ArrayList<>();

		try {
			historySegments = DBHelper.instance().getPassengerHistory(userID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < historySegments.size(); i++) {
			returnString.append(String.format(div, historySegments.get(i).getProfilePicNum(), historySegments.get(i).getName(), historySegments.get(i).getOriginLocation(), historySegments.get(i).getDestinationLocation(), historySegments.get(i).getTime(), historySegments.get(i).getDate()));
		}
		return returnString.toString();

	}


	private String createDriverHistoryPage(int userID) {
		String div = "<div style=\"width: 100%%;height: 80px;font-family: Roboto, sans-serif;font-weight: 300;\">\n" +
				"                <div class=\"d-flex justify-content-center align-items-center\" style=\"width: 5%%;height: 100%%;float: left;\"></div>\n" +
				"                <div class=\"d-flex flex-column justify-content-center\" style=\"width: 70%%;float: left;height: 100%%;\">\n" +
				"                    <p class=\"driverInfoMiddleText\">%s to</p>\n" +
				"                    <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"                </div>\n" +
				"                <div class=\"d-flex flex-column justify-content-center align-items-center\" style=\"height: 100%%;width: 20%%;margin-right: 0;margin-left: auto;\">\n" +
				"                    <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"                    <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"                </div>\n" +
				"                <hr style=\"margin-top: 0px;opacity: 0.67;\">\n" +
				"            </div>";
		StringBuilder returnString = new StringBuilder();
		ArrayList<HistorySegment> historySegments = new ArrayList<>();

		try {
			historySegments = DBHelper.instance().getDriverHistory(userID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < historySegments.size(); i++) {
			returnString.append(String.format(div, historySegments.get(i).getOriginLocation(), historySegments.get(i).getDestinationLocation(), historySegments.get(i).getTime(), historySegments.get(i).getDate()));
		}
		return returnString.toString();

	}


	private String createReviewsPage(int userID) {

		String div = "<div class=\"reviewCellDIV\" id=\"%d\" style=\"font-family: Roboto, sans-serif; width: 100%%;height: 80px;\">\n" +
				"                <div class=\"d-flex justify-content-center align-items-center\" style=\"width: 20%%;height: 100%%;float: left;\"><img src=\"images/%d\" width=\"50px\" height=\"50px\" style=\"object-fit: cover;border-radius: 50%%;\"></div>\n" +
				"                <div class=\"d-flex flex-column justify-content-center\" style=\"width: 50%%;float: left;height: 100%%;\">\n" +
				"                    <p class=\"driverInfoMiddleText\" style=\"font-weight: 500;\">%s %s</p>\n" +
				"                </div>\n" +
				"                <div class=\"d-flex flex-column justify-content-center align-items-center\" style=\"height: 100%%;width: 30%%;margin-right: 0;margin-left: auto;\">\n" +
				"                    <p class=\"driverInfoMiddleText\">%s</p>\n" +
				"                </div>\n" +
				"                <hr style=\"margin-top: 0px;opacity: 0.67;\">\n" +
				"            </div>";
		StringBuilder returnString = new StringBuilder();
		ArrayList<ReviewListElement> reviewListElements = new ArrayList<>();
		User user = null;
		String userType = null;

		try {
			reviewListElements = DBHelper.instance().showLeaveReviewPrompt(userID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < reviewListElements.size(); i++) {
			try {
				user = DBHelper.instance().getUserInfo(reviewListElements.get(i).getUserID());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (reviewListElements.get(i).getDriverOrPassenger()) {
				userType = "Driver";
			} else {
				userType = "Passenger";
			}
			returnString.append(String.format(div, reviewListElements.get(i).getUserID(), user.getProfilePicture(), user.getFirstName(), user.getLastName(), userType));
		}
		return returnString.toString();
	}


	private String createRatingsPage(int userID) {

		String div = "<div class=\"submitRating\">\n" +
				"        <div class=\"d-flex flex-column justify-content-center align-items-center container\" style=\"width: 100%%;height: 100%%;\">\n" +
				"            <div style=\"margin-top: 40px;\"><img src=\"images/%d\" width=\"100px\" height=\"100px\" style=\"object-fit: cover;border-radius: 50%%;\"></div>\n" +
				"            <div style=\"margin-top: 14px;\">\n" +
				"                <p style=\"font-size: 26px;font-family: Roboto, sans-serif;color: rgb(145,145,145);font-weight: 300;\">%s %s</p>\n" +
				"            </div>\n" +
				"            <div style=\"margin-top: 68px;font-size: 17px;\">\n" +
				"    <div class=\"col-lg-12\">\n" +
				"      <div class=\"star-rating\">\n" +
				"        <span class=\"fa fa-star-o\" data-rating=\"1\"></span>\n" +
				"        <span class=\"fa fa-star-o\" data-rating=\"2\"></span>\n" +
				"        <span class=\"fa fa-star-o\" data-rating=\"3\"></span>\n" +
				"        <span class=\"fa fa-star-o\" data-rating=\"4\"></span>\n" +
				"        <span class=\"fa fa-star-o\" data-rating=\"5\"></span>\n" +
				"        <input type=\"hidden\" name=\"whatever1\" id=\"rating-value\" class=\"rating-value\" value=\"2.56\">\n" +
				"      </div>\n" +
				"    </div>\n" +
				"\n" +
				"</div>\n" +
				"        </div>\n" +
				"        <div style=\"bottom: 0;position: absolute;width: 100%%;margin-bottom: 26px;height: 50px;\"><button class=\"btn btn-success\" type=\"button\" id=\"submitRatingButton\" style=\"width: 90%%;display: block;font-family: Roboto, sans-serif;font-size: 24px;font-weight: 300;margin-left: auto;margin-right: auto;\">Submit Rating</button></div>\n" +
				"        </div>";

		User user = null;
		try {
			user = DBHelper.instance().getUserInfo(userID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return String.format(div, user.getProfilePicture(), user.getFirstName(), user.getLastName());
	}


}

