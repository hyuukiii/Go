package main.java.Mypage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import User.UserDTO;

public class MypageDAO {

	String driver = "oracle.jdbc.driver.OracleDriver";
	String url = "jdbc:oracle:thin:@localhost:1521:testdb";
	String user = "scott";
	String password = "tiger";

	private Connection dbcon() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public UserDTO getUserInfo(String userId) {
		Connection con = dbcon();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UserDTO user = null;

		String sql = "SELECT * FROM users WHERE user_id = ?";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, userId);
			rs = pst.executeQuery();

			if (rs.next()) {
				user = new UserDTO();
				user.setUser_id(rs.getString("user_id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setPhone(rs.getString("phone"));
				user.setAddress(rs.getString("address"));
				user.setJoin_date(rs.getDate("join_date"));
				user.setProfile_image(rs.getString("profile_image"));
				user.setRating(rs.getDouble("rating"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (pst != null)
					pst.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return user;
	}

}
