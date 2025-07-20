package org.jsp.myFirst;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

public class RegisterServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String contact = req.getParameter("contact");
		String fullName = req.getParameter("fullName");
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		String email = null;
		Long mobileNumber = null;

		// Determine if input is mobile or email
		if (contact != null) {
			contact = contact.trim();

			// Check if it's a 10-digit number
			if (contact.matches("\\d{10}")) {
				mobileNumber = Long.parseLong(contact);
			}
			// Else if it's an email
			else if (contact.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
				email = contact;
			}
		}

		// If input is invalid (not mobile/email)
		if (mobileNumber == null && email == null) {
			resp.getWriter()
					.print("<h2 style='color:red;'>Please enter a valid 10-digit mobile number or email address.</h2>");
			return;
		}

		try {
			// DB connection
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=Gokul");

			// SQL insert
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO instagram.loginDetails (MobileNumber, FullName, UserName, password, email) VALUES (?, ?, ?, ?, ?)");

			// Set mobile/email accordingly
			if (mobileNumber != null) {
				ps.setLong(1, mobileNumber);
			} else {
				ps.setNull(1, Types.BIGINT);
			}

			ps.setString(2, fullName);
			ps.setString(3, username);
			ps.setString(4, password);

			if (email != null) {
				ps.setString(5, email);
			} else {
				ps.setNull(5, Types.VARCHAR);
			}

			int rows = ps.executeUpdate();

			if (rows > 0) {
				resp.sendRedirect("Login.html");
			} else {
				resp.getWriter().print("<h2 style='color:red;'>Registration failed. Try again.</h2>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().print("<h2 style='color:red;'>Error: " + e.getMessage() + "</h2>");
		}
	}
}
