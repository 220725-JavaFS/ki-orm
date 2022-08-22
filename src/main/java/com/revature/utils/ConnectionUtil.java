package com.revature.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

	private static Connection connection;

	public static Connection getConnection() throws SQLException {
		if (connection != null && !connection.isClosed())
			return connection;
		else {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			String url = "jdbc:postgresql://javafs-220725.cmfrqggyvwnv.us-west-2.rds.amazonaws.com:5432/postgres";
			String uname = "postgres";
			String pass = "password";

			connection = DriverManager.getConnection(url, uname, pass);
			return connection;
		}
	}
}
