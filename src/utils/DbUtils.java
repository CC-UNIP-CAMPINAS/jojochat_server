package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbUtils {

public static Connection conn = null;
	
	public static Connection getConnection(){
		if(conn == null) {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				try {
					conn = DriverManager.getConnection(url, props);
				} catch (SQLException e) {
					e.printStackTrace();
				}	
		}
		return conn;
	}
	
	public static void closeConnection() {
		if(conn != null) {
			try {
				conn.close();
				conn = null;	
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Properties loadProperties() {
		String caminho = System.getProperty("user.home")+File.separatorChar+"Documents"+File.separatorChar+"JOJO_DATA"+ File.separatorChar+"db.properties";
		if (IdentificadorSoUtils.sistema().equals("linux")){
				caminho = System.getProperty("user.home")+File.separatorChar+"Documents"+File.separatorChar+"JOJO_DATA"+ File.separatorChar+"db.properties";
		}
		try(FileInputStream fs = new FileInputStream(caminho)){
			Properties props = new Properties();
			props.load(fs);
			return props;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void fechaStatement(Statement st) {
		if(st != null) {
			try {
				st.close();
				st = null;
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void fechaResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
				rs = null;
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
