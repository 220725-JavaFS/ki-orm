package com.revature.orm.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

	private static Connection connection;

	public static Connection getConnection(Conf config) throws SQLException {
		if (connection != null && !connection.isClosed())
			return connection;
		else {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			connection = DriverManager.getConnection(config.getUrl(), config.getUname(), config.getPass());
			return connection;
		}
	}
}
