package org.jsp.myFirst;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String input = req.getParameter("nm");       // Mobile or Email
        String password = req.getParameter("ps");
        String fullName = req.getParameter("fn");
        String userName = req.getParameter("un");

        Connection con = null;
        PreparedStatement checkStmt = null, insertStmt = null;
        ResultSet rs = null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=Gokul");

            // Check if MobileNumber, email or UserName already exists
            String checkQuery = "SELECT * FROM instagram.loginDetails WHERE UserName=? OR email=? OR MobileNumber=?";
            checkStmt = con.prepareStatement(checkQuery);

            long mobile = 0;
            boolean isMobile = false;
            try {
                mobile = Long.parseLong(input);
                isMobile = true;
            } catch (NumberFormatException e) {
                // Not a number, so treat as email
            }

            checkStmt.setString(1, userName);
            checkStmt.setString(2, isMobile ? "" : input);
            checkStmt.setLong(3, isMobile ? mobile : 0);

            rs = checkStmt.executeQuery();

            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            String logo = "<img src='https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Instagram_logo.svg/1280px-Instagram_logo.svg.png' style='height: 57px; width: 191px; padding: 0px 70px;' alt=''>";

            if (rs.next()) {
                out.println("<html><body align='center' bgcolor='orange'>");
                out.println(logo);
                out.println("<h2 style='color:red'>User already exists with same Username, Email or Mobile Number.</h2>");
                out.println("</body></html>");
            } else {
                // Insert new user
                String insertQuery = "INSERT INTO instagram.loginDetails (MobileNumber, FullName, UserName, password, email) VALUES (?, ?, ?, ?, ?)";
                insertStmt = con.prepareStatement(insertQuery);

                if (isMobile) {
                    insertStmt.setLong(1, mobile); // MobileNumber
                    insertStmt.setString(5, "");   // email empty
                } else {
                    insertStmt.setLong(1, 0);      // MobileNumber empty
                    insertStmt.setString(5, input); // email
                }

                insertStmt.setString(2, fullName);
                insertStmt.setString(3, userName);
                insertStmt.setString(4, password);

                int rows = insertStmt.executeUpdate();
                out.println("<html><body align='center' bgcolor='lightgreen'>");
                out.println(logo);
                if (rows > 0) {
                    out.println("<h2 style='color:green'>Successfully registered! Welcome to Instagram, " + userName + " 🎉</h2>");
                } else {
                    out.println("<h2 style='color:red'>Registration failed. Please try again later.</h2>");
                }
                out.println("</body></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (checkStmt != null) checkStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (insertStmt != null) insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
