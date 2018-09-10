package webcam;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {

	public static String DRIVERNAME = null;
	public static String URL = null;
	public static String USER = null;
	public static String PASSWORD = null;

	public static Connection conn = null;

	static {
		try {

			DRIVERNAME = Utils.getPropertyValue(DBUtils.class, "config.properties", "dbDriver");
			URL = Utils.getPropertyValue(DBUtils.class, "config.properties", "dbUrl");
			USER = Utils.getPropertyValue(DBUtils.class, "config.properties", "dbName");
			PASSWORD = Utils.getPropertyValue(DBUtils.class, "config.properties", "dbPwd");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection() throws Exception {
		if (conn != null) {
			return conn;
		}
		Class.forName(DRIVERNAME);
		conn = DriverManager.getConnection(URL, USER, PASSWORD);

		return conn;
	}

	public static void closeResource(Connection conn) throws SQLException {
		conn.close();
		if(conn != null){
			conn = null;
		}
	}
	
	public static void closeResource(Connection conn, PreparedStatement ps) throws SQLException {
		ps.close();
		conn.close();
	}

	public static void closeResource(ResultSet rs, PreparedStatement ps) throws SQLException {
		ps.close();
		rs.close();
	}
	
	public static void closeResource(Connection conn, ResultSet rs, PreparedStatement ps) throws SQLException {
		ps.close();
		rs.close();
		conn.close();
	}
}