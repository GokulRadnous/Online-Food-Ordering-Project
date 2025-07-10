package org.jsp.myFirst;

import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class MyFirstServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String input = req.getParameter("nm"); // username, email, or phone
		String ps = req.getParameter("ps"); // password

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=Gokul");

			// First: check if username/email/mobile exists
			String checkUserSql = "SELECT * FROM instagram.loginDetails WHERE UserName = ? OR email = ? OR MobileNumber = ?";
			pstmt = con.prepareStatement(checkUserSql);

			long mobile = 0;
			try {
				mobile = Long.parseLong(input);
			} catch (NumberFormatException e) {
				mobile = 0;
			}

			pstmt.setString(1, input); // username
			pstmt.setString(2, input); // email
			pstmt.setLong(3, mobile); // mobile

			rs = pstmt.executeQuery();

			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();

			String logo = "<img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Instagram_logo.svg/1280px-Instagram_logo.svg.png\" style=\"height: 57px; width: 191px; padding: 0px 70px;\" alt=\"\">";

			if (rs.next()) {
				String storedPassword = rs.getString("password");

				if (storedPassword.equals(ps)) {
					String user = rs.getString("UserName");
					out.println(
							"<html><body style='text-align:center; background: linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888);'>");
					out.println(logo);
					out.println(
							"<marquee behavior='alternate' scrollamount='10'><h1 style='color: white; font-family: Arial;'> Welcome........ <i>"
									+ user + "</i>! You're successfully logged in. </h1></marquee>");
					out.println("</body></html>");
				} else {
					out.println("<html><body align='center' bgcolor='orange'>");
					out.println(logo);
					out.println(
							"<h1 style='color: darkred; font-family: Courier;'> Incorrect password. Please try again! </h1>");
					out.println("</body></html>");
				}
			} else {
				out.println("<html><body align='center' bgcolor='red'>");
				out.println(logo);
				out.println(
						"<h1 style='color: yellow; font-family: Verdana;'> Username, Email or Mobile number not found. Please sign up! </h1>");
				out.println("</body></html>");
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
