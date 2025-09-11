package Product;

import java.sql.*;

public class FavoriteDAO {

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

	public boolean isFavorited(String userId, int productId) {
		String sql = "SELECT 1 FROM Favorites WHERE user_id = ? AND product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, userId);
			pst.setInt(2, productId);
			ResultSet rs = pst.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void insertFavorite(String userId, int productId) {
		String sql = "INSERT INTO Favorites (favorite_id, user_id, product_id, favorite_date) "
				+ "VALUES (favorite_seq.NEXTVAL, ?, ?, SYSDATE)";

		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, userId);
			pst.setInt(2, productId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteFavorite(String userId, int productId) {
		String sql = "DELETE FROM Favorites WHERE user_id = ? AND product_id = ?";
		try (Connection con = dbcon(); PreparedStatement pst = con.prepareStatement(sql)) {

			pst.setString(1, userId);
			pst.setInt(2, productId);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ProductRankingServlet에서 사용하는 메서드 추가
	public boolean addFavorite(String userId, int productId) {
		// 이미 즐겨찾기에 있는지 확인
		if (isFavorited(userId, productId)) {
			return true; // 이미 즐겨찾기에 있으면 성공으로 간주
		}

		try {
			insertFavorite(userId, productId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeFavorite(String userId, int productId) {
		// 즐겨찾기에 없으면 삭제할 필요 없음
		if (!isFavorited(userId, productId)) {
			return true; // 이미 즐겨찾기에 없으면 성공으로 간주
		}

		try {
			deleteFavorite(userId, productId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}