package FindIdPw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindIdDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:xe";
	String user = "scott";
	String password = "tiger";

	public Connection dbcon() {
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return con;
	}
	
	public String findUserId(String username, String email, String phone) {
	    String userId = null;
	    String sql = "SELECT user_id FROM users WHERE username = ? AND email = ? AND phone = ?";
	    
	    try (Connection con = dbcon();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, email);
	        pstmt.setString(3, phone);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            userId = rs.getString("user_id");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return userId;
	}
 
}
