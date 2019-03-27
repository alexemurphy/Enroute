package main.servlet.servletClasses;

import utils.Authentication;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {

		// get request parameters for userID and password
		String user = request.getParameter("username");
		String pwd = request.getParameter("password");
		if (Authentication.authenticateWithPass(user, pwd)) {
			HttpSession session = request.getSession();
			String sessionKey = Authentication.genNewSessionKey();
			session.setAttribute("username", user);
			session.setAttribute("sessionToken", sessionKey);
			//setting session to expiry in 30 mins
			session.setMaxInactiveInterval(30 * 60);
			Cookie sessionToken = new Cookie("sessionToken", sessionKey);
			sessionToken.setMaxAge(30 * 60);
			response.addCookie(sessionToken);
			response.sendRedirect("mapMainPage.jsp");
		} else {
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
			PrintWriter out = response.getWriter();
			out.println("<script>alert(\"Either user name or password is wrong.\")</script>" +
					"");
			rd.include(request, response);
		}

	}

}
