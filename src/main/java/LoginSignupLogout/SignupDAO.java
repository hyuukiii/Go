package LoginSignupLogout;

import java.sql.*;
import java.util.Date;
import User.UserDTO;

public class SignupDAO {
	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:testdb";
	String user = "scott";
	String password = "tiger";

	public Connection dbcon() {
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public int insertUser(UserDTO dto) {
		int result = 0;
		String sql = "INSERT INTO users (user_id, username, email, password, phone, address, join_date, profile_image, rating) "
				+ "VALUES (?, ?, ?, ?, ?, ?, SYSDATE, ?, ?)";

		try (Connection con = dbcon(); PreparedStatement pstmt = con.prepareStatement(sql)) {
			pstmt.setString(1, dto.getUser_id());
			pstmt.setString(2, dto.getUsername());
			pstmt.setString(3, dto.getEmail());
			pstmt.setString(4, dto.getPassword());
			pstmt.setString(5, dto.getPhone());
			pstmt.setString(6, dto.getAddress());
			pstmt.setString(7, dto.getProfile_image());
			pstmt.setDouble(8, dto.getRating());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}