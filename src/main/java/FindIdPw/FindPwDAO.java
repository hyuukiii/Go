package FindIdPw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindPwDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:testdb";
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
	
	public String findPassword(String userId, String username, String email, String phone) {
		String password = null;
		String sql = "SELECT password FROM users WHERE user_id=? AND username=? AND email=? AND phone=?";

		try (Connection con = dbcon();
			 PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, userId);
			pstmt.setString(2, username);
			pstmt.setString(3, email);
			pstmt.setString(4, phone);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				password = rs.getString("password");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}
	
}
