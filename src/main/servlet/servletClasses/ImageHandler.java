package main.servlet.servletClasses;

import main.db.DBHelper;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/images/*")
public class ImageHandler extends HttpServlet {


	// content=blob, name=varchar(255) UNIQUE.
	private static final String SQL_FIND = "SELECT IMAGE FROM IMAGES WHERE ID = ?";


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String imageName = request.getPathInfo().substring(1); // Returns "foo.png".
		try {
			Connection connection = DBHelper.instance().getConnection();
			PreparedStatement statement = connection.prepareStatement(SQL_FIND);
			statement.setString(1, imageName);
			try {
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					byte[] content = resultSet.getBytes("IMAGE");
					response.setContentType(getServletContext().getMimeType(imageName));
					response.setContentLength(content.length);
					response.getOutputStream().write(content);
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
